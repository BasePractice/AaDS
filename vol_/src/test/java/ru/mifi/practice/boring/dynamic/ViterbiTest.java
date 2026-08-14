package ru.mifi.practice.boring.dynamic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Проверка на классическом примере: скрытые состояния — погода (0 — дождь, 1 — солнце),
 * наблюдения — занятия человека (0 — гуляет, 1 — ходит по магазинам, 2 — убирается).
 */
@DisplayName("Наиболее вероятная цепочка состояний по Витерби")
final class ViterbiTest {

    @DisplayName("По прогулке узнаётся солнечный день")
    @Test
    @Timeout(1)
    void readsTheSunnyDayFromAWalk() {
        assertThat("the walk dont point at the sunny day",
            weather().path(new int[]{0}), is(new int[]{1}));
    }

    @DisplayName("По уборке узнаётся дождливый день")
    @Test
    @Timeout(1)
    void readsTheRainyDayFromCleaning() {
        assertThat("cleaning dont point at the rainy day",
            weather().path(new int[]{2}), is(new int[]{0}));
    }

    @DisplayName("Цепочка наблюдений даёт цепочку состояний той же длины")
    @Test
    @Timeout(1)
    void answersWithTheSameLength() {
        assertThat("the answer is shorter than the observations",
            weather().path(new int[]{0, 1, 2}).length, is(3));
    }

    @DisplayName("Классический пример даёт известный ответ")
    @Test
    @Timeout(1)
    void solvesTheClassicExample() {
        assertThat("the classic example gives an unexpected chain",
            weather().path(new int[]{0, 1, 2}), is(new int[]{1, 0, 0}));
    }

    @DisplayName("Запрещённый переход не выбирается, каким бы выгодным ни было наблюдение")
    @Test
    @Timeout(1)
    void refusesTheForbiddenTransition() {
        double[] starts = {0.4, 0.6};
        double[][] transitions = {{1.0, 0.0}, {1.0, 0.0}};
        double[][] emissions = {{0.4, 0.6}, {0.6, 0.4}};
        assertThat("the forbidden transition got into the chain",
            new Viterbi(starts, transitions, emissions).path(new int[]{0, 0}), is(new int[]{1, 0}));
    }

    @DisplayName("Пустая цепочка наблюдений даёт пустой ответ")
    @Test
    @Timeout(1)
    void answersNothingToNoObservations() {
        assertThat("the empty observations got a chain of states",
            weather().path(new int[0]).length, is(0));
    }

    private static Viterbi weather() {
        double[] starts = {0.6, 0.4};
        double[][] transitions = {{0.7, 0.3}, {0.4, 0.6}};
        double[][] emissions = {{0.1, 0.4, 0.5}, {0.6, 0.3, 0.1}};
        return new Viterbi(starts, transitions, emissions);
    }
}
