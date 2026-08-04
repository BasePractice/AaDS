package ru.mifi.practice.voln.cache.memory;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Проверка оповещений об изменении кэша.
 *
 * <p>Доставка идёт отдельным потоком, поэтому проверки ждут её на защёлке с таймаутом, а не
 * усыплением: усыплённая проверка либо тормозит сборку, либо мигает на загруженной машине.
 */
@DisplayName("Оповещения кэша в памяти")
final class NotifiableMemoryTest {

    @DisplayName("Подписчик получает оповещение своего канала")
    @Test
    @Timeout(10)
    void deliversTheNotificationToTheSubscriber() throws Exception {
        try (NotifiableMemory notifiable = new NotifiableMemory(16, new SimpleMeterRegistry())) {
            CountDownLatch delivered = new CountDownLatch(1);
            notifiable.registerNotify("счётчик", key -> delivered.countDown());
            notifiable.notify("счётчик", 42L);
            assertThat("the subscriber dont get the notification of its channel",
                delivered.await(5, TimeUnit.SECONDS), is(true));
        }
    }

    @DisplayName("Оповещение доносит ключ")
    @Test
    @Timeout(10)
    void carriesTheKeyToTheSubscriber() throws Exception {
        try (NotifiableMemory notifiable = new NotifiableMemory(16, new SimpleMeterRegistry())) {
            CountDownLatch delivered = new CountDownLatch(1);
            AtomicLong seen = new AtomicLong();
            notifiable.registerNotify("счётчик", key -> {
                seen.set(key);
                delivered.countDown();
            });
            notifiable.notify("счётчик", 42L);
            delivered.await(5, TimeUnit.SECONDS);
            assertThat("the notification dont carry the key", seen.get(), is(42L));
        }
    }

    @DisplayName("Оповещения чужого канала не приходят")
    @Test
    @Timeout(10)
    void keepsForeignChannelsApart() throws Exception {
        try (NotifiableMemory notifiable = new NotifiableMemory(16, new SimpleMeterRegistry())) {
            CountDownLatch own = new CountDownLatch(1);
            AtomicLong foreign = new AtomicLong();
            notifiable.registerNotify("чужой", key -> foreign.incrementAndGet());
            notifiable.registerNotify("свой", key -> own.countDown());
            notifiable.notify("свой", 1L);
            own.await(5, TimeUnit.SECONDS);
            assertThat("a foreign channel gets the notification", foreign.get(), is(0L));
        }
    }

    @DisplayName("Оповещение доходит до всех подписчиков канала")
    @Test
    @Timeout(10)
    void deliversToEverySubscriberOfTheChannel() throws Exception {
        try (NotifiableMemory notifiable = new NotifiableMemory(16, new SimpleMeterRegistry())) {
            CountDownLatch delivered = new CountDownLatch(2);
            notifiable.registerNotify("счётчик", key -> delivered.countDown());
            notifiable.registerNotify("счётчик", key -> delivered.countDown());
            notifiable.notify("счётчик", 42L);
            assertThat("the notification dont reach every subscriber of the channel",
                delivered.await(5, TimeUnit.SECONDS), is(true));
        }
    }
}
