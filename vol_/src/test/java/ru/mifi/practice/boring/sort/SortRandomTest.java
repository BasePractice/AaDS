package ru.mifi.practice.boring.sort;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.Arrays;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Сверка всех сортировок пакета со штатной на случайных входах.
 *
 * <p>Диапазон значений узкий, чтобы повторы попадались часто, а длины разные — в том числе не
 * степени двойки, на которых ломается сортирующая сеть, если её дополнение сделано небрежно.
 */
@DisplayName("Сортировки на случайных входах")
final class SortRandomTest {

    @DisplayName("Сортировка Шелла упорядочивает как штатная")
    @Test
    @Timeout(10)
    void shellSortAgreesWithTheStandardOne() {
        assertThat("shell sort disagrees with the standard sorting",
            disagreements(new ShellSort()), is(0));
    }

    @DisplayName("Сортировка расчёской упорядочивает как штатная")
    @Test
    @Timeout(10)
    void combSortAgreesWithTheStandardOne() {
        assertThat("comb sort disagrees with the standard sorting",
            disagreements(new CombSort()), is(0));
    }

    @DisplayName("Поразрядная сортировка упорядочивает как штатная")
    @Test
    @Timeout(10)
    void radixSortAgreesWithTheStandardOne() {
        assertThat("radix sort disagrees with the standard sorting",
            disagreements(new RadixSort()), is(0));
    }

    @DisplayName("Битонная сортировка упорядочивает как штатная")
    @Test
    @Timeout(10)
    void bitonicSortAgreesWithTheStandardOne() {
        assertThat("bitonic sort disagrees with the standard sorting",
            disagreements(new BitonicSort()), is(0));
    }

    private static int disagreements(Sort sort) {
        Random random = new Random(20260815L);
        int mismatches = 0;
        for (int attempt = 0; attempt < 200; attempt++) {
            int[] values = new int[random.nextInt(40)];
            for (int i = 0; i < values.length; i++) {
                values[i] = random.nextInt(100) - 50;
            }
            int[] expected = values.clone();
            Arrays.sort(expected);
            if (!Arrays.equals(sort.sorted(values), expected)) {
                ++mismatches;
            }
        }
        return mismatches;
    }
}
