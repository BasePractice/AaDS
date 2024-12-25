package ru.mifi.practice.vol8.regexp.machine;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import ru.mifi.practice.vol8.regexp.AbstractPatternTest;
import ru.mifi.practice.vol8.regexp.tree.Tree;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Machine")
class MachineTest extends AbstractPatternTest {
    @DisplayName("generator")
    @ParameterizedTest
    @MethodSource("patternText")
    void parse(String name, String text) throws IOException {
        MachineGenerator generator = new MachineGenerator();
        Tree.Default tree = new Tree.Default(text);
        tree.visit(generator);
        assertNotNull(generator.getState());
        PlantUmlTextGenerator plantUml = new PlantUmlTextGenerator();
        plantUml.start(generator.getState());
        plantUml.writeFile(String.format("%s.fsm.utext", name));

        StringBuilder builder = new StringBuilder();
        builder.append("@startregex").append("\n");
        builder.append(text).append("\n");
        builder.append("@endregex").append("\n");
        Files.writeString(Path.of(String.format("%s.rgx.utext", name)), builder, StandardCharsets.UTF_8);
    }
}
