package ru.mifi.practice.voln.event;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

/** Поток событий с добавлением и выборкой по смещению. */
public interface EventStream {
    long id();

    Event add(Event.Data data, Event.Type type, Object source, Object target);

    long size(long offset);

    Stream<Event> getEvents(long offset, Event.Type type);

    default Stream<Event> getEvents(long offset) {
        return getEvents(offset, Event.Type.PUBLIC);
    }

    @Builder(toBuilder = true)
    record Default(long id, List<Event> events) implements EventStream {
        @Override
        public synchronized Event add(Event.Data data, Event.Type type, Object source, Object target) {
            Event event = Event.builder()
                .streamId(id)
                .offset(events.size())
                .createdAt(LocalDateTime.now())
                .data(data)
                .type(type)
                .source(source)
                .target(target)
                .build();
            events.add(event);
            return event;
        }

        @Override
        public long size(long offset) {
            return Math.max(0, (long) events.size() - offset);
        }

        @Override
        public Stream<Event> getEvents(long offset, Event.Type type) {
            return events.stream()
                .filter(event -> event.offset() >= offset && event.type() == type);
        }
    }
}
