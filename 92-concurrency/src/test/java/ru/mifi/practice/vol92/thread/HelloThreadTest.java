package ru.mifi.practice.vol92.thread;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

@DisplayName("Поток, пишущий шаги в общий журнал")
final class HelloThreadTest {

    @DisplayName("Два потока заполняют журнал после join")
    @Test
    @Timeout(10)
    void fillsTheJournalFromTwoThreadsAfterJoin() throws InterruptedException {
        List<String> journal = new CopyOnWriteArrayList<>();
        Thread first = new Thread(new HelloThread("первый", 3, journal));
        Thread second = new Thread(new HelloThread("второй", 3, journal));
        first.start();
        second.start();
        first.join();
        second.join();
        assertThat("two threads of three steps dont fill the journal",
            journal.size(), is(6));
        assertThat("journal misses steps of the first thread",
            journal, hasItem("первый шаг 0"));
        assertThat("journal misses steps of the second thread",
            journal, hasItem("второй шаг 2"));
    }

    @DisplayName("Пустой журнал остаётся пустым без запуска")
    @Test
    @Timeout(1)
    void keepsTheJournalEmptyWithoutStart() {
        List<String> journal = Collections.synchronizedList(new java.util.ArrayList<>());
        new HelloThread("одиночка", 3, journal);
        assertThat("unstarted thread writes to the journal",
            journal.size(), is(0));
    }
}
