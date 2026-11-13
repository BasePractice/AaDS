package ru.mifi.practice.vol92.race;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;

@DisplayName("Счётчик с гонкой данных")
final class BrokenCounterTest {

    @DisplayName("Два потока теряют инкременты хотя бы один раз")
    @Test
    @Timeout(60)
    void losesIncrementsAtLeastOnce() throws InterruptedException {
        int steps = 100_000;
        int worst = 2 * steps;
        for (int attempt = 0; attempt < 5; attempt++) {
            BrokenCounter counter = new BrokenCounter(steps);
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
        assertThat("plain counter never loses increments under race",
            worst, lessThan(2 * steps));
    }

    @DisplayName("Один поток теряет инкременты только при гонке")
    @Test
    @Timeout(10)
    void keepsAllIncrementsWithoutRace() throws InterruptedException {
        int steps = 10_000;
        BrokenCounter counter = new BrokenCounter(steps);
        Thread single = new Thread(counter);
        single.start();
        single.join();
        assertThat("single-threaded counter loses increments",
            counter.value(), is(steps));
    }
}
