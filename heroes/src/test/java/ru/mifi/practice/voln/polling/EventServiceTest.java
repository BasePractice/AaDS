package ru.mifi.practice.voln.polling;

import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

class EventServiceTest {

    private EventService eventService;
    private static final long BUFFER_SIZE = 5;

    @BeforeEach
    void setUp() {
        eventService = new EventService.Default(BUFFER_SIZE);
    }

    @Test
    void testAddAndGetHistoricalEvents() {
        eventService.addEvent(new Event.Data("1"));
        eventService.addEvent(new Event.Data("2"));

        StepVerifier.create(eventService.getEvents(0L, 1))
                .expectNextMatches(e -> e.id() == 1 && "1".equals(e.data().message()))
                .expectNextMatches(e -> e.id() == 2 && "2".equals(e.data().message()))
                .verifyComplete();
    }

    @Test
    void testGetEventsWithOffset() {
        eventService.addEvent(new Event.Data("1"));
        eventService.addEvent(new Event.Data("2"));
        eventService.addEvent(new Event.Data("3"));

        StepVerifier.create(eventService.getEvents(1L, 1))
                .expectNextMatches(e -> e.id() == 2)
                .expectNextMatches(e -> e.id() == 3)
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
        for (int i = 1; i <= BUFFER_SIZE + 2; i++) {
            eventService.addEvent(new Event.Data(String.valueOf(i)));
        }

        StepVerifier.create(eventService.getEvents(0L, 1))
                .expectNextMatches(e -> e.id() == 3)
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    void testGetEventsWithNullOffset() {
        eventService.addEvent(new Event.Data("1"));
        StepVerifier.create(eventService.getEvents(null, 1))
                .expectNextMatches(e -> e.id() == 1)
                .verifyComplete();
    }
}
