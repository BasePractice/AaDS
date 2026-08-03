package ru.mifi.practice.vol2.vm;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import ru.mifi.practice.vol2.vm.impl.PolskaMachine;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;

@DisplayName("Виртуальная машина обратной польской записи")
final class PolskaMachineTest {

    @DisplayName("Складывает два операнда")
    @Test
    @Timeout(1)
    void addsTwoOperands() {
        assertThat("addition dont produce the sum",
            new PolskaMachine().eval("3 2 +", VirtualMachine.Context.newContext()).doubleValue(),
            closeTo(5, 1e-9));
    }

    @DisplayName("Вычитает второй операнд из первого")
    @Test
    @Timeout(1)
    void subtractsSecondOperandFromFirst() {
        assertThat("subtraction takes the operands in reverse order",
            new PolskaMachine().eval("3 2 -", VirtualMachine.Context.newContext()).doubleValue(),
            closeTo(1, 1e-9));
    }

    @DisplayName("Делит первый операнд на второй")
    @Test
    @Timeout(1)
    void dividesFirstOperandBySecond() {
        assertThat("division takes the operands in reverse order",
            new PolskaMachine().eval("10 2 /", VirtualMachine.Context.newContext()).doubleValue(),
            closeTo(5, 1e-9));
    }

    @DisplayName("Пустое значение имеет тип NONE")
    @Test
    @Timeout(1)
    void marksMissingValueAsNone() {
        assertThat("a missing value pretends to be a number",
            VirtualMachine.DefaultValue.none().type(), is(VirtualMachine.Type.NONE));
    }
}
