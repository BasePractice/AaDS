package ru.mifi.practice.vol8.regexp.machine;

import ru.mifi.practice.vol8.regexp.tree.Tree;

public interface Match {
    boolean match(String text);

    final class Machine implements Match {
        private final State state;

        public Machine(Tree tree) {
            MachineGenerator generator = new MachineGenerator();
            tree.visit(generator);
            this.state = generator.getState();
        }

        @Override
        public boolean match(String text) {
            Input input = new Input.StringInput(text);
            State.Match match = match(state, input);
            return match.ok() && match.isCompleted();
        }

        private static State.Match match(State state, Input input) {
            if (state.accept(input)) {
                return state.match(input);
            }
            return new State.Match(false, input.copy());
        }
    }
}
