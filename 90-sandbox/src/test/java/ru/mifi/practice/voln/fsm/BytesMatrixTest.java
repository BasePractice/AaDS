package ru.mifi.practice.voln.fsm;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Матрица клеточного автомата")
final class BytesMatrixTest {

    @DisplayName("Матрица на массиве возвращает записанное значение")
    @Test
    @Timeout(1)
    void readsBackTheStoredValueFromAnArray() {
        BytesMatrix matrix = new BytesMatrix.BytesTorusMatrix(4, 5);
        matrix.set(1, 2, (byte) 1);
        assertThat("stored cell dont come back", matrix.get(1, 2), is((byte) 1));
    }

    @DisplayName("Матрица на битах возвращает записанное значение")
    @Test
    @Timeout(1)
    void readsBackTheStoredValueFromBits() {
        BytesMatrix matrix = new BytesMatrix.BytesTorusBits(4, 5);
        matrix.set(1, 2, (byte) 1);
        assertThat("stored bit dont come back", matrix.get(1, 2), is((byte) 1));
    }

    @DisplayName("Сброс бита не ломает матрицу")
    @Test
    @Timeout(1)
    void clearsABitWithoutFailing() {
        BytesMatrix matrix = new BytesMatrix.BytesTorusBits(4, 5);
        matrix.set(1, 2, (byte) 1);
        matrix.set(1, 2, (byte) 0);
        assertThat("clearing a bit dont clear the cell", matrix.get(1, 2), is((byte) 0));
    }

    @DisplayName("Столбец слева от нулевого — последний столбец")
    @Test
    @Timeout(1)
    void wrapsTheColumnBeforeTheFirstToTheLast() {
        BytesMatrix matrix = new BytesMatrix.BytesTorusMatrix(4, 5);
        matrix.set(0, 4, (byte) 1);
        assertThat("the torus dont wrap the column to the opposite edge", matrix.get(0, -1), is((byte) 1));
    }

    @DisplayName("Строка выше нулевой — последняя строка")
    @Test
    @Timeout(1)
    void wrapsTheRowBeforeTheFirstToTheLast() {
        BytesMatrix matrix = new BytesMatrix.BytesTorusMatrix(4, 5);
        matrix.set(3, 0, (byte) 1);
        assertThat("the torus dont wrap the row to the opposite edge", matrix.get(-1, 0), is((byte) 1));
    }

    @DisplayName("Отрицательные координаты на битовой матрице заворачиваются")
    @Test
    @Timeout(1)
    void wrapsNegativeCoordinatesOnBits() {
        BytesMatrix matrix = new BytesMatrix.BytesTorusBits(4, 5);
        matrix.set(0, 4, (byte) 1);
        assertThat("bit storage dont wrap negative coordinates", matrix.get(0, -1), is((byte) 1));
    }

    @DisplayName("Столбец за последним — нулевой столбец")
    @Test
    @Timeout(1)
    void wrapsTheColumnAfterTheLastToTheFirst() {
        BytesMatrix matrix = new BytesMatrix.BytesTorusMatrix(4, 5);
        matrix.set(0, 0, (byte) 1);
        assertThat("the torus dont wrap past the right edge", matrix.get(0, 5), is((byte) 1));
    }
}
