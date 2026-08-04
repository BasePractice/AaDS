package ru.mifi.practice.voln.cache;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import ru.mifi.practice.voln.cache.memory.CacheableMapMemory;
import ru.mifi.practice.voln.cache.memory.NotifiableMemory;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Проверка значения с двумя уровнями кэша.
 *
 * <p>Оба уровня здесь в памяти: первый — на Guava, второй — карта вместо Redis. Кэш обновляется
 * не по запросу, а через канал оповещений: промах отвечает пустотой и заказывает обновление,
 * поэтому проверки ждут его с крайним сроком, а не рассчитывают получить значение сразу.
 */
@DisplayName("Значение с двумя уровнями кэша")
final class SimpleCacheableValueTest {

    @DisplayName("Первое обращение значения не даёт")
    @Test
    @Timeout(10)
    void givesNothingOnTheFirstRead() throws Exception {
        try (CacheableValue value = cacheable(key -> key * 10)) {
            assertThat("the first read gives a value the cache dont have yet",
                value.getValue(1L).isPresent(), is(false));
        }
    }

    @DisplayName("Промах заказывает значение у источника")
    @Test
    @Timeout(10)
    void ordersTheValueFromTheSourceOnAMiss() throws Exception {
        CountDownLatch fetched = new CountDownLatch(1);
        try (CacheableValue value = cacheable(key -> {
            fetched.countDown();
            return key * 10;
        })) {
            value.getValue(1L);
            assertThat("a miss dont order the value from the source",
                fetched.await(5, TimeUnit.SECONDS), is(true));
        }
    }

    @DisplayName("После обновления значение читается из кэша")
    @Test
    @Timeout(10)
    void readsTheValueAfterTheUpdate() throws Exception {
        try (CacheableValue value = cacheable(key -> key * 10)) {
            assertThat("the value dont come from the cache after the update",
                warmed(value, 7L).value(), is(70L));
        }
    }

    @DisplayName("Обновлённое значение считается актуальным")
    @Test
    @Timeout(10)
    void marksTheUpdatedValueActual() throws Exception {
        try (CacheableValue value = cacheable(key -> key * 10)) {
            assertThat("the updated value is not marked actual", warmed(value, 7L).isActual(), is(true));
        }
    }

    @DisplayName("Прогретый ключ источник больше не дёргает")
    @Test
    @Timeout(10)
    void keepsTheSourceUntouchedForAWarmKey() throws Exception {
        AtomicInteger reads = new AtomicInteger();
        try (CacheableValue value = cacheable(key -> {
            reads.incrementAndGet();
            return key * 10;
        })) {
            warmed(value, 1L);
            value.getValue(1L);
            value.getValue(1L);
            assertThat("a warm key reaches the source again", reads.get(), is(1));
        }
    }

    /**
     * Ждёт, пока заказанное обновление доедет до кэша. Обновление идёт отдельным потоком, и
     * защёлка внутри источника срабатывает раньше, чем значение туда положено, — поэтому здесь
     * опрос с крайним сроком, а не ожидание самого обращения к источнику.
     */
    private static CacheableValue.Value warmed(CacheableValue value, long key) throws InterruptedException {
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
        while (System.nanoTime() < deadline) {
            Optional<CacheableValue.Value> found = value.getValue(key);
            if (found.isPresent()) {
                return found.get();
            }
            TimeUnit.MILLISECONDS.sleep(10);
        }
        throw new IllegalStateException("Обновление не доехало до кэша за отведённое время");
    }

    private static CacheableValue cacheable(Function<Long, Long> source) {
        SimpleMeterRegistry registry = new SimpleMeterRegistry();
        return new SimpleCacheableValue(
            new CacheableMapMemory(registry),
            new NotifiableMemory(16, registry),
            source,
            60_000L,
            128L);
    }
}
