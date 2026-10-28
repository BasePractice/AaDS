package ru.mifi.practice.vol1.bigo;

import ru.mifi.practice.commons.Counter;

/**
 * Нагрузка известного класса сложности: отрабатывает на входе заданного размера и отмечает в
 * счётчике каждый элементарный шаг. Классы вынесены в отдельные реализации, чтобы стенд раздела 1
 * мерил не время, которое зависит от машины, а число шагов, которое зависит только от размера.
 */
@FunctionalInterface
public interface Workload {

    void run(int size, Counter counter);

    enum Type implements Workload {
        CONSTANT(new Constant()),
        LOGARITHMIC(new Logarithmic()),
        LINEAR(new Linear()),
        LINEARITHMIC(new Linearithmic()),
        QUADRATIC(new Quadratic());

        private final Workload workload;

        Type(Workload workload) {
            this.workload = workload;
        }

        @Override
        public void run(int size, Counter counter) {
            workload.run(size, counter);
        }
    }

    /** O(1): обращение к элементу по индексу стоит один шаг, каким бы длинным вход ни был. */
    final class Constant implements Workload {
        @Override
        public void run(int size, Counter counter) {
            counter.increment();
        }
    }

    /** O(log n): счёт единичных битов делением на два — вход уменьшается вдвое за шаг. */
    final class Logarithmic implements Workload {
        @Override
        public void run(int size, Counter counter) {
            int rest = size;
            while (rest > 0) {
                counter.increment();
                rest /= 2;
            }
        }
    }

    /** O(n): сумма всех элементов — каждый элемент читается ровно раз. */
    final class Linear implements Workload {
        @Override
        public void run(int size, Counter counter) {
            for (int i = 0; i < size; i++) {
                counter.increment();
            }
        }
    }

    /** O(n log n): двоичный поиск каждого из n значений в упорядоченном входе. */
    final class Linearithmic implements Workload {
        @Override
        public void run(int size, Counter counter) {
            for (int value = 0; value < size; value++) {
                search(value, size, counter);
            }
        }

        private void search(int value, int size, Counter counter) {
            int low = 0;
            int high = size - 1;
            while (low <= high) {
                counter.increment();
                int middle = (low + high) >>> 1;
                if (middle == value) {
                    return;
                }
                if (middle < value) {
                    low = middle + 1;
                } else {
                    high = middle - 1;
                }
            }
        }
    }

    /** O(n²): сравнение каждого элемента с каждым — то же, что делает пузырьковая сортировка. */
    final class Quadratic implements Workload {
        @Override
        public void run(int size, Counter counter) {
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    counter.increment();
                }
            }
        }
    }
}
