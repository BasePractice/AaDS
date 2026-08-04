package ru.mifi.practice.vol8.regexp.machine;

import ru.mifi.practice.vol8.regexp.TextBuffer;

import java.util.HashSet;
import java.util.Set;

/** Генерация PlantUML-диаграммы состояний конечного автомата. */
public final class PlantUmlTextGenerator implements Visitor, State.Diagram {
    private final TextBuffer buffer = new TextBuffer();
    private final Set<State> visited = new HashSet<>();
    private final Set<String> printed = new HashSet<>();
    private final Set<String> declared = new HashSet<>();

    @Override
    public String name(State state) {
        if (state == null) {
            return "[*]";
        }
        String name = String.format("S%02d", state.index());
        if (!declared.contains(name)) {
            declared.add(name);
            buffer.line("state \"" + state.diagramLabel() + "\" as " + name);
        }
        return name;
    }

    @Override
    public void visit(State from, State state) {
        print(from, state);
    }

    public void start(State state) {
        buffer.reset();
        buffer.line("@startuml");
        buffer.line("hide empty description");
        state.visit(this);
        buffer.line("[*] --> " + name(state));
        buffer.line("@enduml");
    }

    private void print(State state, State next) {
        if (next == null) {
            edge(name(state), "[*]");
            return;
        }
        if (visited.contains(next)) {
            return;
        }
        visited.add(next);
        String stateName = name(state);
        String nextName = name(next);
        edge(stateName, nextName);
        state.describe(this, stateName, nextName);
    }

    @Override
    public void edge(String from, String to) {
        String text = from + " --> " + to;
        if (printed.contains(text)) {
            return;
        }
        printed.add(text);
        buffer.line(text);
    }

    @Override
    public String toString() {
        return buffer.toString();
    }
}
