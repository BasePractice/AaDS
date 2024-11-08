package ru.mifi.practice.vol1.cbs;

import java.util.Optional;
import java.util.Stack;

public interface CorrectBriceSequence {

    boolean isCorrect(String input);

    enum Kind {
        OPEN, CLOSE, NONE
    }

    enum Type {
        SIMPLE('(', ')'),
        FIGURE('{', '}'),
        ;

        private final int open;
        private final int closed;

        Type(int open, int closed) {
            this.open = open;
            this.closed = closed;
        }

        static Optional<Brice> is(int character) {
            for (Type type : values()) {
                if (type.open == character) {
                    return Optional.of(new Brice(type, Kind.OPEN));
                } else if (type.closed == character) {
                    return Optional.of(new Brice(type, Kind.CLOSE));
                }
            }
            return Optional.empty();
        }
    }

    @SuppressWarnings("PMD.LooseCoupling")
    final class Default implements CorrectBriceSequence {

        @Override
        public boolean isCorrect(String input) {
            Stack<Brice> stack = new Stack<>();
            int length = input.length();
            for (int i = 0; i < length; i++) {
                char c = input.charAt(i);
                Optional<Brice> is = Type.is(c);
                if (is.isPresent()) {
                    Brice brice = is.get();
                    if (brice.kind == Kind.OPEN) {
                        stack.push(brice);
                    } else if (brice.kind == Kind.CLOSE) {
                        Brice peek = stack.peek();
                        if (peek.kind == Kind.OPEN && peek.type == brice.type) {
                            stack.pop();
                        } else {
                            System.err.println();
                            return false;
                        }
                    }
                }
            }
            return stack.isEmpty();
        }
    }

    record Brice(Type type, Kind kind) {

    }
}
