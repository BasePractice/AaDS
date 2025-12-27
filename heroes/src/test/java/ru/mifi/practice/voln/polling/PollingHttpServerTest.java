package ru.mifi.practice.voln.polling;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PollingHttpServerTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private PollingHttpServer server;
    private EventService eventService;
    private HttpClient client;
    private int port;

    @BeforeEach
    void setUp() throws Exception {
        eventService = new EventService.Default(100);
        server = new PollingHttpServer(0, eventService);
        server.start();
        port = server.getPort();
        client = HttpClient.newHttpClient();
    }

    @AfterEach
    void tearDown() {
        server.stop();
    }

    @Test
    void testPostAndGetEvents() throws Exception {
        String json = "{\"message\":\"hello\"}";
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/events"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, postResponse.statusCode());

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/events?lastOffset=0"))
                .GET()
                .build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getResponse.statusCode());

        List<Event> events = MAPPER.readValue(getResponse.body(), new TypeReference<List<Event>>() {});
        assertEquals(1, events.size());
        assertEquals("hello", events.get(0).data().message());
    }

    @Test
    void testLongPolling() throws Exception {
        CompletableFuture<HttpResponse<String>> futureResponse = client.sendAsync(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:" + port + "/events?lastOffset=0&timeout=2"))
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString());

        TimeUnit.MILLISECONDS.sleep(500);

        String json = "{\"message\":\"late event\"}";
        client.send(HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/events"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build(), HttpResponse.BodyHandlers.ofString());

        HttpResponse<String> response = futureResponse.get(5, TimeUnit.SECONDS);
        assertEquals(200, response.statusCode());
        List<Event> events = MAPPER.readValue(response.body(), new TypeReference<List<Event>>() {});
        assertEquals(1, events.size());
        assertEquals("late event", events.get(0).data().message());
    }

    @Test
    void testTimeout() throws Exception {
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/events?lastOffset=0&timeout=1"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        List<Event> events = MAPPER.readValue(response.body(), new TypeReference<List<Event>>() {});
        assertEquals(0, events.size());
    }

    @Test
    void testSse() throws Exception {
        EventService sseEventService = new EventService.Default(100);
        PollingHttpServer sseServer = new PollingHttpServer(0, sseEventService, PollingHttpServer.TransportMode.SSE);
        sseServer.start();
        int ssePort = sseServer.getPort();
        try {
            List<String> receivedData = new ArrayList<>();
            CompletableFuture<Void> future = new CompletableFuture<>();

            CompletableFuture.runAsync(() -> {
                try {
                    URL url = new URL("http://localhost:" + ssePort + "/events");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");

                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            if (line.startsWith("data: ")) {
                                receivedData.add(line);
                                if (receivedData.size() == 2) {
                                    future.complete(null);
                                    break;
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    future.completeExceptionally(e);
                }
            });

            TimeUnit.MILLISECONDS.sleep(500);

            sseEventService.addEvent(new Event.Data("sse1"));
            sseEventService.addEvent(new Event.Data("sse2"));

            future.get(5, TimeUnit.SECONDS);

            assertEquals(2, receivedData.size());
        } finally {
            sseServer.stop();
        }
    }
}
