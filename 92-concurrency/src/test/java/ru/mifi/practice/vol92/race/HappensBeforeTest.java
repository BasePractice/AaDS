package ru.mifi.practice.vol92.race;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Видимость после join")
final class HappensBeforeTest {

    @DisplayName("Записи потока видны после его join")
    @Test
    @Timeout(10)
    void makesWritesVisibleAfterJoin() throws InterruptedException {
        HappensBefore worker = new HappensBefore();
        Thread thread = new Thread(worker);
        thread.start();
        thread.join();
        assertThat("answer is not visible after join",
            worker.answer(), is(42));
        assertThat("flag is not visible after join",
            worker.ready(), is(true));
    }
}
