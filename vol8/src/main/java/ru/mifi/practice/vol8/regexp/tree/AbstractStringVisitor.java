package ru.mifi.practice.vol8.regexp.tree;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@SuppressWarnings("PMD.AvoidStringBufferField")
abstract class AbstractStringVisitor extends AbstractVisitor {
    protected final StringBuilder buffer = new StringBuilder();

    public final void writeFile(String fileName) throws IOException {
        Files.writeString(Path.of(fileName), buffer.toString());
    }

    @Override
    public void visit(Tree.Char ch) {
        buffer.append(ch.ch());
    }

    @Override
    public void start() {
        buffer.setLength(0);
    }

    @Override
    public String toString() {
        return buffer.toString();
    }
}
