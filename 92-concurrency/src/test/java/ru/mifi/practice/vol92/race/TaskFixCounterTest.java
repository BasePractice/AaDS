package ru.mifi.practice.vol92.race;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Задание: потокобезопасный счётчик")
final class TaskFixCounterTest {

    @Disabled("Место для решения студента")
    @DisplayName("Два потока досчитывают до 2 * steps без потерь")
    @Test
    @Timeout(30)
    void keepsAllIncrementsFromTwoThreads() throws InterruptedException {
        int steps = 100_000;
        TaskFixCounter counter = new TaskFixCounter(steps);
        Thread first = new Thread(counter);
        Thread second = new Thread(counter);
        first.start();
        second.start();
        first.join();
        second.join();
        assertThat("student counter loses increments under race",
            counter.value(), is(2 * steps));
    }
}
