package ru.mifi.practice.vol8.regexp.machine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public interface Visitor {

    void start();

    void end();

    void visit(State from, State state);

    @SuppressWarnings("PMD.AvoidStringBufferField")
    abstract class AbstractStringVisitor extends AbstractVisitor {
        protected final StringBuilder buffer = new StringBuilder();

        public final void writeFile(String fileName) throws IOException {
            Files.writeString(Path.of(fileName), buffer.toString());
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

    abstract class AbstractVisitor implements Visitor {
        @Override
        public void start() {
            //Nothing
        }

        @Override
        public void end() {
            //Nothing
        }

        @Override
        public void visit(State from, State state) {
            //Nothing
        }
    }
}
