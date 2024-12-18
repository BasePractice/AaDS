package ru.mifi.practice.vol8.regexp.machine;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.mifi.practice.vol8.regexp.machine.State.Manager;

@DisplayName("State")
class StateTest {
    @DisplayName("manager")
    @Test
    void manager() {
        Manager manager = new Manager.Default();
        var epsilon = manager.newState(State.Epsilon.class);
        assertEquals("EP00", epsilon.toString());
        var symbol = manager.newState(State.Symbol.class, 'p');
        assertEquals("SY01", symbol.toString());
        var sequence = manager.newState(State.Sequence.class);
        assertEquals("SQ02", sequence.toString());
        var parallel = manager.newState(State.Parallel.class);
        assertEquals("PL03", parallel.toString());
        var bridge = manager.newState(State.Bridge.class, epsilon.index, symbol.index);
        assertEquals("BR04(EP00->SY01)", bridge.toString());
    }
}
