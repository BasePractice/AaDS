package ru.mifi.practice.voln.polling;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

//TODO: Переписать https://github.com/reactor/reactor-netty
@Slf4j
public final class PollingHttpServer {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final int port;
    private final EventService eventService;
    private HttpServer server;

    public PollingHttpServer(int port, EventService eventService) {
        this.port = port;
        this.eventService = eventService;
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/events", this::handleEvents);
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    public int getPort() {
        return server.getAddress().getPort();
    }

    private void handleEvents(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        if ("GET".equalsIgnoreCase(method)) {
            handleGetEvents(exchange);
        } else if ("POST".equalsIgnoreCase(method)) {
            handlePostEvent(exchange);
        } else {
            exchange.sendResponseHeaders(405, -1);
            exchange.close();
        }
    }

    private void handleGetEvents(HttpExchange exchange) throws IOException {
        Map<String, String> params = parseQueryParams(exchange.getRequestURI());
        long lastOffset = Long.parseLong(params.getOrDefault("lastOffset", "0"));
        long timeout = Long.parseLong(params.getOrDefault("timeout", "30"));

        eventService.getEvents(lastOffset, timeout)
            .collectList()
            .subscribe(events -> {
                try {
                    byte[] response = MAPPER.writeValueAsBytes(events);
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(200, response.length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response);
                    }
                } catch (IOException expected) {
                    try {
                        exchange.sendResponseHeaders(500, -1);
                    } catch (IOException expectedInner) {
                        if (log.isErrorEnabled()) {
                            log.error("Error sending response", expectedInner);
                        }
                    }
                } finally {
                    exchange.close();
                }
            }, throwable -> {
                try {
                    exchange.sendResponseHeaders(500, -1);
                } catch (IOException expected) {
                    if (log.isErrorEnabled()) {
                        log.error("Error sending response", expected);
                    }
                } finally {
                    exchange.close();
                }
            });
    }

    private void handlePostEvent(HttpExchange exchange) throws IOException {
        try {
            Event.Data data = MAPPER.readValue(exchange.getRequestBody(), Event.Data.class);
            eventService.addEvent(data);
            exchange.sendResponseHeaders(200, -1);
        } catch (IOException expected) {
            exchange.sendResponseHeaders(400, -1);
        } finally {
            exchange.close();
        }
    }

    private Map<String, String> parseQueryParams(URI uri) {
        Map<String, String> result = new HashMap<>();
        String query = uri.getQuery();
        if (query != null) {
            for (String param : query.split("&")) {
                String[] pair = param.split("=");
                if (pair.length > 1) {
                    result.put(pair[0], pair[1]);
                } else {
                    result.put(pair[0], "");
                }
            }
        }
        return result;
    }
}
