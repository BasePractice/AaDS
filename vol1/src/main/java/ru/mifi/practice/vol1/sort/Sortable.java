package ru.mifi.practice.vol1.sort;

@FunctionalInterface
public interface Sortable<T extends Comparable<T>> {
    void sort(T[] data);

    enum Type {
        BUBBLE {
            @Override
            public <T extends Comparable<T>> Sortable<T> newSorting() {
                return new BubbleSorting<>();
            }
        },
        SELECTION {
            @Override
            public <T extends Comparable<T>> Sortable<T> newSorting() {
                return new SelectionSorting<>();
            }
        };

        public abstract <T extends Comparable<T>> Sortable<T> newSorting();
    }

    final class BubbleSorting<T extends Comparable<T>> implements Sortable<T> {
        private BubbleSorting() {
        }

        @Override
        public void sort(T[] data) {
            for (int i = 0; i < data.length - 1; i++) {
                for (int j = 0; j < data.length - i - 1; j++) {
                    if (data[j].compareTo(data[j + 1]) > 0) {
                        T temp = data[j];
                        data[j] = data[j + 1];
                        data[j + 1] = temp;
                    }
                }
            }
        }
    }

    final class SelectionSorting<T extends Comparable<T>> implements Sortable<T> {
        private SelectionSorting() {
        }

        @Override
        public void sort(T[] data) {
            int minimum;
            for (int i = 0; i < data.length - 1; i++) {
                minimum = i;
                for (int j = i + 1; j < data.length; j++) {
                    if (data[minimum].compareTo(data[j]) > 0) {
                        minimum = j;
                    }
                }
                T temp = data[minimum];
                data[minimum] = data[i];
                data[i] = temp;
            }
        }
    }
}
