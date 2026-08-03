package ru.mifi.practice.vol2.vm;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.mifi.practice.vol2.vm.impl.PolskaMachine;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;

@DisplayName("Байт-код виртуальной машины")
final class BytecodeTest {

    @DisplayName("Скомпилированная программа считает то же, что и разобранная")
    @ParameterizedTest
    @ValueSource(strings = {"1 10 - abs", "3 2 -", "10 2 /", "3 2 +", "7 6 *", "9 3 - 2 /", "1 2 + 3 +"})
    void computesTheSameAsTheInterpreter(String program) throws IOException {
        VirtualMachine machine = new PolskaMachine();
        double interpreted = machine.eval(program, VirtualMachine.Context.newContext()).doubleValue();
        double compiled = machine.eval(machine.compile(program), VirtualMachine.Context.newContext()).doubleValue();
        assertThat("compiled program dont agree with the interpreter", compiled, closeTo(interpreted, 1e-9));
    }

    @DisplayName("Число больше байта переживает компиляцию")
    @Test
    void keepsANumberLargerThanAByte() throws IOException {
        VirtualMachine machine = new PolskaMachine();
        assertThat("a number above 255 is truncated to a single byte",
            machine.eval(machine.compile("1000 1 +"), VirtualMachine.Context.newContext()).doubleValue(),
            closeTo(1001, 1e-9));
    }

    @DisplayName("Дробное число переживает компиляцию")
    @Test
    void keepsAFractionalNumber() throws IOException {
        VirtualMachine machine = new PolskaMachine();
        assertThat("the fractional part is dropped at compile time",
            machine.eval(machine.compile("2.5 1 +"), VirtualMachine.Context.newContext()).doubleValue(),
            closeTo(3.5, 1e-9));
    }

    @DisplayName("Отрицательный результат переживает компиляцию")
    @Test
    void keepsANegativeResult() throws IOException {
        VirtualMachine machine = new PolskaMachine();
        assertThat("a negative result is mangled at compile time",
            machine.eval(machine.compile("1 10 -"), VirtualMachine.Context.newContext()).doubleValue(),
            closeTo(-9, 1e-9));
    }

    @DisplayName("Кириллица переживает запись и чтение")
    @Test
    void keepsCyrillicText() throws IOException {
        assertThat("a cyrillic string dont survive the round trip",
            roundTrip(VirtualMachine.DefaultValue.of("Привет, мир")).value(), is("Привет, мир"));
    }

    @DisplayName("Длинная строка переживает запись и чтение")
    @Test
    void keepsALongText() throws IOException {
        assertThat("a string longer than a byte length prefix is truncated",
            roundTrip(VirtualMachine.DefaultValue.of("я".repeat(300))).value(), is("я".repeat(300)));
    }

    @DisplayName("Логическое значение переживает запись и чтение")
    @Test
    void keepsABoolean() throws IOException {
        assertThat("a boolean dont survive the round trip",
            roundTrip(VirtualMachine.DefaultValue.of(true)).value(), is(true));
    }

    @DisplayName("Пустое значение переживает запись и чтение")
    @Test
    void keepsTheMissingValue() throws IOException {
        assertThat("a missing value dont survive the round trip",
            roundTrip(VirtualMachine.DefaultValue.none()).type(), is(VirtualMachine.Type.NONE));
    }

    private static VirtualMachine.Value roundTrip(VirtualMachine.Value value) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        value.write(output);
        try (ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray())) {
            return VirtualMachine.Type.of(input.read()).read(input);
        }
    }
}
