package ru.mifi.practice.vol92.philosophers;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.lessThan;

@DisplayName("Задание: стол без дедлока")
final class TaskNoDeadlockTest {

    @Disabled("Место для решения студента")
    @DisplayName("Стол студента доедает все приёмы пищи без дедлока")
    @Test
    @Timeout(60)
    void finishesAllMealsWithoutDeadlock() throws InterruptedException {
        TaskNoDeadlock task = new TaskNoDeadlock(5, 3);
        Table table = task.table();
        assertThat("student table deadlocks or loses meals",
            table.dine(5, TimeUnit.SECONDS), not(lessThan(table.expected())));
    }
}
