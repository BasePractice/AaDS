package ru.mifi.practice.vol3;

import ru.mifi.practice.commons.Counter;

import java.util.ArrayList;
import java.util.List;

/**
 * Поразрядная сортировка из раздела 5 курса. Сортировка подсчётом применяется несколько раз —
 * по одному проходу на разряд, от младшего к старшему. Для 32-битного int это четыре прохода
 * по байтам, поэтому сложность O(4·(n + 256)), то есть линейная по количеству элементов.
 *
 * <p>Устойчивость здесь не украшение, а условие работы: каждый следующий проход обязан сохранять
 * порядок, установленный предыдущим, иначе результат рассыпется.
 *
 * <p>Старший байт содержит знаковый бит, поэтому перед последним проходом он инвертируется —
 * тогда отрицательные числа оказываются левее положительных.
 */
public final class RadixSort implements Sort<Integer> {
    private static final int BITS = 8;
    private static final int BUCKETS = 1 << BITS;
    private static final int MASK = BUCKETS - 1;
    private static final int PASSES = Integer.BYTES;
    private static final int SIGN = 1 << (BITS - 1);

    @Override
    public List<Integer> sort(List<Integer> array, Counter counter, boolean debug) {
        int[] values = new int[array.size()];
        for (int i = 0; i < array.size(); i++) {
            values[i] = array.get(i);
            counter.increment();
        }
        int[] buffer = new int[values.length];
        for (int pass = 0; pass < PASSES; pass++) {
            distribute(values, buffer, pass, counter);
            int[] swap = values;
            values = buffer;
            buffer = swap;
        }
        List<Integer> result = new ArrayList<>(values.length);
        for (int value : values) {
            result.add(value);
        }
        return result;
    }

    private void distribute(int[] source, int[] target, int pass, Counter counter) {
        int[] counts = new int[BUCKETS];
        for (int value : source) {
            counter.increment();
            ++counts[digit(value, pass)];
        }
        for (int i = 1; i < BUCKETS; i++) {
            counts[i] += counts[i - 1];
        }
        //Справа налево — иначе проход перестанет быть устойчивым
        for (int i = source.length - 1; i >= 0; i--) {
            counter.increment();
            int digit = digit(source[i], pass);
            --counts[digit];
            target[counts[digit]] = source[i];
        }
    }

    private int digit(int value, int pass) {
        int digit = value >>> (pass * BITS) & MASK;
        if (pass == PASSES - 1) {
            return digit ^ SIGN;
        }
        return digit;
    }
}
