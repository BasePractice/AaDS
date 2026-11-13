package ru.mifi.practice.vol92.race;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThan;

@DisplayName("Счётчик на volatile")
final class VolatileCounterTest {

    @DisplayName("Видимость есть, атомарности нет — инкременты теряются хотя бы один раз")
    @Test
    @Timeout(60)
    void losesIncrementsAtLeastOnce() throws InterruptedException {
        int steps = 100_000;
        int worst = 2 * steps;
        for (int attempt = 0; attempt < 5; attempt++) {
            VolatileCounter counter = new VolatileCounter(steps);
            Thread first = new Thread(counter);
            Thread second = new Thread(counter);
            first.start();
            second.start();
            first.join();
            second.join();
            worst = counter.value();
            if (worst < 2 * steps) {
                break;
            }
        }
        assertThat("volatile fixes the lost update race",
            worst, lessThan(2 * steps));
    }
}
