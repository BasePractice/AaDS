package ru.mifi.practice.voln.machine.post;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings({"PMD.AssignmentInOperand", "PMD.SimplifyBooleanReturns"})
public interface MechanicalHead {

    boolean hasLabel();

    int doLabel(LabelOperation op, int num);

    int step(Step step, int num);

    int gotoLine(int num);

    int stop();

    boolean isStopped();

    enum Step {
        LEFT, RIGHT
    }

    enum LabelOperation {
        PUT, DELETE
    }

    final class BooleanArrayHead implements MechanicalHead {
        private final boolean[] values;
        private final AtomicInteger it = new AtomicInteger(0);
        private final AtomicBoolean stopped = new AtomicBoolean(false);

        public BooleanArrayHead(int n) {
            this.values = new boolean[n];
            this.it.set(n / 2);
        }

        @Override
        public boolean hasLabel() {
            return values[it.intValue()];
        }

        @Override
        public int doLabel(LabelOperation op, int num) {
            switch (op) {
                case PUT -> values[it.intValue()] = true;
                case DELETE -> values[it.intValue()] = false;
                default -> throw new UnsupportedOperationException();
            }
            return num;
        }

        @Override
        public int step(Step step, int num) {
            switch (step) {
                case LEFT -> it.decrementAndGet();
                case RIGHT -> it.incrementAndGet();
                default -> throw new UnsupportedOperationException();
            }
            if (it.intValue() < 0) {
                throw new IllegalStateException();
            } else if (it.intValue() >= values.length) {
                throw new IllegalStateException();
            }
            return num;
        }

        @Override
        public int gotoLine(int num) {
            return num;
        }

        @Override
        public int stop() {
            stopped.set(true);
            return it.intValue();
        }

        @Override
        public boolean isStopped() {
            return stopped.get();
        }

        public BooleanArrayHead initiate(int start, String data) {
            it.set(start);
            for (int pc = start, i = 0; pc < start + data.length(); pc++, i++) {
                char c = data.charAt(i);
                if (c == '0') {
                    values[pc] = false;
                } else if (c == '1') {
                    values[pc] = true;
                }
            }
            return this;
        }

        public BooleanArrayHead initiate(String data) {
            int center = size() / 2 - data.length() / 2;
            return initiate(center, data);
        }

        public BooleanArrayHead offset(int pc) {
            it.set(pc);
            return this;
        }

        public int size() {
            return values.length;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof BooleanArrayHead that)) {
                return false;
            }
            return Arrays.equals(values, that.values);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(values);
        }

        @Override
        public String toString() {
            StringBuilder result = new StringBuilder();
            for (boolean value : values) {
                result.append(value ? '1' : '0');
            }
            return result.toString();
        }
    }

    final class BitSetHead implements MechanicalHead {
        private final BitSet bitSet;
        private final AtomicInteger it = new AtomicInteger(0);
        private final AtomicBoolean stopped = new AtomicBoolean(false);

        public BitSetHead(int n) {
            this.bitSet = new BitSet(n);
            this.it.set(n / 2);
        }

        @Override
        public boolean hasLabel() {
            return bitSet.get(it.intValue());
        }

        @Override
        public int doLabel(LabelOperation op, int num) {
            switch (op) {
                case PUT -> bitSet.set(it.intValue());
                case DELETE -> bitSet.clear(it.intValue());
                default -> throw new UnsupportedOperationException();
            }
            return num;
        }

        @Override
        public int step(Step step, int num) {
            switch (step) {
                case LEFT -> it.decrementAndGet();
                case RIGHT -> it.incrementAndGet();
                default -> throw new UnsupportedOperationException();
            }
            if (it.intValue() < 0) {
                throw new IllegalStateException();
            } else if (it.intValue() >= bitSet.length()) {
                throw new IllegalStateException();
            }
            return num;
        }

        @Override
        public int gotoLine(int num) {
            return num;
        }

        @Override
        public int stop() {
            stopped.set(true);
            return it.intValue();
        }

        @Override
        public boolean isStopped() {
            return stopped.get();
        }

        public BitSetHead initiate(int start, String data) {
            it.set(start);
            for (int pc = start, i = 0; pc < start + data.length(); pc++, i++) {
                char c = data.charAt(i);
                if (c == '0') {
                    bitSet.clear(pc);
                } else if (c == '1') {
                    bitSet.set(pc);
                }
            }
            return this;
        }

        public BitSetHead initiate(String data) {
            int center = size() / 2 - data.length();
            return initiate(center, data);
        }

        public BitSetHead offset(int pc) {
            it.set(pc);
            return this;
        }

        public int size() {
            return bitSet.size();
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof BitSetHead that)) {
                return false;
            }
            return Objects.equals(bitSet, that.bitSet);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(bitSet);
        }

        @Override
        public String toString() {
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < bitSet.size(); i++) {
                result.append(bitSet.get(i) ? '1' : '0');
            }
            return result.toString();
        }
    }
}
