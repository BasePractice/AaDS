package ru.mifi.practice.vol2.yasymb;

@SuppressWarnings("PMD.UnusedPrivateField")
public interface Stage {
    final class Generation {
        private final char[] numbers = new char[10];
        private final Generation parent;
        private final int index;
        private final char[] x;
        private final char[] y;
        private final char[] z;
        private int cx;
        private int cy;
        private int cz;
        private Generation next;

        public Generation(char[] x, char[] y, char[] z) {
            this.parent = null;
            this.index = 1;
            this.x = x;
            this.y = y;
            this.z = z;
        }

        private Generation(Generation parent, int index, char[] x, char[] y, char[] z) {
            this.parent = parent;
            this.index = index;
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public boolean permutation() {

            throw new UnsupportedOperationException("Not supported yet.");
        }

        public Generation parent() {
            return parent;
        }

        public boolean hasParent() {
            return parent != null;
        }

        public boolean hasStage() {
            return x.length >= index + 1 && y.length >= index + 1 && z.length >= index + 1;
        }

        public Generation newStage() {
            Generation stage = new Generation(this, index + 1, x, y, z);
            this.next = stage;
            return stage;
        }

        public void clean(char ch) {
            int index = indexOf(ch);
            if (index != -1) {
                numbers[index] = 0;
            }
        }

        public int indexOf(char ch) {
            for (int i = 0; i < numbers.length; i++) {
                if (numbers[i] == ch) {
                    return i;
                }
            }
            return -1;
        }

        public boolean hasNumber(int number) {
            return numbers[number] != 0;
        }

        public boolean hasSymbol(char c) {
            for (char ch : numbers) {
                if (ch == c) {
                    return true;
                }
            }
            return false;
        }
    }
}
