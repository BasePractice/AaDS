package ru.mifi.practice.voln.machine.post;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Машина Поста")
class ProgramTest {

    private static final String ONE_INCREMENT = """
        ? 1;2
        V 3
        X 4
        !
        < 0
        """; //promo.transaction.result,promo.wallet.debit,bonus.wallet.debit

    private static Stream<Arguments> programs() throws IOException {
        return Stream.of(
            createArguments("100000", ONE_INCREMENT, "100001")
        );
    }

    private static Arguments createArguments(String head, String program, String result) throws IOException {
        MechanicalHead.BooleanArrayHead original = new MechanicalHead.BooleanArrayHead(head.length());
        return Arguments.of(
            original.initiate(head).offset(head.length() - 1),
            new Program.Default(program, CanonicalCommand.values()),
            new MechanicalHead.BooleanArrayHead(original.size()).initiate(result)
        );
    }

    @ParameterizedTest
    @MethodSource("programs")
    void execute(MechanicalHead head, Program program, MechanicalHead result) {
        program.execute(head);
        assertEquals(result, head);
    }
}
