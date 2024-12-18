package ru.mifi.practice.vol8.regexp;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import ru.mifi.practice.vol8.regexp.visitor.MatchGenerator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static ru.mifi.practice.vol8.regexp.Mach.Input.StringInput;

public interface Mach {

    static Mach of(State start) {
        return new DefaultMach(start);
    }

    static State matched(char character) {
        return new Matched(character);
    }

    static State sequence() {
        return new Sequence();
    }

    static State epsilon(boolean isEnd) {
        return new Epsilon(isEnd);
    }

    static State epsilon() {
        return epsilon(false);
    }

    boolean match(String input);

    enum Match {
        MATCHED, SKIPPED, UNMATCHED
    }

    enum Type {
        SEQUENCE, PARALLELISM
    }

    interface Input {

        void mark();

        void reset();

        Optional<Character> peek();

        void next();

        final class StringInput implements Input {
            private final char[] chars;
            private int it;
            private int mark;

            public StringInput(String text) {
                this.chars = text.toCharArray();
            }

            @Override
            public void mark() {
                mark = it;
            }

            @Override
            public void reset() {
                it = mark;
                mark = 0;
            }

            @Override
            public Optional<Character> peek() {
                if (it >= chars.length) {
                    return Optional.empty();
                }
                return Optional.of(chars[it]);
            }

            @Override
            public void next() {
                if (it < chars.length) {
                    it++;
                }
            }
        }
    }

    @UtilityClass
    final class Compiler {
        public Mach compile(String pattern) {
            MatchGenerator generator = new MatchGenerator();
            new Tree.Default(pattern).visit(generator);
            return generator.getMach();
        }
    }

    final class DefaultMach implements Mach {
        private final State start;

        private DefaultMach(State start) {
            this.start = start;
        }

        @Override
        public boolean match(String text) {
            StringInput input = new StringInput(text);
            return new Result(Match.MATCHED, start).match(input);
        }
    }

    @SuppressWarnings("PMD.UnusedPrivateMethod")
    abstract class State {
        protected final List<State> transitions = new ArrayList<>();

        protected static String toString(State print, Set<State> visited) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < print.transitions.size(); i++) {
                State state = print.transitions.get(i);
                if (visited.contains(state)) {
                    continue;
                }
                visited.add(state);
                if (i > 0) {
                    builder.append(",");
                }
                builder.append("->").append(state.toString());
            }
            return builder.toString();
        }

        @Override
        public String toString() {
            return toString(this, new HashSet<>());
        }

        public void add(State state) {
            transitions.add(state);
        }

        protected abstract Match match(Input input);

        protected abstract Result next(Input input);

        public boolean isEnd() {
            return false;
        }
    }

    final class Sequence extends State {
        private Sequence() {
            //Nothing
        }

        @Override
        protected Match match(Input input) {
            return Match.SKIPPED;
        }

        @Override
        protected Result next(Input input) {
            Iterator<State> it = transitions.iterator();
            while (it.hasNext()) {
                State state = it.next();
                Result result = state.next(input);
                if (result.match == Match.SKIPPED || result.match == Match.MATCHED) {
                    it.remove();
                } else {
                    return new Result(Match.UNMATCHED);
                }
            }
            return new Result(Match.MATCHED);
        }
    }

    @Getter
    final class Block extends State {
        private final State start = epsilon();
        private final State end = epsilon(false);
        private State current = start;

        @Override
        protected Match match(Input input) {
            return current.match(input);
        }

        @Override
        protected Result next(Input input) {
            throw new UnsupportedOperationException();
        }
    }

    final class Epsilon extends State {
        private final boolean isEnd;

        private Epsilon(boolean isEnd) {
            this.isEnd = isEnd;
        }

        private Epsilon() {
            this(false);
        }

        @Override
        public boolean isEnd() {
            return isEnd;
        }

        @Override
        protected Match match(Input input) {
            return Match.SKIPPED;
        }

        @Override
        protected Result next(Input input) {
            return new Result(Match.SKIPPED, transitions.toArray(new State[0]));
        }

        @Override
        public String toString() {
            return isEnd ? "{END}" : toString(this, new HashSet<>());
        }
    }

    final class Matched extends State {
        private final Character character;

        private Matched(Character character) {
            this.character = character;
        }

        @Override
        protected Match match(Input input) {
            return input.peek().filter(c -> c.equals(character))
                .map(c -> Match.MATCHED).orElse(Match.UNMATCHED);
        }

        @Override
        protected Result next(Input input) {
            List<State> states = new ArrayList<>();
            transitions.forEach(t -> {
                Match match = t.match(input);
                if (match == Match.MATCHED || match == Match.SKIPPED) {
                    states.add(t);
                }
            });
            return new Result(states.isEmpty() ? Match.UNMATCHED : Match.MATCHED, states.toArray(new State[0]));
        }

        @Override
        public String toString() {
            return character.toString() + toString(this, new HashSet<>());
        }
    }

    record Result(Match match, State... next) {
        boolean match(Input input) {
            Set<State> states = new HashSet<>();
            for (State state : next) {
                Match matched = state.match(input);
                if (matched == Match.MATCHED || matched == Match.SKIPPED) {
                    states.add(state);
                }
            }
            Set<Result> results = new HashSet<>();
            for (State state : states) {
                input.mark();
                Result result = state.next(input);
                input.reset();
                if (result.match == Match.SKIPPED || result.match == Match.MATCHED) {
                    results.add(result);
                }
            }
            return results.stream().anyMatch(r -> r.match(input));
        }
    }
}
