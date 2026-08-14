package ru.mifi.practice.boring.sort;

/**
 * Сортировка Шелла: те же вставки, но сначала сравниваются элементы, отстоящие далеко друг от
 * друга, а промежуток раз за разом сокращается до единицы. Дальние обмены за один шаг
 * перебрасывают элемент через полмассива — обычным вставкам на это нужны десятки сдвигов.
 *
 * <p>Последний проход с промежутком единица — это обычная сортировка вставками, но к этому
 * моменту массив почти упорядочен, а на таком входе вставки стоят почти линейно. Промежутки
 * берутся по Кнуту (1, 4, 13, 40, ...): это даёт O(n^1.5) вместо O(n²) у исходной
 * последовательности половинных промежутков.
 */
public final class ShellSort implements Sort {

    @Override
    public int[] sorted(int[] values) {
        int[] sorted = values.clone();
        for (int gap = gap(sorted.length); gap > 0; gap = (gap - 1) / 3) {
            for (int i = gap; i < sorted.length; i++) {
                int value = sorted[i];
                int place = i;
                while (place >= gap && sorted[place - gap] > value) {
                    sorted[place] = sorted[place - gap];
                    place -= gap;
                }
                sorted[place] = value;
            }
        }
        return sorted;
    }

    private int gap(int length) {
        int gap = 1;
        while (gap * 3 + 1 < length) {
            gap = gap * 3 + 1;
        }
        return gap;
    }
}
