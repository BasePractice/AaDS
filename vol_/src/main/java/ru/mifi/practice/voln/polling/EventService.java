package ru.mifi.practice.voln.polling;

import java.time.Duration;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

public interface EventService {

    void addEvent(Event.Data event);

    Flux<Event> getEvents(Long lastOffset, long timeoutSeconds);

    final class Default implements EventService {
        private final ConcurrentNavigableMap<Long, Event> eventBuffer = new ConcurrentSkipListMap<>();
        private final Sinks.Many<Event> eventSink = Sinks.many().multicast().onBackpressureBuffer(1000);
        private final AtomicLong eventId = new AtomicLong(1);
        private final long bufferSize;
        private final AtomicLong currentSize = new AtomicLong(0);

        public Default(long bufferSize) {
            this.bufferSize = bufferSize;
        }

        @Override
        public synchronized void addEvent(Event.Data data) {
            long id = eventId.getAndIncrement();
            Event event = new Event(id, data);
            eventBuffer.put(event.id(), event);
            if (currentSize.incrementAndGet() > bufferSize) {
                eventBuffer.pollFirstEntry();
                currentSize.decrementAndGet();
            }
            eventSink.tryEmitNext(event);
        }

        @Override
        public Flux<Event> getEvents(Long lastOffset, long timeoutSeconds) {
            long offset = lastOffset != null ? lastOffset : 0L;
            return getHistoricalEvents(offset)
                    .switchIfEmpty(Mono.defer(() -> eventSink.asFlux()
                            .filter(event -> event.id() > offset)
                            .next()
                            .timeout(Duration.ofSeconds(timeoutSeconds), Mono.empty())));
        }

        private Flux<Event> getHistoricalEvents(long lastOffset) {
            return Flux.fromIterable(eventBuffer.tailMap(lastOffset + 1).values());
        }
    }
}
