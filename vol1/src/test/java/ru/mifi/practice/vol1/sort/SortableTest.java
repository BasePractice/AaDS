package ru.mifi.practice.vol1.sort;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.Arrays;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Сверка всех стратегий сортировки со штатной на случайных входах.
 *
 * <p>Диапазон значений узкий, чтобы повторы попадались часто: на равных элементах ломается
 * выбор минимума, если сравнение перепутано со строгим.
 */
@DisplayName("Стратегии сортировки")
final class SortableTest {

    @DisplayName("Пузырьковая сортировка упорядочивает как штатная")
    @Test
    @Timeout(10)
    void bubbleAgreesWithTheStandardOne() {
        assertThat("bubble sorting disagrees with the standard one",
            disagreements(Sortable.Type.BUBBLE), is(0));
    }

    @DisplayName("Сортировка выбором упорядочивает как штатная")
    @Test
    @Timeout(10)
    void selectionAgreesWithTheStandardOne() {
        assertThat("selection sorting disagrees with the standard one",
            disagreements(Sortable.Type.SELECTION), is(0));
    }

    @DisplayName("Сортировка слиянием упорядочивает как штатная")
    @Test
    @Timeout(10)
    void mergeAgreesWithTheStandardOne() {
        assertThat("merge sorting disagrees with the standard one",
            disagreements(Sortable.Type.MERGE), is(0));
    }

    @DisplayName("Параллельная сортировка упорядочивает как штатная")
    @Test
    @Timeout(10)
    void parallelAgreesWithTheStandardOne() {
        assertThat("parallel sorting disagrees with the standard one",
            disagreements(Sortable.Type.PARALLEL), is(0));
    }

    @DisplayName("Быстрая сортировка упорядочивает как штатная")
    @Test
    @Timeout(10)
    void quickAgreesWithTheStandardOne() {
        assertThat("quick sorting disagrees with the standard one",
            disagreements(Sortable.Type.QSORT), is(0));
    }

    @DisplayName("Пустой массив сортируется без ошибок")
    @Test
    @Timeout(10)
    void sortsAnEmptyArray() {
        Integer[] data = new Integer[0];
        Sortable.Type.BUBBLE.<Integer>newSorting().sort(data);
        assertThat("an empty array breaks the sorting", data.length, is(0));
    }

    @DisplayName("Название стратегии выравнивается по ширине колонки")
    @Test
    @Timeout(10)
    void padsTheStrategyName() {
        assertThat("the strategy name is not padded", Sortable.Type.QSORT.toString(), is("    QSORT"));
    }

    private static int disagreements(Sortable.Type type) {
        Random random = new Random(20260804L);
        int mismatches = 0;
        for (int attempt = 0; attempt < 200; attempt++) {
            Integer[] data = new Integer[random.nextInt(40)];
            for (int i = 0; i < data.length; i++) {
                data[i] = random.nextInt(20);
            }
            Integer[] expected = data.clone();
            Arrays.sort(expected);
            type.<Integer>newSorting().sort(data);
            if (!Arrays.equals(data, expected)) {
                ++mismatches;
            }
        }
        return mismatches;
    }
}
