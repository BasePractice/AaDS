package ru.mifi.practice.vol8.regexp.machine;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("State")
class StateTest {
    @DisplayName("manager")
    @Test
    void manager() {
        Manager manager = new Manager.Default();
        var symbol = manager.newState(State.Symbol.class, 'p');
        assertEquals("p", symbol.toString());
        var sequence = manager.newState(State.Sequence.class);
        assertEquals("", sequence.toString());
        var parallel = manager.newState(State.Parallel.class);
        assertEquals("[]", parallel.toString());
    }
}
