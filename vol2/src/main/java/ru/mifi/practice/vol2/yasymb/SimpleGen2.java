package ru.mifi.practice.vol2.yasymb;

import java.util.HashSet;
import java.util.Optional;

public final class SimpleGen2 implements YaSymbol {
    @SuppressWarnings("PMD.EmptyControlStatement")
    private static Optional<Context> process(Context context, Equation input, int index, boolean carrier) {
        //var xText = context.transform(input.x());
        //var yText = context.transform(input.y());
        //var zText = context.transform(input.z());
        context.print(input);
        if (index > input.x().length() || index > input.y().length()) {
            var x = context.toNumber(input.x());
            var y = context.toNumber(input.y());
            var z = context.toNumber(input.z());
            if (x.isPresent() && y.isPresent() && z.isPresent()) {
                if (x.get().intValue() + y.get().intValue() == z.get().intValue()) {
                    return Optional.of(context);
                }
                return Optional.empty();
            }
            var free = new HashSet<Integer>();
            for (int i = 0; i < 10; i++) {
                context.addOperation();
                if (context.digits.contains(i)) {
                    continue;
                }
                free.add(i);
            }
            var maxSize = Math.max(input.x().length(), Math.max(input.y().length(), input.z().length()));

            for (int i = 1; i <= maxSize && !free.isEmpty(); i++) {
                context.addOperation();
                Character cX = null;
                Character cY = null;
                Character cZ = null;
                if (i <= input.x().length()) {
                    cX = input.x().charAt(input.x().length() - i);
                }
                if (i <= input.y().length()) {
                    cY = input.y().charAt(input.y().length() - i);
                }
                if (i <= input.z().length()) {
                    cZ = input.z().charAt(input.z().length() - i);
                }
                if (cX == null) {
                    if (cY != null && !Character.isDigit(cY) && cZ != null && !Character.isDigit(cZ)) {
                        for (Integer f : free) {
                            context.addOperation();
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
                            context.addOperation();
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
                            context.addOperation();
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
                            context.addOperation();
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
                } else if (cZ == null) {
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
        var xSymbol = input.x().charAt(input.x().length() - index);
        var ySymbol = input.y().charAt(input.y().length() - index);
        var zSymbol = input.z().charAt(input.z().length() - index);
        for (nX = 0; nX < 10; nX++) {
            context.addOperation();
            Optional<State> next = context.next(xSymbol, nX);
            if (next.isEmpty()) {
                continue;
            }
            State xState = next.get();
            nX = xState.digit();
            context.assign(xSymbol, nX);
            boolean nextIt = true;
            for (nY = 0; nY < 10 && nextIt; nY++) {
                context.addOperation();
                next = context.next(ySymbol, nY);
                if (next.isEmpty()) {
                    continue;
                }
                State yState = next.get();
                nY = yState.digit();
                context.assign(ySymbol, nY);


                var nZ = nX + nY;
                var summary = nZ;
                if (summary == 0) {
                    nextIt = false;
                    if (yState.type() == StateType.GENERATED) {
                        context.reset(ySymbol);
                    }
                    continue;
                }
                var needCarrier = carrier;
                if (summary >= 10) {
                    summary = summary % 10;
                    needCarrier = true;
                }

                if (context.symbols.containsKey(zSymbol)) {
                    Optional<State> nexted = context.next(zSymbol, summary);
                    if (nexted.isPresent() && nexted.get().digit() != summary) {
                        if (yState.type() == StateType.GENERATED) {
                            context.reset(ySymbol);
                        }
                        nextIt = nX != nY;
                        continue;
                    }
                } else if (context.digits.contains(summary)) {
                    if (zSymbol != xSymbol && zSymbol != ySymbol) {
                        if (yState.type() == StateType.GENERATED) {
                            context.reset(ySymbol);
                        }
                        nextIt = nX != nY;
                        continue;
                    }
                }
                context.assign(zSymbol, nZ);
                Context copy = context.copy();
                Optional<Context> result;
                if (carrier && summary + 1 == nZ) {
                    result = process(copy, input, index + 1, summary + 1 >= 10);
                } else if (summary == nZ) {
                    result = process(copy, input, index + 1, needCarrier);
                } else {
                    result = Optional.empty();
                }
                if (result.isPresent()) {
                    return result;
                }

                if (yState.type() == StateType.GENERATED) {
                    context.reset(ySymbol);
                } else {
                    break;
                }
            }
            if (xState.type() == StateType.GENERATED) {
                context.reset(xSymbol);
            } else {
                break;
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Equation> process(String x, String y, String z) {
        var input = new Equation(x, y, z, new Metrics());
        var context = process(new Context(), input, 1, false);
        return context.map(value -> new Equation(
            value.transform(x),
            value.transform(y),
            value.transform(z),
            value.metrics
        ));
    }
}
