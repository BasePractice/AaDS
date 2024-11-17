package ru.mifi.practice.vol2.yasymb.stage2;

import java.util.ArrayDeque;
import java.util.Deque;

public interface Stage2 {

    static boolean start(String x, String y, String z, boolean debug) {
        Solution solution = new Solution(x, y, z);
        solution.debug = debug;
        System.out.println(solution);
        boolean executed = solution.execute();
        System.out.println("RES: " + (executed ? "SUCCESS" : "FAILURE"));
        System.out.println("GEN: " + solution.generation);
        if (executed) {
            System.out.println(solution);
        }
        System.out.println("==================");
        return executed;
    }

    static void simple(boolean debug) {
//        start("x", "y", "z", debug);
//        start("win", "lose", "game", debug);
//        start("love", "hate", "feel", debug);
        start("four", "seven", "eight", debug);
//        start("a", "b", "a", debug);
//        start("odin", "odin", "mnogo", debug);
//        start("acdf", "adbg", "baeg", debug);
//        start("accb", "adeg", "bcfe", debug);
//        start("acef", "abfg", "bdcf", debug);
    }

    static void generated(boolean debug) {
        Generator generator = new Generator(4, 4);
        for (int i = 0; i < 1; i++) {
            Generator.Result generated = generator.generate();
            Generator.Equation letters = generated.letters();
            boolean ok = start(letters.x(), letters.y(), letters.z(), debug);
            if (!ok) {
                System.out.println(generated.math().toString());
            }
        }
    }

    static void main(String[] args) {
        boolean debug = false;
        simple(debug);
//        generated(debug);
    }

    final class Solution {
        final Step[] steps;
        int step;
        int generation;
        boolean debug;

        public Solution(String x, String y, String z) {
            Deque<Slice> pipe = new ArrayDeque<>();
            Equation equation = new Equation(x, y, z);
            int index = 1;
            while (true) {
                Slice slice = equation.of(index);
                if (slice.isEmpty()) {
                    break;
                }
                pipe.push(slice);
                ++index;
            }
            steps = new Step[pipe.size()];
            Step parent = null;
            index = 0;
            while (!pipe.isEmpty()) {
                Slice slice = pipe.pollLast();
                Step next = new Step(this, parent, slice);
                if (parent != null) {
                    parent.hasNext = true;
                }
                steps[index++] = next;
                parent = next;
            }
        }

        boolean execute() {
            generation = 0;
            while (step >= 0 && step < steps.length) {
                Step current = steps[step];
                if (current.up) {
                    --step;
                    current.up = false;
                    continue;
                }
                boolean result = current.refresh().step();
                if (result) {
                    ++step;
                } else {
                    current.reset();
                    --step;
                }
                ++generation;
                if (generation > 10000) {
                    return false;
                }
                if (debug) {
                    System.out.println("GEN: " + generation);
                    System.out.println(this);
                }
            }
            return steps[steps.length - 1].isComplete() && hardValidate();
        }

        private boolean hardValidate() {
            return Integer.parseInt(x().trim()) + Integer.parseInt(y().trim()) == Integer.parseInt(z().trim());
        }

        public boolean isComplete() {
            for (Step step : steps) {
                if (step.x == -1 || step.y == -1 || step.z == -1) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public String toString() {
            return x() + "\n" + y() + "\n" + z();
        }

        private String x() {
            StringBuilder b = new StringBuilder();
            for (int i = steps.length - 1; i >= 0; i--) {
                b.append(steps[i].trX());
            }
            return b.toString();
        }

        private String y() {
            StringBuilder b = new StringBuilder();
            for (int i = steps.length - 1; i >= 0; i--) {
                b.append(steps[i].trY());
            }
            return b.toString();
        }

        private String z() {
            StringBuilder b = new StringBuilder();
            for (int i = steps.length - 1; i >= 0; i--) {
                b.append(steps[i].trZ());
            }
            return b.toString();
        }
    }

    final class Step {
        final Solution solution;
        final Step parent;
        final Slice slice;
        boolean hasNext;
        int x = -1;
        boolean hasX = false;
        int y = -1;
        boolean hasY = false;
        int z = -1;
        boolean hasZ = false;
        boolean carrier;
        boolean needCarrier;
        boolean up;

        public Step(Solution solution, Step parent, Slice slice) {
            this.solution = solution;
            this.parent = parent;
            this.slice = slice;
            refresh();
        }

        private static char tr(char c, int n) {
            if (c == 0) {
                return ' ';
            }
            if (n == -1) {
                return c;
            }
            return (char) (n + '0');
        }

        void reset() {
            carrier = false;
            needCarrier = false;
            x = -1;
            y = -1;
            z = -1;
            hasX = false;
            hasY = false;
            hasZ = false;
        }

        Step refresh() {
            carrier = false;
            needCarrier = false;
            if (parent != null) {
                int t = parent.numberOf(slice.x);
                if (t >= 0) {
                    x = t;
                    hasX = true;
                }
                t = parent.numberOf(slice.y);
                if (t >= 0) {
                    y = t;
                    hasY = true;
                }
                t = parent.numberOf(slice.z);
                if (t >= 0) {
                    z = t;
                    hasZ = true;
                } else {
                    z = -1;
                }
                carrier = parent.needCarrier;
            }
            return this;
        }

        private int numberOf(char ch) {
            if (ch == 0) {
                return -1;
            }
            if (slice.x == ch) {
                return x;
            } else if (slice.y == ch) {
                return y;
            } else if (slice.z == ch) {
                return z;
            }
            if (parent != null) {
                return parent.numberOf(ch);
            }
            return -1;
        }

        private char trX() {
            return tr(slice.x, x);
        }

        private char trY() {
            return tr(slice.y, y);
        }

        private char trZ() {
            return tr(slice.z, z);
        }

        boolean isComplete() {
            return (slice.x == 0 || x != -1)
                && (slice.y == 0 || y != -1) && z != -1;
        }

        @Override
        public String toString() {
            return String.valueOf(trX()) + trY() + trZ();
        }

        public boolean step() {
            if (hasX && hasY && hasZ) {
                int sum = sum(x, y);
                if (sum >= 10) {
                    needCarrier = true;
                }
                if (needCarrier && !hasNext) {
                    return false;
                }
                return sum % 10 == z;
            }
            boolean xChanged = false;
            boolean nextGeneration = true;
            while (true) {
                //FIXME: Грязный хак
                if (solution.isComplete()) {
                    return true;
                }

                if (nextGeneration && !hasX && slice.x != 0 && (y == -1 || hasY)) {
                    xChanged = true;
                    nextGeneration = false;
                    x = nextNumber(!hasNext && x == -1 ? 0 : x);
                    if (x == -1) {
                        break;
                    }
                }

                if (xChanged) {
                    xChanged = false;
                    if (!hasY) {
                        if (slice.y != 0 && slice.y == slice.x) {
                            y = x;
                        } else {
                            y = nextNumber(hasNext ? y : 1);
                        }
                    }
                } else {
                    if (!hasY) {
                        if (slice.y != 0 && slice.y == slice.x) {
                            if (y == x) {
                                y = -1;
                                nextGeneration = true;
                                needCarrier = false;
                                continue;
                            }
                            y = x;
                        } else if (slice.y == 0) {
                            y = -1;
                        } else {
                            y = nextNumber(!hasNext && y == -1 ? 0 : y);
                        }
                    }
                }
                if (!hasZ) {
                    z = -1;
                }
                if (y == -1) {
                    if (slice.x == 0 && slice.y != 0) {
                        return false;
                    } else if (hasX) {
                        return false;
                    } else if (slice.x == 0) {
                        if (carrier) {
                            z = 1;
                            return true;
                        }
                        if (parent != null) {
                            parent.up = true;
                        }
                        return false;
                    }
                    nextGeneration = true;
                    continue;
                }

                int nX = x;
                if (slice.x == 0) {
                    nX = 0;
                }
                int nY = y;
                if (slice.y == 0) {
                    nY = 0;
                }

                int sum = sum(nX, nY);
                if (!hasZ) {
                    if (slice.z == slice.x) {
                        if (sum != x) {
                            z = -1;
                            y = -1;
                            needCarrier = false;
                            nextGeneration = true;
                            continue;
                        }
                        z = x;
                    } else if (slice.z == slice.y) {
                        if (sum != y) {
                            z = -1;
                            needCarrier = false;
                            if (!hasX) {
                                y = -1;
                                nextGeneration = true;
                            }
                            continue;
                        }
                        z = y;
                    } else if (sum >= 10) {
                        z = sum % 10;
                        needCarrier = true;
                    } else {
                        z = sum;
                    }

                    if (z == x && slice.z != slice.x && !carrier) {
                        if (slice.y == slice.x) {
                            y = -1;
                            nextGeneration = true;
                        } else if (hasY) {
                            nextGeneration = true;
                        }
                        continue;
                    } else if (z == y && slice.z != slice.y) {
                        if (hasY) {
                            if (hasX) {
                                return false;
                            }
                            nextGeneration = true;
                        }
                        continue;
                    } else if (z == x && z == y) {
                        y = -1;
                        nextGeneration = true;
                        continue;
                    } else if (parent != null && parent.hasNumber(z)) {
                        if (slice.x == slice.y || hasY) {
                            y = -1;
                            nextGeneration = true;
                        }
                        z = -1;
                        needCarrier = false;
                        continue;
                    }
                } else {
                    needCarrier = sum >= 10;
                }


                if (sum % 10 == z) {
                    if (slice.x == 0) {
                        x = -1;
                    }
                    if (slice.y == 0) {
                        y = -1;
                    }
                    if (needCarrier) {
                        if (hasNext) {
                            return true;
                        } else if (hasZ && slice.x == slice.y) {
                            if (!hasY) {
                                y = -1;
                            }
                            nextGeneration = true;
                        } else if (hasX && hasY) {
                            return false;
                        }
                        z = -1;
                    } else {
                        return true;
                    }
                } else if (slice.x == slice.y) {
                    y = -1;
                    nextGeneration = true;
                } else if (hasY) {
                    nextGeneration = true;
                }
                needCarrier = false;
            }
            return false;
        }

        private boolean hasNumber(int n) {
            if (z == n || x == n || y == n) {
                return true;
            } else if (parent != null) {
                return parent.hasNumber(n);
            }
            return false;
        }

        private int sum(int x, int y) {
            return x + y + (carrier ? 1 : 0);
        }

        @SuppressWarnings("PMD.AvoidBranchingStatementAsLastInLoop")
        private int nextNumber(int n) {
            int search = n + 1;
            while (search < 10) {
                if (search == x || search == y || search == z) {
                    ++search;
                    continue;
                } else {
                    if (parent != null) {
                        int last = search;
                        search = parent.nextNumber(last - 1);
                        if (last == search) {
                            return search;
                        } else if (search == -1) {
                            return -1;
                        }
                        continue;
                    }
                }
                return search;
            }
            return -1;
        }
    }

    record Slice(char x, char y, char z) {
        boolean isEmpty() {
            return x() == 0 && y() == 0 && z() == 0;
        }
    }

    final class Equation {
        private final char[] x;
        private final char[] y;
        private final char[] z;

        public Equation(String x, String y, String z) {
            this.x = x.toCharArray();
            this.y = y.toCharArray();
            this.z = z.toCharArray();
        }

        public Slice of(int index) {
            char x = 0;
            if (this.x.length > index - 1) {
                x = this.x[this.x.length - index];
            }
            char y = 0;
            if (this.y.length > index - 1) {
                y = this.y[this.y.length - index];
            }
            char z = 0;
            if (this.z.length > index - 1) {
                z = this.z[this.z.length - index];
            }
            return new Slice(x, y, z);
        }
    }
}
