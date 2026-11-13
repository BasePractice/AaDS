package ru.mifi.practice.vol92.patterns;

/** Неизменяемое сообщение: все поля final, состояние заморожено после конструктора. */
public final class ImmutableMessage {
    private final String sender;
    private final String body;
    private final long stamp;

    public ImmutableMessage(String sender, String body, long stamp) {
        this.sender = sender;
        this.body = body;
        this.stamp = stamp;
    }

    public String sender() {
        return sender;
    }

    public String body() {
        return body;
    }

    public long stamp() {
        return stamp;
    }
}
