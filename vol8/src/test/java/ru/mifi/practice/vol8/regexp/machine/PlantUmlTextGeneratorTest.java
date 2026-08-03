package ru.mifi.practice.vol8.regexp.machine;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.mifi.practice.vol8.regexp.tree.Tree;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

@DisplayName("Диаграмма состояний в PlantUML")
final class PlantUmlTextGeneratorTest {

    @DisplayName("Диаграмма замыкается для любого квантификатора и структуры")
    @ParameterizedTest
    @Timeout(5)
    @ValueSource(strings = {"a?", "a*", "a+", "ab", "a|b", "[^of]"})
    void closesDiagramForEveryPattern(String pattern) {
        MachineGenerator generator = new MachineGenerator();
        new Tree.Default(pattern).visit(generator);
        PlantUmlTextGenerator plantUml = new PlantUmlTextGenerator();
        plantUml.start(generator.getState());
        assertThat("PlantUML diagram dont close for the pattern",
            plantUml.toString(), containsString("@enduml"));
    }
}
