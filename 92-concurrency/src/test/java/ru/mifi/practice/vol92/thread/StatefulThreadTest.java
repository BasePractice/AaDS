package ru.mifi.practice.vol92.thread;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Состояния потока снаружи")
final class StatefulThreadTest {

    @DisplayName("Поток виден как NEW до запуска и TERMINATED после join")
    @Test
    @Timeout(10)
    void showsNewBeforeStartAndTerminatedAfterJoin() throws InterruptedException {
        AtomicInteger ran = new AtomicInteger();
        StatefulThread thread = new StatefulThread(ran::incrementAndGet);
        assertThat("fresh thread is not in state NEW",
            thread.state(), is(Thread.State.NEW));
        thread.start();
        thread.join();
        assertThat("joined thread is not in state TERMINATED",
            thread.state(), is(Thread.State.TERMINATED));
        assertThat("thread body did not run",
            ran.get(), is(1));
    }
}
