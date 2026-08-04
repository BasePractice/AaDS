package ru.mifi.practice.commons;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/** Проверка счётчика операций, на котором держатся все замеры сложности. */
@DisplayName("Счётчик операций")
final class CounterTest {

    @DisplayName("Новый счётчик стоит на нуле")
    @Test
    @Timeout(1)
    void startsFromZero() {
        assertThat("a fresh counter dont start from zero", Counter.create().count(), is(0));
    }

    @DisplayName("Шаг увеличивает счётчик на единицу")
    @Test
    @Timeout(1)
    void growsByOneOnEachStep() {
        Counter counter = Counter.create();
        counter.increment();
        counter.increment();
        assertThat("two steps dont give two", counter.count(), is(2));
    }

    @DisplayName("Сброс возвращает счётчик на ноль")
    @Test
    @Timeout(1)
    void returnsToZeroOnReset() {
        Counter counter = Counter.create();
        counter.increment();
        counter.reset();
        assertThat("reset dont return the counter to zero", counter.count(), is(0));
    }

    @DisplayName("Счётчик печатается своим значением")
    @Test
    @Timeout(1)
    void printsItsValue() {
        Counter counter = Counter.create();
        counter.increment();
        assertThat("the counter dont print its value", counter.toString(), is("1"));
    }

    @DisplayName("Счёт из нескольких потоков ничего не теряет")
    @Test
    @Timeout(5)
    void losesNothingWhenCountedFromSeveralThreads() throws InterruptedException {
        Counter counter = Counter.create();
        Thread[] threads = new Thread[4];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                for (int step = 0; step < 1000; step++) {
                    counter.increment();
                }
            });
            threads[i].start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        assertThat("counting from several threads loses steps", counter.count(), is(4000));
    }
}
