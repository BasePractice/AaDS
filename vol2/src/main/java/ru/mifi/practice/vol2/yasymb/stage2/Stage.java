package ru.mifi.practice.vol2.yasymb.stage2;

import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings({"PMD.UnusedPrivateField", "PMD.AvoidBranchingStatementAsLastInLoop"})
public interface Stage {

    private static Generation process(String x, String y, String z, boolean debug) {
        Generation generation = new Generation(x.toCharArray(), y.toCharArray(), z.toCharArray());
        Stepper stepper = new Stepper.DefaultStepper();
        if (debug) {
            System.out.println(generation);
        }
        while (true) {
            boolean permutation = generation.permutation(stepper);
            if (!permutation) {
                if (!generation.hasParent()) {
                    break;
                } else if (generation.isComplete()) {
                    break;
                }
                generation = generation.up().reset();
                continue;
            }
            if (generation.isComplete()) {
                break;
            } else if (generation.hasNext()) {
                generation = generation.newStage();
                continue;
            } else if (generation.isCarrier()) {
                boolean carrier = generation.tryCarrier();
                if (carrier) {
                    break;
                }
                generation = generation.up().reset();
                continue;
            } else if (!generation.isComplete() && generation.hasParent()) {
                generation = generation.up().reset();
                continue;
            }
            if (debug) {
                System.out.println("Has no permutation");
            }
            break;
        }
        if (debug) {
            System.out.printf("-----%5d-----%n", stepper.stepCount());
            System.out.println(generation);
            System.out.println("===============");
        }
        return generation;
    }

    private static void start(String x, String y, String z) {
        process(x, y, z, true);
    }

    static void main(String[] args) {
        start("x", "y", "z");
        start("win", "lose", "game");
        start("love", "hate", "feel");
        start("four", "seven", "eight");
        start("a", "b", "a");
        start("odin", "odin", "mnogo");
//        Generator generator = new Generator(4, 4);
//        for (int i = 0; i < 1; i++) {
//            Generator.Result generated = generator.generate();
//            Generator.Equation letters = generated.letters();
//            Generation generation = process(letters.x(), letters.y(), letters.z(), true);
//            if (generation.isComplete()) {
//                System.out.println("SUCCESS");
//            } else {
//                System.out.println("FAILURE");
//            }
//        }
    }

    interface Stepper {
        void step();

        int stepCount();

        final class DefaultStepper implements Stepper {
            private final AtomicInteger steps = new AtomicInteger();

            @Override
            public void step() {
                steps.incrementAndGet();
            }

            @Override
            public int stepCount() {
                return steps.intValue();
            }
        }
    }

    final class Generation {
        private final char[] numbers;
        private final Generation parent;
        private final int index;
        private final char[] x;
        private final char[] y;
        private final char[] z;
        private final boolean carrier;
        //Не изменяемые
        private final int cx;
        private final int cy;
        private final int cz;
        //Изменяемые
        private int gx;
        private int gy;
        private int gz;
        //Итеративные
        private int ix;

        public Generation(char[] x, char[] y, char[] z) {
            this(new char[10], null, 1, x, y, z, false);
        }

        private Generation(char[] numbers,
                           Generation parent, int index, char[] x, char[] y, char[] z, boolean carrier) {
            this.numbers = numbers;
            this.parent = parent;
            this.index = index;
            this.x = x;
            this.y = y;
            this.z = z;
            this.carrier = carrier;
            this.gx = this.cx = number(numbers, x, index);
            this.gy = this.cy = number(numbers, y, index);
            this.gz = this.cz = number(numbers, z, index);
        }

        private static int number(char[] numbers, char[] chars, int index) {
            if (chars.length < index) {
                return -1;
            }
            return indexOf(numbers, chars[chars.length - index]);
        }

        public static int indexOf(char[] numbers, char ch) {
            for (int i = 0; i < numbers.length; i++) {
                if (numbers[i] == ch) {
                    return i;
                }
            }
            return -1;
        }

        private static int nextNumber(char[] numbers, int cx, int cy, int cz, int n) {
            for (int i = n; i < numbers.length; i++) {
                if (numbers[i] == 0 && i != cx && i != cy && i != cz) {
                    return i;
                }
            }
            return -1;
        }

        private static boolean hasLetter(String transmute) {
            for (int i = 0; i < transmute.length(); i++) {
                if (Character.isLetter(transmute.charAt(i))) {
                    return true;
                }
            }
            return false;
        }

        private static boolean isComplete(char x, char y, char z, int cx, int cy, int cz) {
            if (cx == cy && x != y) {
                return false;
            } else if (cx == cz && x != z) {
                return false;
            } else if (cy == cz && y != z) {
                return false;
            } else if (x == z && cx != cz) {
                return false;
            } else if (y == z && cy != cz) {
                return false;
            }
            return true;
        }

        public boolean isComplete() {
            return !hasLetter(transmute(x)) && !hasLetter(transmute(y)) && !hasLetter(transmute(z));
        }

        private String transmute(char[] chars) {
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
            StringBuilder sb = new StringBuilder();
            for (char ch : chars) {
                if (ch == x && gx != -1) {
                    sb.append(gx);
                    continue;
                }
                if (ch == y && gy != -1) {
                    sb.append(gy);
                    continue;
                }
                if (ch == z && gz != -1) {
                    sb.append(gz);
                    continue;
                }
                int indexed = indexOf(numbers, ch);
                if (indexed >= 0) {
                    sb.append(indexed);
                } else {
                    sb.append(ch);
                }
            }
            return sb.toString();
        }

        private Generation reset() {
            gx = -1;
            gy = -1;
            gz = -1;
            return this;
        }

        public boolean permutation(Stepper stepper) {
            char x = 0;
            if (this.x.length > index - 1) {
                x = this.x[this.x.length - index];
            } else if (!carrier) {
                return false;
            }
            char y = 0;
            if (this.y.length > index - 1) {
                y = this.y[this.y.length - index];
            } else if (!carrier) {
                return false;
            }
            char z;
            if (this.z.length > index - 1) {
                z = this.z[this.z.length - index];
            } else {
                System.err.println("Такого быть не может");
                return false;
            }

            while (true) {
                int cxn = cx == -1 ? gx : cx;
                int cyn = cy == -1 ? gy : cy;
                int czn = cz == -1 ? gz : cz;
                if (cxn == -1 && x != 0) {
                    if (this.x.length == index && ix == 0) {
                        ix = 1;
                    }
                    cxn = nextNumber(numbers, cxn, cyn, czn, ix);
                    if (cxn == -1) {
                        stepper.step();
                        return false;
                    }
                } else if (x == 0) {
                    cxn = 0;
                }
                ix = cxn;
                for (int iy = 0; iy < 10; iy++) {
                    stepper.step();
                    if (x == y) {
                        cyn = cxn;
                    }
                    if (cyn == -1) {
                        if (this.y.length == index && iy == 0) {
                            iy = 1;
                        }
                        cyn = nextNumber(numbers, cxn, cyn, czn, iy);
                        if (cyn == -1) {
                            break;
                        }
                    }
                    if (czn == -1) {
                        czn = cxn + cyn;
                        if (carrier) {
                            czn += 1;
                        }
                        if (czn >= 10) {
                            czn %= 10;
                        }

                        if (!isComplete(x, y, z, cxn, cyn, czn) || hasNumber(czn)) {
                            if (x == y) {
                                break;
                            }
                            iy = cyn;
                            cyn = -1;
                            czn = cz == -1 ? gz : cz;
                            continue;
                        }
                        break;
                    }
                    if (x == y) {
                        break;
                    }
                }
                if (cyn == -1) {
                    if (cx != -1 || (cxn == 0 && czn == -1)) {
                        return false;
                    }
                    gx = -1;
                    gy = -1;
                    gz = -1;
                    ++ix;
                    continue;
                }
                int sum = cxn + cyn;
                if (carrier) {
                    sum += 1;
                }
                if (sum % 10 == czn && isComplete(x, y, z, cxn, cyn, czn)) {
                    ix = gx = cxn;
                    gy = cyn;
                    gz = czn;
                    ++ix;
                    return true;
                } else if (x == 0) {
                    return false;
                } else if (cx != -1 && cx == cy) {
                    return false;
                }
                gx = -1;
                gy = -1;
                gz = -1;
                ++ix;
            }
        }

        public Generation up() {
            return parent;
        }

        public boolean hasParent() {
            return parent != null;
        }

        public boolean hasNext() {
            return (x.length >= index + 1 && z.length >= index + 1)
                || (y.length >= index + 1 && z.length >= index + 1);
        }

        public Generation newStage() {
            char[] chars = new char[10];
            System.arraycopy(numbers, 0, chars, 0, chars.length);
            chars[gx] = x[this.x.length - index];
            chars[gy] = y[this.y.length - index];
            chars[gz] = z[this.z.length - index];
            return new Generation(chars, this, index + 1, x, y, z, gx + gy >= 10);
        }

        public boolean hasNumber(int number) {
            return numbers[number] != 0;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            String tX = transmute(x);
            String tY = transmute(y);
            String tZ = transmute(z);
            int width = Math.max(x.length, Math.max(y.length, z.length));
            sb.append(String.format("%" + width + "s", tX)).append("\n");
            sb.append(String.format("%" + width + "s", tY)).append("\n");
            sb.append(String.format("%" + width + "s", tZ));
            return sb.toString();
        }

        public boolean isCarrier() {
            if (z.length > x.length || z.length > y.length) {
                return indexOf(numbers, z[0]) == -1;
            }
            return false;
        }

        public boolean tryCarrier() {
            if (isCarrier()) {
                if (hasNumber(1)) {
                    return false;
                }
                numbers[1] = z[0];
                return true;
            }
            return false;
        }
    }
}
