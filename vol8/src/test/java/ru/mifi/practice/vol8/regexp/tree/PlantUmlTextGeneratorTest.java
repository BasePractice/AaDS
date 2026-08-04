package ru.mifi.practice.vol8.regexp.tree;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

@DisplayName("Диаграмма EBNF по дереву регулярного выражения")
final class PlantUmlTextGeneratorTest {

    @DisplayName("Диаграмма обрамлена директивами PlantUML")
    @Test
    @Timeout(5)
    void framesTheDiagramWithPlantUmlDirectives() {
        PlantUmlTextGenerator visitor = new PlantUmlTextGenerator();
        new Tree.Default("ab").visit(visitor);
        assertThat("EBNF diagram dont carry the PlantUML frame",
            visitor.toString(), is("@startebnf\npattern = a,b;\n@endebnf\n"));
    }

    @DisplayName("Узлы дерева переводятся в правило EBNF")
    @ParameterizedTest
    @Timeout(5)
    @CsvSource(delimiter = '#', value = {
        "ab                                              # pattern = a,b;",
        "a|b                                             # pattern = (a|b);",
        "a*                                              # pattern = {a};",
        "a?                                              # pattern = [a];",
        "[^of]                                           # pattern = (o|f);",
        "abc*d?|abce|ab?ei|a(bcde[cei])+|d(c|e|i)?i      # pattern = (a,b,{c},[d]|a,b,c,e|a,[b],e,i|a,[(b,c,d,e,(c|e|i))]|d,[((c|e|i))],i);",
    })
    void translatesTreeNodesIntoAnEbnfRule(String pattern, String rule) {
        PlantUmlTextGenerator visitor = new PlantUmlTextGenerator();
        new Tree.Default(pattern).visit(visitor);
        assertThat("the tree dont translate into the EBNF rule", visitor.toString(), containsString(rule));
    }

    @DisplayName("Повторный обход не смешивает диаграммы")
    @ParameterizedTest
    @Timeout(5)
    @CsvSource(delimiter = '#', value = {
        "ab   # pattern = a,b;",
        "a|b  # pattern = (a|b);",
    })
    void keepsDiagramsApartOnAReusedVisitor(String pattern, String rule) {
        PlantUmlTextGenerator visitor = new PlantUmlTextGenerator();
        new Tree.Default("d(c|e|i)?i").visit(visitor);
        new Tree.Default(pattern).visit(visitor);
        assertThat("a reused visitor mixes the previous diagram in",
            visitor.toString(), is("@startebnf\n" + rule + "\n@endebnf\n"));
    }
}
