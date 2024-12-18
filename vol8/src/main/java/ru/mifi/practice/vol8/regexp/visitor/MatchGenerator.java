package ru.mifi.practice.vol8.regexp.visitor;

import ru.mifi.practice.vol8.regexp.Mach;

public final class MatchGenerator extends AbstractVisitor {
    private Mach.State current;

    @Override
    public void start() {
        current = new Mach.Sequence();
    }

    public Mach getMach() {
        return Mach.of(current);
    }
}
