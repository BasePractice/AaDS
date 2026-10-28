package ru.mifi.practice.boring.sort;

/**
 * Сортировка расчёской: пузырёк, который сравнивает не соседей, а элементы на расстоянии, и
 * сокращает это расстояние в 1,3 раза за проход. Так гибнут «черепахи» — маленькие элементы в
 * хвосте массива, из-за которых обычный пузырёк ползёт: сосед двигает их на шаг за проход, а
 * расчёска перекидывает сразу далеко вперёд.
 *
 * <p>Коэффициент 1,3 подобран экспериментально и на практике даёт около O(n log n), хотя
 * худший случай остаётся квадратичным. Проходы идут, пока расстояние больше единицы или пока
 * последний проход хоть что-то поменял.
 */
public final class CombSort implements Sort {
    private static final double SHRINK = 1.3;

    @Override
    public int[] sorted(int[] values) {
        int[] sorted = values.clone();
        int gap = sorted.length;
        boolean swapped = true;
        while (gap > 1 || swapped) {
            gap = Math.max(1, (int) (gap / SHRINK));
            swapped = false;
            for (int i = 0; i + gap < sorted.length; i++) {
                if (sorted[i] > sorted[i + gap]) {
                    swap(sorted, i, i + gap);
                    swapped = true;
                }
            }
        }
        return sorted;
    }

    private void swap(int[] values, int left, int right) {
        int value = values[left];
        values[left] = values[right];
        values[right] = value;
    }
}
