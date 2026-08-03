package ru.mifi.practice.vol8.regexp.machine;

import java.util.HashSet;
import java.util.Set;

/** Генерация PlantUML-диаграммы состояний конечного автомата. */
public final class PlantUmlTextGenerator extends Visitor.AbstractStringVisitor implements State.Diagram {
    private final Set<State> visited = new HashSet<>();
    private final Set<String> printed = new HashSet<>();
    private final Set<String> declared = new HashSet<>();

    @Override
    public String name(State state) {
        if (state == null) {
            return "[*]";
        }
        String name = String.format("S%02d", state.index);
        if (!declared.contains(name)) {
            declared.add(name);
            buffer.append("state \"").append(state.diagramLabel()).append("\" as ").append(name).append("\n");
        }
        return name;
    }

    @Override
    public void visit(State from, State state) {
        print(from, state);
    }

    public void start(State state) {
        buffer.setLength(0);
        buffer.append("@startuml").append("\n");
        buffer.append("hide empty description").append("\n");
        state.visit(this);
        buffer.append("[*] --> ").append(name(state)).append("\n");
        buffer.append("@enduml").append("\n");
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
        buffer.append(text).append("\n");
    }
}
