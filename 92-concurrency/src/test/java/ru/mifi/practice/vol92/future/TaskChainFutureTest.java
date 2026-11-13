package ru.mifi.practice.vol92.future;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Задание: конвейер на CompletableFuture")
final class TaskChainFutureTest {

    @Disabled("Место для решения студента")
    @DisplayName("Конвейер обрезает строку и возвращает длину результата")
    @Test
    @Timeout(30)
    void trimsSourceAndReturnsLength() throws Exception {
        TaskChainFuture task = new TaskChainFuture();
        assertThat("student pipeline returns wrong length",
            task.pipeline("  Java Threads ").get(), is("java-threads".length()));
    }
}
