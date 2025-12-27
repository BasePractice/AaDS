package ru.mifi.practice.voln.polling;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.netty.handler.codec.http.QueryStringDecoder;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;
import ru.mifi.practice.voln.event.Event;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public final class EventHttpServer {

    private static final ObjectMapper MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());
    private final int port;
    private final EventService eventService;
    private final TransportMode transportMode;
    private DisposableServer server;

    public EventHttpServer(int port, EventService eventService) {
        this(port, eventService, TransportMode.LONG_POLLING);
    }

    public EventHttpServer(int port, EventService eventService, TransportMode transportMode) {
        this.port = port;
        this.eventService = eventService;
        this.transportMode = transportMode;
    }

    public void start() {
        server = HttpServer.create()
            .port(port)
            .route(routes -> routes
                .get("/events", this::handleGetEvents)
                .post("/events", this::handlePostEvent)
                .post("/streams", this::handleCreateStream)
                .get("/streams/{id}/events", this::handleGetStreamEvents)
                .post("/streams/{id}/events", this::handlePostStreamEvent))
            .bindNow();
        if (log.isInfoEnabled()) {
            log.info("Server started on port {}", server.port());
        }
    }

    public void stop() {
        if (server != null) {
            server.disposeNow();
        }
    }

    public int getPort() {
        return server.port();
    }

    private Mono<Void> handleGetEvents(HttpServerRequest request, HttpServerResponse response) {
        Map<String, String> params = parseQueryParams(request.uri());
        long lastOffset = Long.parseLong(params.getOrDefault("last-offset", "0"));

        if (transportMode == TransportMode.SSE) {
            return handleSseEvents(response, EventService.DEFAULT_STREAM_ID, lastOffset);
        } else {
            return handleLongPollingEvents(response, EventService.DEFAULT_STREAM_ID, lastOffset, params);
        }
    }

    private Mono<Void> handleGetStreamEvents(HttpServerRequest request, HttpServerResponse response) {
        long streamId = Long.parseLong(Objects.requireNonNull(request.param("id")));
        Map<String, String> params = parseQueryParams(request.uri());
        long lastOffset = Long.parseLong(params.getOrDefault("last-offset", "0"));

        if (transportMode == TransportMode.SSE) {
            return handleSseEvents(response, streamId, lastOffset);
        } else {
            return handleLongPollingEvents(response, streamId, lastOffset, params);
        }
    }

    private Mono<Void> handleLongPollingEvents(HttpServerResponse response, long streamId, long lastOffset, Map<String, String> params) {
        long timeout = Long.parseLong(params.getOrDefault("timeout", "30"));
        return eventService.getEvents(streamId, lastOffset, timeout)
            .collectList()
            .flatMap(events -> {
                try {
                    byte[] bytes = MAPPER.writeValueAsBytes(events);
                    return response.header("Content-Type", "application/json")
                        .sendByteArray(Mono.just(bytes))
                        .then();
                } catch (JsonProcessingException e) {
                    return response.status(500).send();
                }
            });
    }

    private Mono<Void> handleSseEvents(HttpServerResponse response, long streamId, long lastOffset) {
        return response.header("Content-Type", "text/event-stream")
            .header("Cache-Control", "no-cache")
            .header("Connection", "keep-alive")
            .sendString(eventService.getEventStream(streamId, lastOffset)
                .map(event -> {
                    try {
                        return "data: " + MAPPER.writeValueAsString(event) + "\n\n";
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }))
            .then();
    }

    @SuppressWarnings("PMD.UnusedFormalParameter")
    private Mono<Void> handleCreateStream(HttpServerRequest request, HttpServerResponse response) {
        return Mono.fromCallable(eventService::newEventStream)
            .flatMap(stream -> response.status(201)
                .header("Content-Type", "application/json")
                .sendString(Mono.just("{\"id\":" + stream.id() + "}"))
                .then());
    }

    private Mono<Void> handlePostEvent(HttpServerRequest request, HttpServerResponse response) {
        return handlePostStreamEvent(request, response, EventService.DEFAULT_STREAM_ID);
    }

    private Mono<Void> handlePostStreamEvent(HttpServerRequest request, HttpServerResponse response) {
        long streamId = Long.parseLong(Objects.requireNonNull(request.param("id")));
        return handlePostStreamEvent(request, response, streamId);
    }

    private Mono<Void> handlePostStreamEvent(HttpServerRequest request, HttpServerResponse response, long streamId) {
        return request.receive()
            .aggregate()
            .asByteArray()
            .flatMap(bytes -> {
                try {
                    Event.Data data = MAPPER.readValue(bytes, Event.Data.class);
                    eventService.addEvent(streamId, data);
                    return response.status(201).send();
                } catch (Exception e) {
                    return response.status(400).send();
                }
            });
    }

    private Map<String, String> parseQueryParams(String uri) {
        QueryStringDecoder decoder = new QueryStringDecoder(uri);
        return decoder.parameters().entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> e.getValue().isEmpty() ? "" : e.getValue().get(0),
                (v1, v2) -> v1
            ));
    }

    public enum TransportMode {
        LONG_POLLING,
        SSE
    }
}
