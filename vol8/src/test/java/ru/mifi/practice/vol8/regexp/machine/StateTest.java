package ru.mifi.practice.vol8.regexp.machine;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Состояние автомата")
class StateTest {
    @DisplayName("Символьное состояние печатает свой символ")
    @Test
    @Timeout(5)
    void printsItsSymbol() {
        assertThat("symbol state dont print its own character",
            new Manager.Default().newState(State.Symbol.class, 'p').toString(), is("p"));
    }

    @DisplayName("Пустая последовательность печатается пустой строкой")
    @Test
    @Timeout(5)
    void printsEmptySequenceAsBlank() {
        assertThat("empty sequence dont print as a blank string",
            new Manager.Default().newState(State.Sequence.class).toString(), is(""));
    }

    @DisplayName("Пустая параллель печатается пустыми скобками")
    @Test
    @Timeout(5)
    void printsEmptyParallelAsBrackets() {
        assertThat("empty parallel dont print as empty brackets",
            new Manager.Default().newState(State.Parallel.class).toString(), is("[]"));
    }
}
