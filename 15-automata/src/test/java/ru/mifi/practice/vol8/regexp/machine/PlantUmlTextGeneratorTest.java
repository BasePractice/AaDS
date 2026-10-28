package ru.mifi.practice.vol8.regexp.machine;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.mifi.practice.vol8.regexp.tree.Tree;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

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

    @DisplayName("Последовательность рисуется цепочкой переходов")
    @Test
    @Timeout(5)
    void drawsSequenceAsChainOfEdges() {
        MachineGenerator generator = new MachineGenerator();
        new Tree.Default("ab").visit(generator);
        PlantUmlTextGenerator plantUml = new PlantUmlTextGenerator();
        plantUml.start(generator.getState());
        assertThat("sequence dont turn into a chain of edges", plantUml.toString(), is("""
            @startuml
            hide empty description
            state "Epsilon" as S00
            state "a" as S01
            S00 --> S01
            state "b" as S02
            S01 --> S02
            S02 --> [*]
            [*] --> S00
            @enduml
            """));
    }

    @DisplayName("Необязательное состояние рисуется обходным переходом")
    @Test
    @Timeout(5)
    void drawsNoneOrOneWithBypassEdge() {
        MachineGenerator generator = new MachineGenerator();
        new Tree.Default("a?").visit(generator);
        PlantUmlTextGenerator plantUml = new PlantUmlTextGenerator();
        plantUml.start(generator.getState());
        assertThat("none or one state dont get a bypass edge", plantUml.toString(), is("""
            @startuml
            hide empty description
            state "Epsilon" as S01
            state "a" as S00
            S01 --> S00
            S00 --> [*]
            S01 --> [*]
            [*] --> S01
            @enduml
            """));
    }

    @DisplayName("Повторение от нуля рисуется петлёй")
    @Test
    @Timeout(5)
    void drawsNoneOrMoreWithLoop() {
        MachineGenerator generator = new MachineGenerator();
        new Tree.Default("a*").visit(generator);
        PlantUmlTextGenerator plantUml = new PlantUmlTextGenerator();
        plantUml.start(generator.getState());
        assertThat("none or more state dont get a loop", plantUml.toString(), is("""
            @startuml
            hide empty description
            state "Epsilon" as S01
            state "a" as S00
            S01 --> S00
            S00 --> S00
            S00 --> [*]
            [*] --> S01
            @enduml
            """));
    }

    @DisplayName("Повторение от единицы рисуется возвратом к своему телу")
    @Test
    @Timeout(5)
    void drawsOneOrMoreWithReturnToItsBody() {
        MachineGenerator generator = new MachineGenerator();
        new Tree.Default("a+").visit(generator);
        PlantUmlTextGenerator plantUml = new PlantUmlTextGenerator();
        plantUml.start(generator.getState());
        assertThat("one or more state dont return to its own body", plantUml.toString(), is("""
            @startuml
            hide empty description
            state "Epsilon" as S01
            state "a" as S00
            S01 --> S00
            S00 --> [*]
            [*] --> S00
            [*] --> S01
            @enduml
            """));
    }

    @DisplayName("Альтернатива рисуется ветвлением из одного состояния")
    @Test
    @Timeout(5)
    void drawsAlternativeAsBranching() {
        MachineGenerator generator = new MachineGenerator();
        new Tree.Default("a|b").visit(generator);
        PlantUmlTextGenerator plantUml = new PlantUmlTextGenerator();
        plantUml.start(generator.getState());
        assertThat("alternative dont branch out of a single state", plantUml.toString(), is("""
            @startuml
            hide empty description
            state "Epsilon" as S00
            state "a" as S01
            S00 --> S01
            S01 --> [*]
            state "b" as S02
            S00 --> S02
            S02 --> [*]
            [*] --> S00
            @enduml
            """));
    }

    @DisplayName("Отрицание множества рисуется одним состоянием")
    @Test
    @Timeout(5)
    void drawsNegatedSetAsSingleState() {
        MachineGenerator generator = new MachineGenerator();
        new Tree.Default("[^of]").visit(generator);
        PlantUmlTextGenerator plantUml = new PlantUmlTextGenerator();
        plantUml.start(generator.getState());
        assertThat("negated set dont collapse into a single state", plantUml.toString(), is("""
            @startuml
            hide empty description
            state "Epsilon" as S03
            S03 --> [*]
            [*] --> S03
            @enduml
            """));
    }

    @DisplayName("Вложенная альтернатива рисуется сходящимися ветками")
    @Test
    @Timeout(5)
    void drawsNestedAlternativeWithConvergingBranches() {
        MachineGenerator generator = new MachineGenerator();
        new Tree.Default("a(b|c)d").visit(generator);
        PlantUmlTextGenerator plantUml = new PlantUmlTextGenerator();
        plantUml.start(generator.getState());
        assertThat("nested alternative dont converge its own branches", plantUml.toString(), is("""
            @startuml
            hide empty description
            state "Epsilon" as S00
            state "a" as S01
            S00 --> S01
            state "Epsilon" as S03
            S01 --> S03
            state "b" as S04
            S03 --> S04
            state "d" as S06
            S04 --> S06
            S06 --> [*]
            state "c" as S05
            S03 --> S05
            [*] --> S00
            @enduml
            """));
    }
}
