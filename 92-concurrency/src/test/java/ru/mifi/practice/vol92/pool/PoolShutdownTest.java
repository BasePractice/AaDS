package ru.mifi.practice.vol92.pool;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Корректное завершение пула")
final class PoolShutdownTest {

    @DisplayName("Пул дорабатывает задачи после shutdown")
    @Test
    @Timeout(30)
    void finishesTasksAfterShutdown() throws Exception {
        PoolShutdown pool = new PoolShutdown(2);
        int first = pool.task(21).get();
        int second = pool.task(10).get();
        assertThat("pool doubles the task value wrong",
            first + second, is(62));
        assertThat("pool doesnt terminate after shutdown",
            pool.finish(), is(true));
    }
}
