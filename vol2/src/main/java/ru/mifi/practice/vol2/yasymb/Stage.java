package ru.mifi.practice.vol2.yasymb;

@SuppressWarnings("PMD.UnusedPrivateField")
public interface Stage {
    final class Generation {
        private final char[] numbers = new char[10];
        private final Generation parent;
        private Generation next;

        public Generation(Generation parent) {
            this.parent = parent;
        }

        public Generation newStage() {
            Generation stage = new Generation(this);
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
