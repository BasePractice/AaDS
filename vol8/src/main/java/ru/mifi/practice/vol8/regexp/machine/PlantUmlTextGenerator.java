package ru.mifi.practice.vol8.regexp.machine;

import java.util.HashSet;
import java.util.Set;

public final class PlantUmlTextGenerator extends Visitor.AbstractStringVisitor {
    private final Set<State> visited = new HashSet<>();
    private final Set<String> printed = new HashSet<>();
    private final Set<String> declared = new HashSet<>();

    private String name(String name) {
        if (!declared.contains(name)) {
            declared.add(name);
            buffer.append("state \"Epsilon\" as ").append(name).append("\n");
        }
        return name;
    }

    private String name(State state) {
        if (state == null) {
            return "[*]";
        }
        String name = String.format("S%02d", state.index);
        if (!declared.contains(name)) {
            declared.add(name);
            if (state instanceof State.Symbol symbol) {
                buffer.append("state \"").append(symbol.symbol).append("\" as ").append(name).append("\n");
            } else {
                buffer.append("state \"Epsilon\" as ").append(name).append("\n");
            }
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
            String stateName = name(state);
            print(stateName, "[*]");
            return;
        } else if (visited.contains(next)) {
            return;
        }
        visited.add(next);
        String stateName = name(state);
        String nextName = name(next);
        print(stateName, nextName);
        if (state instanceof State.NoneOrOne) {
            print(nextName, name(state.next));
            print(stateName, name(state.next));
        } else if (state instanceof State.OneOrMore) {
            String middleName = name(stateName + nextName);
            print(nextName, middleName);
            print(middleName, name(state.next));
            print(middleName, stateName);
            print(middleName, nextName);
        } else if (state instanceof State.NoneOrMore) {
            print(stateName, name(state.next));
            print(nextName, nextName);
            print(nextName, name(state.next));
            print(name(state.next), stateName);
        }
    }

    private void print(String start, String end) {
        String text = start + " --> " + end;
        if (printed.contains(text)) {
            return;
        }
        printed.add(text);
        buffer.append(text).append("\n");
    }
}
