package ru.mifi.practice.voln.heroes.net;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Канал к серверу боёв: занять слот в комнате, слать ходы и реплики, слушать ответы соперника.
 *
 * <p>Поток событий приходит по Server-Sent Events, то есть обычным долгим GET, у которого тело
 * дописывается строками. Отдельного протокола поверх сокета не нужно: хватает того, что уже
 * умеет сервер чата.
 */
public interface Remote extends AutoCloseable {

    /** Занять свободный слот или создать комнату и ждать соперника. */
    Seat join(String nickname);

    /** Отправить сообщение сопернику. */
    void send(Message message);

    /** Подписаться на поток событий комнаты. */
    void listen(Consumer<Message> sink);

    @Override
    void close();

    /** Место в комнате: сама комната, наша сторона и зерно расстановки. */
    record Seat(UUID room, boolean left, long seed) {
    }

    /** Канал поверх HTTP: обычный клиент JDK без сторонних библиотек. */
    final class Http implements Remote {
        private final URI address;
        private final HttpClient client;
        private Seat seat;
        private Thread listener;

        public Http(URI address) {
            this.address = address;
            this.client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
        }

        @Override
        public Seat join(String nickname) {
            String answer = text(HttpRequest.newBuilder()
                .uri(address.resolve("/api/rooms/join?nickname=" + encoded(nickname)))
                .timeout(Duration.ofSeconds(10))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build());
            String[] parts = answer.trim().split(";");
            if (parts.length != 4 || !"ROOM".equals(parts[0])) {
                throw new IllegalStateException("Сервер ответил не описанием комнаты: " + answer);
            }
            seat = new Seat(UUID.fromString(parts[1]), Boolean.parseBoolean(parts[2]), Long.parseLong(parts[3]));
            return seat;
        }

        @Override
        public void send(Message message) {
            text(HttpRequest.newBuilder()
                .uri(address.resolve("/api/rooms/" + seat.room() + "/message?left=" + seat.left()))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "text/plain; charset=utf-8")
                .POST(HttpRequest.BodyPublishers.ofString(message.encode(), StandardCharsets.UTF_8))
                .build());
        }

        @Override
        public void listen(Consumer<Message> sink) {
            listener = new Thread(() -> stream(sink), "heroes-room");
            listener.setDaemon(true);
            listener.start();
        }

        @Override
        public void close() {
            if (listener != null) {
                listener.interrupt();
            }
        }

        /**
         * Читает поток событий построчно. В Server-Sent Events полезная нагрузка идёт строками
         * «data: …», остальные строки — имя события и разделители — на разбор не влияют.
         */
        private void stream(Consumer<Message> sink) {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(address.resolve("/api/rooms/" + seat.room() + "/stream?left=" + seat.left()))
                .timeout(Duration.ofHours(1))
                .GET()
                .build();
            try {
                client.send(request, HttpResponse.BodyHandlers.ofLines()).body()
                    .filter(line -> line.startsWith("data:"))
                    .map(line -> line.substring("data:".length()).trim())
                    .filter(line -> !line.isEmpty())
                    .forEach(line -> sink.accept(Message.decode(line)));
            } catch (IOException e) {
                throw new IllegalStateException("Поток комнаты оборвался: " + e.getMessage(), e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        private String text(HttpRequest request) {
            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() >= 400) {
                    throw new IllegalStateException("Сервер отказал с кодом " + response.statusCode());
                }
                return response.body();
            } catch (IOException e) {
                throw new IllegalStateException("Сервер недоступен: " + e.getMessage(), e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Обращение к серверу прервано", e);
            }
        }

        private String encoded(String value) {
            return java.net.URLEncoder.encode(value, StandardCharsets.UTF_8);
        }
    }
}
