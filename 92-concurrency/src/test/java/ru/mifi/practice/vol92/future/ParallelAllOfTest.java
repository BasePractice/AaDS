package ru.mifi.practice.vol92.future;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Параллельные задачи и allOf")
final class ParallelAllOfTest {

    @DisplayName("allOf ждёт все задачи и складывает их результаты")
    @Test
    @Timeout(30)
    void waitsForAllTasksAndSumsResults() {
        ParallelAllOf all = new ParallelAllOf();
        long expected = 0;
        for (int task = 0; task < 4; task++) {
            for (int i = 1; i <= task * 100; i++) {
                expected += i;
            }
        }
        assertThat("allOf loses results of parallel tasks",
            all.total(4), is(expected));
    }
}
