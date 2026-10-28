package ru.mifi.practice.boring.number;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Умножение матриц по Штрассену")
final class StrassenTest {

    @DisplayName("Перемножает матрицы два на два")
    @Test
    @Timeout(1)
    void multipliesTwoByTwo() {
        int[][] left = {{1, 2}, {3, 4}};
        int[][] right = {{5, 6}, {7, 8}};
        assertThat("the two by two product is wrong",
            new Strassen().product(left, right), is(new int[][]{{19, 22}, {43, 50}}));
    }

    @DisplayName("Единичная матрица ничего не меняет")
    @Test
    @Timeout(1)
    void multipliesByTheIdentity() {
        int[][] matrix = {{1, 2, 3, 4}, {5, 6, 7, 8}, {9, 10, 11, 12}, {13, 14, 15, 16}};
        assertThat("the product with the identity differs from the matrix",
            new Strassen().product(matrix, identity(4)), is(matrix));
    }

    @DisplayName("Матрица нечётной стороны дополняется и обрезается обратно")
    @Test
    @Timeout(1)
    void handlesTheOddSide() {
        int[][] matrix = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
        assertThat("the odd side breaks the padding",
            new Strassen().product(matrix, identity(3)), is(matrix));
    }

    @DisplayName("Матрица один на один — просто произведение чисел")
    @Test
    @Timeout(1)
    void multipliesTheSingleValue() {
        assertThat("the single value matrix is multiplied wrong",
            new Strassen().product(new int[][]{{6}}, new int[][]{{7}}), is(new int[][]{{42}}));
    }

    @DisplayName("Считает то же, что умножение по определению")
    @Test
    @Timeout(10)
    void agreesWithThePlainProduct() {
        Random random = new Random(20260815L);
        MatrixProduct strassen = new Strassen();
        MatrixProduct plain = new PlainProduct();
        int mismatches = 0;
        for (int attempt = 0; attempt < 50; attempt++) {
            int size = 1 + random.nextInt(9);
            int[][] left = matrix(random, size);
            int[][] right = matrix(random, size);
            if (!java.util.Arrays.deepEquals(strassen.product(left, right), plain.product(left, right))) {
                ++mismatches;
            }
        }
        assertThat("strassen disagrees with the plain product", mismatches, is(0));
    }

    private static int[][] identity(int size) {
        int[][] identity = new int[size][size];
        for (int at = 0; at < size; at++) {
            identity[at][at] = 1;
        }
        return identity;
    }

    private static int[][] matrix(Random random, int size) {
        int[][] matrix = new int[size][size];
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                matrix[row][column] = random.nextInt(20) - 10;
            }
        }
        return matrix;
    }
}
