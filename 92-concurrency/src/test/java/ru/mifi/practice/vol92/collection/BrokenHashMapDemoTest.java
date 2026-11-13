package ru.mifi.practice.vol92.collection;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;

@DisplayName("Гонка в HashMap против ConcurrentHashMap")
final class BrokenHashMapDemoTest {

    @DisplayName("HashMap теряет записи при росте из двух потоков хотя бы один раз")
    @Test
    @Timeout(60)
    void losesEntriesAtLeastOnce() throws InterruptedException {
        int worst = 20_000;
        for (int attempt = 0; attempt < 5; attempt++) {
            Map<Integer, Integer> plain = new HashMap<>();
            run(plain);
            worst = plain.size();
            if (worst < 20_000) {
                break;
            }
        }
        assertThat("plain hashmap never loses entries under race",
            worst, lessThan(20_000));
    }

    @DisplayName("ConcurrentHashMap не теряет записи")
    @Test
    @Timeout(60)
    void keepsEntriesInConcurrentMap() throws InterruptedException {
        Map<Integer, Integer> concurrent = new ConcurrentHashMap<>();
        run(concurrent);
        assertThat("concurrent hashmap loses entries under race",
            concurrent.size(), is(20_000));
    }

    private void run(Map<Integer, Integer> map) throws InterruptedException {
        Thread first = new Thread(new BrokenHashMapDemo(map, 0, 10_000));
        Thread second = new Thread(new BrokenHashMapDemo(map, 10_000, 20_000));
        first.start();
        second.start();
        first.join();
        second.join();
    }
}
