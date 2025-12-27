package ru.mifi.practice.voln.polling;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import ru.mifi.practice.voln.event.Event;

import java.time.Duration;

class EventServiceTest {

    private EventService eventService;

    @BeforeEach
    void setUp() {
        eventService = new EventService.Default();
    }

    @Test
    void testAddAndGetHistoricalEvents() {
        eventService.addEvent(new Event.Data("1"));
        eventService.addEvent(new Event.Data("2"));

        StepVerifier.create(eventService.getEvents(0L, 1))
            .expectNextMatches(e -> e.streamId() == 0L && "1".equals(e.data().message()))
            .expectNextMatches(e -> e.streamId() == 0L && "2".equals(e.data().message()))
            .verifyComplete();
    }

    @Test
    void testGetEventsWithOffset() {
        eventService.addEvent(new Event.Data("1"));
        eventService.addEvent(new Event.Data("2"));
        eventService.addEvent(new Event.Data("3"));

        StepVerifier.create(eventService.getEvents(1L, 1))
            .expectNextMatches(e -> e.streamId() == 0L)
            .expectNextMatches(e -> e.streamId() == 0L)
            .verifyComplete();
    }

    @Test
    void testLongPollingWait() {
        StepVerifier.create(eventService.getEvents(0L, 2))
            .thenAwait(Duration.ofMillis(100))
            .then(() -> eventService.addEvent(new Event.Data("new")))
            .expectNextMatches(e -> "new".equals(e.data().message()))
            .verifyComplete();
    }

    @Test
    void testLongPollingTimeout() {
        StepVerifier.withVirtualTime(() -> eventService.getEvents(0L, 5))
            .expectSubscription()
            .thenAwait(Duration.ofSeconds(10))
            .expectNextCount(0)
            .verifyComplete();
    }

    @Test
    void testBufferLimit() {
        for (int i = 0; i < 4; i++) {
            eventService.addEvent(new Event.Data(String.valueOf(i)));
        }

        StepVerifier.create(eventService.getEvents(0L, 1))
            .expectNextMatches(e -> e.streamId() == 0L)
            .expectNextCount(3)
            .verifyComplete();
    }

    @Test
    void testGetEventsWithNullOffset() {
        eventService.addEvent(new Event.Data("1"));
        StepVerifier.create(eventService.getEvents(null, 1))
            .expectNextMatches(e -> e.streamId() == 0L)
            .verifyComplete();
    }
}
