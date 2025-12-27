package ru.mifi.practice.voln.event;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
public record Event(long streamId, long offset, LocalDateTime createdAt, Data data, Type type, Object source, Object target) {

    public Event(long id, long offset, Data data) {
        this(id, offset, LocalDateTime.now(), data, Type.PUBLIC, null, null);
    }

    public enum Type {
        PRIVATE,
        PUBLIC
    }

    public record Data(Object message) {
    }
}
