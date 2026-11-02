package ru.mifi.practice.vol9;

import ru.mifi.practice.commons.Counter;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Комбинаторика из раздела 4 курса: перестановки, размещения и сочетания.
 *
 * <p>Все три семейства порождаются лениво, поэтому перебрать можно и то, что целиком в память не
 * поместится: 12! — почти полмиллиарда наборов, а храним мы ровно один.
 */
public interface Combinatorics {

    /**
     * Число перестановок n элементов: n!
     */
    static BigInteger permutations(int n) {
        return permutations(n, Counter.create());
    }

    /**
     * Число перестановок n элементов: n! с подсчётом операций.
     */
    static BigInteger permutations(int n, Counter counter) {
        return factorial(n, counter);
    }

    /**
     * Число размещений: A(n, k) = n! / (n − k)!  — выбор k элементов с учётом порядка.
     */
    static BigInteger arrangements(int n, int k) {
        return arrangements(n, k, Counter.create());
    }

    /**
     * Число размещений: A(n, k) = n! / (n − k)! с подсчётом операций.
     */
    static BigInteger arrangements(int n, int k, Counter counter) {
        if (k < 0 || k > n) {
            return BigInteger.ZERO;
        }
        BigInteger numerator = factorial(n, counter);
        BigInteger denominator = factorial(n - k, counter);
        counter.increment();
        return numerator.divide(denominator);
    }

    /**
     * Число сочетаний: C(n, k) = n! / (k! (n − k)!) — выбор k элементов без учёта порядка.
     */
    static BigInteger combinations(int n, int k) {
        return combinations(n, k, Counter.create());
    }

    /**
     * Число сочетаний: C(n, k) = n! / (k! (n − k)!) с подсчётом операций.
     */
    static BigInteger combinations(int n, int k, Counter counter) {
        if (k < 0 || k > n) {
            return BigInteger.ZERO;
        }
        BigInteger result = arrangements(n, k, counter);
        BigInteger divisor = factorial(k, counter);
        counter.increment();
        return result.divide(divisor);
    }

    /**
     * Число размещений с повторениями: A(n, k) = n^k — k позиций, на каждую можно поставить любой
     * из n элементов.
     */
    static BigInteger arrangementsWithRepetition(int n, int k) {
        return arrangementsWithRepetition(n, k, Counter.create());
    }

    /**
     * Число размещений с повторениями: A(n, k) = n^k с подсчётом операций.
     */
    static BigInteger arrangementsWithRepetition(int n, int k, Counter counter) {
        if (n < 0 || k < 0) {
            return BigInteger.ZERO;
        }
        BigInteger result = BigInteger.ONE;
        BigInteger base = BigInteger.valueOf(n);
        for (int i = 0; i < k; i++) {
            counter.increment();
            result = result.multiply(base);
        }
        return result;
    }

    /**
     * Число сочетаний с повторениями: C(n + k − 1, k) — выбор k элементов из n видов, порядок не
     * важен, элементы могут повторяться.
     */
    static BigInteger combinationsWithRepetition(int n, int k) {
        return combinationsWithRepetition(n, k, Counter.create());
    }

    /**
     * Число сочетаний с повторениями: C(n + k − 1, k) с подсчётом операций.
     */
    static BigInteger combinationsWithRepetition(int n, int k, Counter counter) {
        if (n < 0 || k < 0 || (n == 0 && k > 0)) {
            return BigInteger.ZERO;
        }
        if (k == 0) {
            return BigInteger.ONE;
        }
        return combinations(n + k - 1, k, counter);
    }

    private static BigInteger factorial(int n, Counter counter) {
        if (n < 0) {
            throw new IllegalArgumentException("Факториал отрицательного числа не определён: " + n);
        }
        BigInteger result = BigInteger.ONE;
        for (int i = 2; i <= n; i++) {
            counter.increment();
            result = result.multiply(BigInteger.valueOf(i));
        }
        return result;
    }

    /**
     * Перестановки в лексикографическом порядке. Следующая получается из текущей за O(n):
     * находим самый правый элемент, меньший соседа справа, меняем его с наименьшим большим из
     * хвоста и разворачиваем хвост.
     */
    static Iterable<int[]> permutationsOf(int size, Counter counter) {
        if (size < 0) {
            throw new IllegalArgumentException("Размер не может быть отрицательным: " + size);
        }
        return () -> new Iterator<>() {
            private final int[] current = initial(size);
            private boolean has = true;

            @Override
            public boolean hasNext() {
                return has;
            }

            @Override
            public int[] next() {
                if (!has) {
                    throw new NoSuchElementException("Перестановки закончились");
                }
                int[] result = current.clone();
                has = advance(current, counter);
                return result;
            }
        };
    }

    /**
     * Сочетания по k из n в лексикографическом порядке индексов.
     */
    static Iterable<int[]> combinationsOf(int size, int taken, Counter counter) {
        if (taken < 0 || taken > size) {
            return Collections::emptyIterator;
        }
        return () -> new Iterator<>() {
            private final int[] current = initial(taken);
            private boolean has = true;

            @Override
            public boolean hasNext() {
                return has;
            }

            @Override
            public int[] next() {
                if (!has) {
                    throw new NoSuchElementException("Сочетания закончились");
                }
                int[] result = current.clone();
                has = advanceCombination(current, size, counter);
                return result;
            }
        };
    }

    /**
     * Размещения с повторениями: все кортежи длины k из элементов 0..n−1 в лексикографическом
     * порядке.
     */
    static Iterable<int[]> arrangementsWithRepetitionOf(int size, int taken, Counter counter) {
        if (size < 0 || taken < 0) {
            throw new IllegalArgumentException("Размер и длина не могут быть отрицательными: " + size + ", " + taken);
        }
        if (taken == 0) {
            return () -> Collections.singletonList(new int[0]).iterator();
        }
        if (size == 0) {
            return Collections::emptyIterator;
        }
        return () -> new Iterator<>() {
            private final int[] current = new int[taken];
            private boolean has = true;

            @Override
            public boolean hasNext() {
                return has;
            }

            @Override
            public int[] next() {
                if (!has) {
                    throw new NoSuchElementException("Размещения закончились");
                }
                int[] result = current.clone();
                has = advanceRepetition(current, size, counter);
                return result;
            }
        };
    }

    /**
     * Сочетания с повторениями: неубывающие кортежи длины k из элементов 0..n−1 в
     * лексикографическом порядке.
     */
    static Iterable<int[]> combinationsWithRepetitionOf(int size, int taken, Counter counter) {
        if (size < 0 || taken < 0) {
            throw new IllegalArgumentException("Размер и длина не могут быть отрицательными: " + size + ", " + taken);
        }
        if (taken == 0) {
            return () -> Collections.singletonList(new int[0]).iterator();
        }
        if (size == 0) {
            return Collections::emptyIterator;
        }
        return () -> new Iterator<>() {
            private final int[] current = new int[taken];
            private boolean has = true;

            @Override
            public boolean hasNext() {
                return has;
            }

            @Override
            public int[] next() {
                if (!has) {
                    throw new NoSuchElementException("Сочетания закончились");
                }
                int[] result = current.clone();
                has = advanceRepetitionCombination(current, size, counter);
                return result;
            }
        };
    }

    private static int[] initial(int size) {
        int[] initial = new int[size];
        for (int i = 0; i < size; i++) {
            initial[i] = i;
        }
        return initial;
    }

    /**
     * Переводит перестановку в следующую лексикографически. Возвращает false, когда перебор
     * исчерпан: перестановки порождаются на месте, поэтому пустого набора-признака не нужно —
     * иначе единственная перестановка пустого множества была бы неотличима от конца перебора.
     */
    private static boolean advance(int[] permutation, Counter counter) {
        int pivot = permutation.length - 2;
        while (pivot >= 0 && permutation[pivot] >= permutation[pivot + 1]) {
            counter.increment();
            --pivot;
        }
        if (pivot < 0) {
            return false;
        }
        int successor = permutation.length - 1;
        while (permutation[successor] <= permutation[pivot]) {
            counter.increment();
            --successor;
        }
        swap(permutation, pivot, successor);
        reverse(permutation, pivot + 1);
        return true;
    }

    private static boolean advanceCombination(int[] combination, int size, Counter counter) {
        int index = combination.length - 1;
        while (index >= 0 && combination[index] == size - combination.length + index) {
            counter.increment();
            --index;
        }
        if (index < 0) {
            return false;
        }
        ++combination[index];
        for (int i = index + 1; i < combination.length; i++) {
            combination[i] = combination[i - 1] + 1;
        }
        return true;
    }

    private static boolean advanceRepetition(int[] tuple, int size, Counter counter) {
        int index = tuple.length - 1;
        while (index >= 0 && tuple[index] == size - 1) {
            counter.increment();
            tuple[index] = 0;
            --index;
        }
        if (index < 0) {
            return false;
        }
        ++tuple[index];
        return true;
    }

    private static boolean advanceRepetitionCombination(int[] combination, int size, Counter counter) {
        int index = combination.length - 1;
        while (index >= 0 && combination[index] == size - 1) {
            counter.increment();
            --index;
        }
        if (index < 0) {
            return false;
        }
        int next = combination[index] + 1;
        for (int i = index; i < combination.length; i++) {
            combination[i] = next;
        }
        return true;
    }

    private static void swap(int[] values, int left, int right) {
        int tmp = values[left];
        values[left] = values[right];
        values[right] = tmp;
    }

    private static void reverse(int[] values, int from) {
        for (int i = from, j = values.length - 1; i < j; i++, j--) {
            swap(values, i, j);
        }
    }

    /**
     * Собирает перестановки в список. Годится только для маленьких n — на то и комбинаторный взрыв.
     */
    static List<String> collect(Iterable<int[]> tuples) {
        List<String> collected = new ArrayList<>();
        for (int[] tuple : tuples) {
            collected.add(Arrays.toString(tuple));
        }
        return collected;
    }
}
