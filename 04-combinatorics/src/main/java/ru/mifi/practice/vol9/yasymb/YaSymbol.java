package ru.mifi.practice.vol9.yasymb;

import ru.mifi.practice.commons.Counter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/** Решение буквенно-цифрового ребуса сложения x + y = z. */
public interface YaSymbol {
    Optional<Equation> process(String x, String y, String z, Counter counter);

    default Optional<Equation> process(String x, String y, String z) {
        return process(x, y, z, Counter.create());
    }

    enum StateType {
        DEFINED, GENERATED
    }

    final class Context {
        final Map<Character, Integer> symbols;
        final Set<Integer> digits;
        final Counter counter;

        Context() {
            this(Counter.create());
        }

        Context(Counter counter) {
            this(new HashMap<>(), new HashSet<>(), counter);
        }

        Context(Map<Character, Integer> symbols, Set<Integer> digits, Counter counter) {
            this.symbols = new HashMap<>(symbols);
            this.digits = new HashSet<>(digits);
            this.counter = counter;
        }

        @SuppressWarnings("PMD.AvoidBranchingStatementAsLastInLoop")
        Optional<State> next(Character symbol, int digit) {
            if (symbols.containsKey(symbol)) {
                return Optional.of(new State(StateType.DEFINED, symbols.get(symbol)));
            }
            for (int k = digit; k < 10; k++) {
                counter.increment();
                if (digits.contains(k)) {
                    continue;
                }
                return Optional.of(new State(StateType.GENERATED, k));
            }
            return Optional.empty();
        }

        void assign(Character symbol, int digit) {
            symbols.put(symbol, digit);
            digits.add(digit);
        }

        void reset(Character symbol) {
            if (symbols.containsKey(symbol)) {
                Integer removed = symbols.remove(symbol);
                if (removed != null) {
                    boolean contains = symbols.containsValue(removed);
                    if (contains) {
                        return;
                    }
                }
                digits.remove(removed);
            }
        }

        String transform(String text) {
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < text.length(); i++) {
                char character = text.charAt(i);
                if (symbols.containsKey(character)) {
                    character = (char) (symbols.get(character) + '0');
                }
                result.append(character);
            }
            return result.toString();
        }

        Optional<Number> toNumber(String text) {
            String transformed = transform(text);
            try {
                return Optional.of(Long.valueOf(transformed));
            } catch (NumberFormatException e) {
                return Optional.empty();
            }
        }

        @SuppressWarnings("PMD.UnusedPrivateMethod")
        void print(Equation eq) {
            System.out.printf("%s + %s = %s%n", transform(eq.x), transform(eq.y), transform(eq.z));
        }

        void addOperation() {
            counter.increment();
        }

        public Context copy() {
            return new Context(symbols, digits, counter);
        }
    }

    record State(StateType type, int digit) {
    }

    record Equation(String x, String y, String z, Counter counter) {
        void print() {
            int width = Math.max(x.length(), Math.max(y.length(), z.length()));
            System.out.printf("%" + width + "s + %n", x);
            System.out.printf("%" + width + "s%n", y);
            System.out.printf("%" + width + "s%n", z);
        }
    }
}
