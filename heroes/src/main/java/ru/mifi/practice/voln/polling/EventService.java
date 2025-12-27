package ru.mifi.practice.voln.polling;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.SneakyThrows;
import org.jspecify.annotations.NonNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import ru.mifi.practice.voln.event.Event;
import ru.mifi.practice.voln.event.EventSequence;
import ru.mifi.practice.voln.event.EventStream;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Objects;

public interface EventService {

    long DEFAULT_STREAM_ID = 0L;

    void addEvent(Event.Data data);

    void addEvent(long streamId, Event.Data event);

    EventStream newEventStream();

    Flux<Event> getEvents(Long lastOffset, long timeoutSeconds);

    Flux<Event> getEvents(long streamId, Long lastOffset, long timeoutSeconds);

    Flux<Event> getEventStream(long lastOffset);

    Flux<Event> getEventStream(long streamId, long lastOffset);

    final class Default implements EventService {
        private final EventSequence eventSequence = new EventSequence.Default();
        private final Cache<Long, EventStream> eventCache = CacheBuilder.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(30))
            .recordStats()
            .build();
        private final Sinks.Many<Event> eventSink = Sinks.many().multicast().onBackpressureBuffer(1000);

        @Override
        public void addEvent(Event.Data data) {
            addEvent(DEFAULT_STREAM_ID, data);
        }

        @Override
        public synchronized void addEvent(long streamId, Event.Data data) {
            var stream = findOrCreateStream(streamId);
            var event = stream.add(data, Event.Type.PUBLIC, null, null);
            eventSink.tryEmitNext(event);
        }

        @Override
        public EventStream newEventStream() {
            return new EventStream.Default(eventSequence.next(), new ArrayList<>());
        }

        @SneakyThrows
        private @NonNull EventStream findOrCreateStream(long streamId) {
            return eventCache.get(streamId, () -> new EventStream.Default(streamId, new ArrayList<>()));
        }

        @Override
        public Flux<Event> getEvents(Long lastOffset, long timeoutSeconds) {
            return getEvents(DEFAULT_STREAM_ID, lastOffset, timeoutSeconds);
        }

        @Override
        public Flux<Event> getEvents(long streamId, Long lastOffset, long timeoutSeconds) {
            long offset = Objects.requireNonNullElse(lastOffset, 0L);
            return getHistoricalEvents(findOrCreateStream(streamId), offset)
                .switchIfEmpty(Mono.defer(() -> eventSink.asFlux()
                    .filter(event -> event.streamId() == streamId && event.offset() >= offset)
                    .next()
                    .timeout(Duration.ofSeconds(timeoutSeconds), Mono.empty())));
        }

        @Override
        public Flux<Event> getEventStream(long lastOffset) {
            return getEventStream(DEFAULT_STREAM_ID, lastOffset);
        }

        @Override
        public Flux<Event> getEventStream(long streamId, long lastOffset) {
            return getHistoricalEvents(findOrCreateStream(streamId), lastOffset)
                .concatWith(eventSink.asFlux().filter(event -> event.streamId() == streamId && event.offset() >= lastOffset));
        }

        private Flux<Event> getHistoricalEvents(EventStream stream, long lastOffset) {
            return Flux.fromStream(stream.getEvents(lastOffset, Event.Type.PUBLIC));
        }
    }
}
