package ru.mifi.practice.vol8.regexp.machine;

import java.util.HashSet;
import java.util.Set;

public final class PlantUmlTextGenerator extends Visitor.AbstractStringVisitor {
    private final Set<State> visited = new HashSet<>();
    private final Set<State> visitedInfo = new HashSet<>();

    @Override
    public void visit(State from, State state) {
        print(from, state);
        info(from);
        info(state);
    }

    @Override
    public void start() {
        super.start();
        buffer.append("@startuml").append("\n");
    }

    @Override
    public void end() {
        buffer.append("@enduml").append("\n");
    }

    private void print(State state, State next) {
        if (next == null) {
            return;
        } else if (visited.contains(next)) {
            return;
        }
        visited.add(next);
        String stateName = name(state);
        String nextName = name(next);
        if (state instanceof State.NoneOrOne) {
            buffer.append(stateName).append(" --> ").append(nextName).append("\n");
            if (next.next != null) {
                buffer.append(stateName).append(" --> ").append(nextName).append("\n");
            }
        } else if (state instanceof State.OneOrMore) {
            buffer.append(stateName).append(" --> ").append(nextName).append("\n");
            if (next.next != null) {
                buffer.append(nextName).append(" --> ").append(stateName).append("\n");
            }
        } else if (state instanceof State.NoneOrMore) {
            buffer.append(stateName).append(" --> ").append(nextName).append("\n");
            buffer.append(nextName).append(" --> ").append(stateName).append("\n");
            if (next.next != null) {
                buffer.append(stateName).append(" --> ").append(nextName).append("\n");
            }
        } else {
            buffer.append(stateName).append(" --> ").append(nextName).append("\n");
        }
    }

    private void info(State state) {
        if (state == null || visitedInfo.contains(state)) {
            return;
        }
        visitedInfo.add(state);
        if (state instanceof State.Symbol symbol) {
            buffer.append(name(state)).append(": '").append(symbol.symbol).append("'\n");
        } else {
            buffer.append(name(state)).append(": '").append(state.getClass().getSimpleName()).append("'\n");
        }
    }

    private static String name(State state) {
        if (state == null) {
            return "[*]";
        }
        return String.format("S%02d", state.index);
    }
}
