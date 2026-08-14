package ru.mifi.practice.boring.misc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@DisplayName("Алгоритм банкира")
final class BankerTest {

    @DisplayName("Безопасное состояние признаётся безопасным")
    @Test
    @Timeout(1)
    void acceptsTheSafeState() {
        assertThat("the safe state is reported as unsafe", safe().safe(), is(true));
    }

    @DisplayName("Для безопасного состояния находится порядок завершения")
    @Test
    @Timeout(1)
    void findsTheOrderOfCompletion() {
        assertThat("the order of completion is not found", safe().sequence(), hasSize(5));
    }

    @DisplayName("Небезопасное состояние признаётся небезопасным")
    @Test
    @Timeout(1)
    void refusesTheUnsafeState() {
        int[][] maximum = {{10, 10}, {10, 10}};
        int[][] allocated = {{1, 1}, {1, 1}};
        int[] available = {1, 1};
        assertThat("the unsafe state passes for safe",
            new Banker(maximum, allocated, available).safe(), is(false));
    }

    @DisplayName("У небезопасного состояния порядка завершения нет")
    @Test
    @Timeout(1)
    void findsNoOrderForTheUnsafeState() {
        int[][] maximum = {{10, 10}, {10, 10}};
        int[][] allocated = {{1, 1}, {1, 1}};
        int[] available = {1, 1};
        assertThat("the unsafe state got an order of completion",
            new Banker(maximum, allocated, available).sequence(), is(List.of()));
    }

    @DisplayName("Процесс, которому хватает свободного, завершается первым")
    @Test
    @Timeout(1)
    void completesTheAffordableProcessFirst() {
        int[][] maximum = {{3, 2}, {2, 2}};
        int[][] allocated = {{0, 0}, {2, 1}};
        int[] available = {1, 1};
        assertThat("the affordable process is not completed first",
            new Banker(maximum, allocated, available).sequence().get(0), is(1));
    }

    /** Классический пример из учебников: пять процессов и три вида ресурсов. */
    private static Banker safe() {
        int[][] maximum = {{7, 5, 3}, {3, 2, 2}, {9, 0, 2}, {2, 2, 2}, {4, 3, 3}};
        int[][] allocated = {{0, 1, 0}, {2, 0, 0}, {3, 0, 2}, {2, 1, 1}, {0, 0, 2}};
        int[] available = {3, 3, 2};
        return new Banker(maximum, allocated, available);
    }
}
