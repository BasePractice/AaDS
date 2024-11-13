package ru.mifi.practice.vol2.yasymb;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface YaSymbol {

    Optional<Equation> process(String x, String y, String z);

    enum StateType {
        DEFINED, GENERATED
    }

    final class Metrics {
        private int operations;

        public Metrics() {
            this.operations = 0;
        }

        public Metrics(Metrics metrics) {
            operations = metrics.operations;
        }

        public int getOperations() {
            return operations;
        }
    }

    final class Context {
        final Map<Character, Integer> symbols;
        final Set<Integer> digits;
        final Metrics metrics;

        Context() {
            this.symbols = new HashMap<>();
            this.digits = new HashSet<>();
            this.metrics = new Metrics();
        }

        Context(Map<Character, Integer> symbols, Set<Integer> digits, Metrics metrics) {
            this.symbols = new HashMap<>(symbols);
            this.digits = new HashSet<>(digits);
            this.metrics = new Metrics(metrics);
        }

        @SuppressWarnings("PMD.AvoidBranchingStatementAsLastInLoop")
        Optional<State> next(Character symbol, int digit) {
            if (symbols.containsKey(symbol)) {
                return Optional.of(new State(StateType.DEFINED, symbols.get(symbol)));
            }
            for (int k = digit; k < 10; k++) {
                metrics.operations++;
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
                digits.remove(removed);
            }
        }

        String transform(String text) {
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < text.length(); i++) {
                char c = text.charAt(i);
                if (symbols.containsKey(c)) {
                    c = (char) (symbols.get(c) + '0');
                }
                result.append(c);
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
            ++metrics.operations;
        }

        public Context copy() {
            return new Context(symbols, digits, metrics);
        }
    }

    record State(StateType type, int digit) {
    }

    record Equation(String x, String y, String z, Metrics metrics) {
        void print() {
            int width = Math.max(x.length(), Math.max(y.length(), z.length()));
            System.out.printf("%" + width + "s + %n", x);
            System.out.printf("%" + width + "s%n", y);
            System.out.printf("%" + width + "s%n", z);
        }
    }
}
