package ru.mifi.practice.voln.domain;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Комната сетевого боя: два слота, зерно расстановки и каналы событий обеих сторон.
 *
 * <p>Правил боя комната не знает и знать не должна: она сводит двоих, раздаёт им общее зерно и
 * дальше переправляет строки от одной стороны к другой. Поэтому изменение правил игры не требует
 * ни строчки правок на сервере.
 */
public final class Room {
    private final UUID id = UUID.randomUUID();
    private final long seed = ThreadLocalRandom.current().nextLong();
    private final Map<Boolean, String> players = new ConcurrentHashMap<>();
    private final Map<Boolean, Channel> channels = new ConcurrentHashMap<>();

    public UUID id() {
        return id;
    }

    public long seed() {
        return seed;
    }

    /** Есть ли в комнате свободный слот. */
    public boolean vacant() {
        return players.size() < 2;
    }

    /** Занять свободный слот и вернуть доставшуюся сторону. */
    public boolean take(String nickname) {
        boolean left = !players.containsKey(Boolean.TRUE);
        players.put(left, nickname);
        return left;
    }

    public String player(boolean left) {
        return players.getOrDefault(left, "соперник");
    }

    /** Привязать к стороне канал событий. */
    public void attach(boolean left, Channel channel) {
        channels.put(left, channel);
    }

    /** Обе стороны на местах и слушают. */
    public boolean ready() {
        return channels.size() == 2;
    }

    /** Отправить строку одной стороне; молчащий слот просто пропускается. */
    public void send(boolean left, String data) {
        Channel channel = channels.get(left);
        if (channel != null) {
            channel.send(data);
        }
    }

    /** Отправить строку обеим сторонам. */
    public void broadcast(String data) {
        send(true, data);
        send(false, data);
    }

    /** Отцепить сторону; комната считается брошенной, когда каналов не осталось. */
    public boolean detach(boolean left) {
        channels.remove(left);
        players.remove(left);
        return channels.isEmpty();
    }
}
