package ru.mifi.practice.vol92.atomic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Цикл compare-and-set")
final class CasLoopTest {

    @DisplayName("Два потока сходятся к точной сумме через CAS")
    @Test
    @Timeout(30)
    void keepsExactTotalFromTwoThreads() throws InterruptedException {
        CasLoop loop = new CasLoop();
        Thread first = new Thread(() -> loop.advance(10_000));
        Thread second = new Thread(() -> loop.advance(10_000));
        first.start();
        second.start();
        first.join();
        second.join();
        assertThat("cas loop loses updates under race",
            loop.value(), is(20_000));
    }
}
