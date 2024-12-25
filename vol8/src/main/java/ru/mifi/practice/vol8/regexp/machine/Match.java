package ru.mifi.practice.vol8.regexp.machine;

import ru.mifi.practice.vol8.regexp.machine.Input.StringInput;
import ru.mifi.practice.vol8.regexp.tree.Tree;

public interface Match {
    default boolean match(String text) {
        return match(new StringInput(text));
    }

    boolean match(Input input);

    final class Machine implements Match {
        private final State state;

        public Machine(Tree tree) {
            this(tree, new Manager.Default());
        }

        public Machine(Tree tree, Manager manager) {
            MachineGenerator generator = new MachineGenerator(manager);
            tree.visit(generator);
            this.state = generator.getState();
        }

        @Override
        public boolean match(Input input) {
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
