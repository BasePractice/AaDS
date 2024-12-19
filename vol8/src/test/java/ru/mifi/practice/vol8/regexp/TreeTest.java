package ru.mifi.practice.vol8.regexp;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import ru.mifi.practice.vol8.regexp.visitor.OriginalTextGenerator;
import ru.mifi.practice.vol8.regexp.visitor.PlantUmlTextGenerator;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Tree")
class TreeTest extends AbstractPatternTest {

    @DisplayName("parse")
    @ParameterizedTest
    @MethodSource("patternText")
    void parse(String name, String text) throws IOException {
        Tree.Default tree = new Tree.Default(text);
        Tree.Node node = tree.root();
        assertEquals(text, node.toString());
        PlantUmlTextGenerator uTextVisitor = new PlantUmlTextGenerator();
        OriginalTextGenerator textVisitor = new OriginalTextGenerator();
        tree.visit(uTextVisitor);
        uTextVisitor.writeFile(String.format("%s.utext", name));
        tree.visit(textVisitor);
        assertEquals(textVisitor.toString(), node.toString());
    }
}
