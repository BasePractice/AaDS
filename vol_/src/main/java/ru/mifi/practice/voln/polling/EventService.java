package ru.mifi.practice.voln.polling;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;

public interface EventService {

    void addEvent(Event.Data event);

    Flux<Event> getEvents(Long lastOffset, long timeoutSeconds);

    final class Default implements EventService {
        private final ConcurrentNavigableMap<Long, Event> eventBuffer = new ConcurrentSkipListMap<>();
        private final Sinks.Many<Event> eventSink = Sinks.many().multicast().onBackpressureBuffer(1000);
        private final AtomicLong eventId = new AtomicLong(1);
        private final long bufferSize;

        public Default(long bufferSize) {
            this.bufferSize = bufferSize;
        }

        @Override
        public synchronized void addEvent(Event.Data data) {
            long id = eventId.getAndIncrement();
            var event = new Event(id, data);
            eventBuffer.put(event.id(), event);
            if (eventBuffer.size() > bufferSize) {
                eventBuffer.pollFirstEntry();
            }
            eventSink.tryEmitNext(event);
        }

        @Override
        public Flux<Event> getEvents(Long lastOffset, long timeoutSeconds) {
            return Flux.defer(() -> {
                Flux<Event> historicalEvents = getHistoricalEvents(lastOffset);
                Flux<Event> newEvents = eventSink.asFlux()
                    .filter(event -> event.id() > (lastOffset != null ? lastOffset : 0))
                    .timeout(Duration.ofSeconds(timeoutSeconds))
                    .onErrorResume(e -> Flux.empty());

                return Flux.concat(historicalEvents, newEvents);
            });
        }

        private Flux<Event> getHistoricalEvents(Long lastOffset) {
            return Flux.fromIterable(() -> {
                Long startKey = lastOffset != null ? lastOffset + 1 : 1L;
                return eventBuffer.tailMap(startKey).values().iterator();
            });
        }
    }
}
