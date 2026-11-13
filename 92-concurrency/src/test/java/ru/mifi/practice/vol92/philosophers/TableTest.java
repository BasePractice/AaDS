package ru.mifi.practice.vol92.philosophers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.not;

@DisplayName("Стол обедающих философов")
final class TableTest {

    @DisplayName("Жадная стратегия попадает в дедлок хотя бы один раз")
    @Test
    @Timeout(60)
    void deadlocksOnGreedyStrategyAtLeastOnce() throws InterruptedException {
        int expected = new Table(5, Table.Strategy.GREEDY, 3).expected();
        int meals = expected;
        for (int attempt = 0; attempt < 3 && meals == expected; attempt++) {
            meals = new Table(5, Table.Strategy.GREEDY, 3).dine(500, TimeUnit.MILLISECONDS);
        }
        assertThat("greedy philosophers never deadlock",
            meals, lessThan(expected));
    }

    @DisplayName("Стратегия с нумерацией вилок не попадает в дедлок")
    @Test
    @Timeout(60)
    void avoidsDeadlockOnOrderedStrategy() throws InterruptedException {
        Table table = new Table(5, Table.Strategy.ORDERED, 3);
        assertThat("ordered philosophers deadlock or lose meals",
            table.dine(5, TimeUnit.SECONDS), not(lessThan(table.expected())));
    }

    @DisplayName("Семафор мест не пускает за стол всех сразу")
    @Test
    @Timeout(60)
    void avoidsDeadlockOnSemaphoreStrategy() throws InterruptedException {
        Table table = new Table(5, Table.Strategy.SEMAPHORE, 3);
        assertThat("semaphore philosophers deadlock or lose meals",
            table.dine(5, TimeUnit.SECONDS), not(lessThan(table.expected())));
    }

    @DisplayName("tryLock с откатом не попадает в дедлок")
    @Test
    @Timeout(60)
    void avoidsDeadlockOnTryLockStrategy() throws InterruptedException {
        Table table = new Table(5, Table.Strategy.TRYLOCK, 5);
        int meals = table.dine(5, TimeUnit.SECONDS);
        assertThat("trylock philosophers deadlock",
            meals, not(lessThan(1)));
        assertThat("trylock philosophers lose all meals",
            meals, not(lessThan(1)));
    }
}
