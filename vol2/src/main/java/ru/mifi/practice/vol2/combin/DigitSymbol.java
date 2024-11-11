package ru.mifi.practice.vol2.combin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface DigitSymbol {

    Optional<Equation<Number>> process(String x, String y, String z);

    enum StateType {
        DEFINED, GENERATED
    }

    final class Simple implements DigitSymbol {

        @SuppressWarnings("PMD.EmptyControlStatement")
        private static Optional<Context> process(Context context, Equation<String> input, int index, boolean carrier) {
            context.print(input);
            if (index > input.x.length() || index > input.y.length()) {
                var x = context.toNumber(input.x);
                var y = context.toNumber(input.y);
                var z = context.toNumber(input.z);
                if (x.isPresent() && y.isPresent() && z.isPresent()) {
                    if (x.get().intValue() + y.get().intValue() == z.get().intValue()) {
                        return Optional.of(context);
                    }
                    return Optional.empty();
                }
                var free = new HashSet<Integer>();
                for (int i = 0; i < 10; i++) {
                    if (context.digits.contains(i)) {
                        continue;
                    }
                    free.add(i);
                }
                var maxSize = Math.max(input.x.length(), Math.max(input.y.length(), input.z.length()));

                for (int i = 1; i <= maxSize; i++) {
                    Character cX = null;
                    Character cY = null;
                    Character cZ = null;
                    if (i <= input.x.length()) {
                        cX = input.x.charAt(input.x.length() - i);
                    }
                    if (i <= input.y.length()) {
                        cY = input.y.charAt(input.y.length() - i);
                    }
                    if (i <= input.z.length()) {
                        cZ = input.z.charAt(input.z.length() - i);
                    }
                    if (cX == null) {
                        if (cY != null && !Character.isDigit(cY) && cZ != null && !Character.isDigit(cZ)) {
                            for (Integer f : free) {
                                var v = f;
                                if (carrier) {
                                    f += 1;
                                    if (f < 10 && !context.digits.contains(f) && !context.digits.contains(v)) {
                                        context.assign(cY, v);
                                        context.assign(cZ, f);
                                        return Optional.of(context);
                                    }
                                }
                            }
                        } else if (cY != null && !Character.isDigit(cY) && cZ != null && Character.isDigit(cZ)) {
                            var nZ = context.symbols.get(cZ);
                            for (Integer f : free) {
                                var v = f;
                                if (carrier) {
                                    v += 1;
                                }
                                if (v < 10 && !context.digits.contains(v) && !v.equals(nZ)) {
                                    context.assign(cY, v);
                                    return Optional.of(context);
                                }
                            }
                        } else if (cY != null && Character.isDigit(cY) && cZ != null && !Character.isDigit(cZ)) {
                            var v = carrier ? context.symbols.get(cY) + 1 : context.symbols.get(cZ);
                            if (!context.digits.contains(v)) {
                                context.assign(cZ, v);
                                return Optional.of(context);
                            }
                        } else {
                            if (!context.digits.contains(1)) {
                                context.assign(cZ, 1);
                                return Optional.of(context);
                            }
                        }
                    } else if (cY == null) {
                        if (!Character.isDigit(cX) && cZ != null && !Character.isDigit(cZ)) {
                            for (Integer f : free) {
                                var v = f;
                                if (carrier) {
                                    f += 1;
                                    if (f < 10 && !context.digits.contains(f) && !context.digits.contains(v)) {
                                        context.assign(cX, v);
                                        context.assign(cZ, f);
                                        return Optional.of(context);
                                    }
                                }
                            }
                        } else if (!Character.isDigit(cX) && cZ != null && Character.isDigit(cZ)) {
                            var nZ = context.symbols.get(cZ);
                            for (Integer f : free) {
                                var v = f;
                                if (carrier) {
                                    v += 1;
                                }
                                if (v < 10 && !context.digits.contains(v) && !v.equals(nZ)) {
                                    context.assign(cX, v);
                                    return Optional.of(context);
                                }
                            }
                        } else if (Character.isDigit(cX) && cZ != null && !Character.isDigit(cZ)) {
                            var v = carrier ? context.symbols.get(cX) + 1 : context.symbols.get(cX);
                            if (!context.digits.contains(v)) {
                                context.assign(cZ, v);
                                return Optional.of(context);
                            }
                        } else {
                            if (!context.digits.contains(1)) {
                                context.assign(cZ, 1);
                                return Optional.of(context);
                            }
                        }
                    } else {
                        if (!context.digits.contains(1)) {
                            context.assign(cZ, 1);
                            return Optional.of(context);
                        }
                    }
                }
                return Optional.empty();
            }
            context = context.copy();
            int nX;
            int nY;
            int nZ;
            var xSymbol = input.x.charAt(input.x.length() - 1);
            var ySymbol = input.y.charAt(input.y.length() - 1);
            var zSymbol = input.z.charAt(input.z.length() - 1);
            for (nX = 0; nX < 10; nX++) {
                Optional<State> next = context.next(xSymbol, nX);
                if (next.isEmpty()) {
                    continue;
                }
                State xState = next.get();
                nX = xState.digit;
                context.assign(xSymbol, nX);
                for (nY = 0; nY < 10; nY++) {
                    next = context.next(ySymbol, nY);
                    if (next.isEmpty()) {
                        continue;
                    }
                    State yState = next.get();
                    nY = yState.digit;
                    context.assign(ySymbol, nY);
                    for (nZ = 0; nZ < 10; nZ++) {
                        next = context.next(zSymbol, nZ);
                        if (next.isEmpty()) {
                            continue;
                        }
                        State zState = next.get();
                        nZ = zState.digit;
                        context.assign(zSymbol, nZ);
                        var summary = nX + nY;
                        var needCarrier = carrier;
                        if (summary > 10) {
                            summary = summary % 10;
                            needCarrier = true;
                        }
                        Context copy = context.copy();
                        if (carrier && summary + 1 == nZ) {
                            var result = process(copy, input, index + 1, summary + 1 > 10);
                            if (result.isPresent()) {
                                return result;
                            }
                        } else if (summary == nZ) {
                            var result = process(copy, input, index + 1, needCarrier);
                            if (result.isPresent()) {
                                return result;
                            }
                        } else {
                            //TODO: Next
                        }

                        if (zState.type == StateType.GENERATED) {
                            context.reset(zSymbol);
                        } else {
                            break;
                        }
                    }
                    if (yState.type == StateType.GENERATED) {
                        context.reset(ySymbol);
                    } else {
                        break;
                    }
                }
                if (xState.type == StateType.GENERATED) {
                    context.reset(xSymbol);
                } else {
                    break;
                }
            }
            return Optional.empty();
        }

        @Override
        public Optional<Equation<Number>> process(String x, String y, String z) {
            var input = new Equation<>(x, y, z);
            var context = process(new Context(), input, 1, false);
            return context.map(value -> new Equation<>(
                value.toNumber(x).orElse(0),
                value.toNumber(y).orElse(0),
                value.toNumber(z).orElse(0)
            ));
        }
    }

    final class Context implements Cloneable {
        private final Map<Character, Integer> symbols;
        private final Set<Integer> digits;

        Context() {
            this.symbols = new HashMap<>();
            this.digits = new HashSet<>();
        }

        Context(Map<Character, Integer> symbols, Set<Integer> digits) {
            this.symbols = new HashMap<>(symbols);
            this.digits = new HashSet<>(digits);
        }

        @SuppressWarnings("PMD.AvoidBranchingStatementAsLastInLoop")
        private Optional<State> next(Character symbol, int digit) {
            if (symbols.containsKey(symbol)) {
                return Optional.of(new State(StateType.DEFINED, symbols.get(symbol)));
            }
            for (int k = digit; k < 10; k++) {
                if (digits.contains(k)) {
                    continue;
                }
                return Optional.of(new State(StateType.GENERATED, k));
            }
            return Optional.empty();
        }

        private void assign(Character symbol, int digit) {
            symbols.put(symbol, digit);
            digits.add(digit);
        }

        private void reset(Character symbol) {
            if (symbols.containsKey(symbol)) {
                Integer removed = symbols.remove(symbol);
                digits.remove(removed);
            }
        }

        private String transform(String text) {
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

        private Optional<Number> toNumber(String text) {
            String transformed = transform(text);
            try {
                return Optional.of(Long.valueOf(transformed));
            } catch (NumberFormatException e) {
                return Optional.empty();
            }
        }

        private void print(Equation<String> eq) {
            System.out.printf("%s + %s = %s%n", transform(eq.x), transform(eq.y), transform(eq.z));
        }

        @Override
        public Context clone() {
            try {
                return (Context) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }

        public Context copy() {
            return new Context(symbols, digits);
        }
    }

    record State(StateType type, int digit) {
    }

    record Equation<T>(T x, T y, T z) {

    }
}
