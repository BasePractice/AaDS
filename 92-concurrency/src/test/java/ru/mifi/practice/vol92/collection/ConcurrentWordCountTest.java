package ru.mifi.practice.vol92.collection;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Подсчёт слов в ConcurrentHashMap")
final class ConcurrentWordCountTest {

    @DisplayName("Два потока считают пересекающиеся списки слов")
    @Test
    @Timeout(30)
    void countsOverlappingWordsFromTwoThreads() throws InterruptedException {
        ConcurrentWordCount count = new ConcurrentWordCount();
        List<String> first = List.of("java", "поток", "java");
        List<String> second = List.of("поток", "java", "монитор");
        Thread one = new Thread(() -> count.count(first));
        Thread two = new Thread(() -> count.count(second));
        one.start();
        two.start();
        one.join();
        two.join();
        assertThat("concurrent map loses word counts under race",
            count.frequency("java"), is(3));
        assertThat("concurrent map loses word counts under race",
            count.frequency("поток"), is(2));
        assertThat("concurrent map loses word counts under race",
            count.frequency("монитор"), is(1));
    }
}
