package ru.mifi.practice.vol2.sudoku;

import java.util.Objects;

@SuppressWarnings("PMD.ConstantsInInterface")
public sealed interface Value {
    Value EMPTY = new Empty();
    Value[] DIGITS = new Value[]{
        EMPTY, new Digit(1), new Digit(2), new Digit(3), new Digit(4),
        new Digit(5), new Digit(6), new Digit(7), new Digit(8), new Digit(9)
    };


    final class Empty implements Value {
        private Empty() {
        }

        @SuppressWarnings("PMD.SimplifyBooleanReturns")
        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            return o != null && getClass() == o.getClass();
        }

        @SuppressWarnings("PMD.UselessOverridingMethod")
        @Override
        public int hashCode() {
            return super.hashCode();
        }

        @Override
        public String toString() {
            return "-";
        }
    }

    final class Digit implements Value {
        private final int digit;

        private Digit(int digit) {
            this.digit = digit;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Digit other = (Digit) o;
            return Objects.equals(digit, other.digit);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(digit);
        }

        @Override
        public String toString() {
            return "" + digit;
        }
    }
}
