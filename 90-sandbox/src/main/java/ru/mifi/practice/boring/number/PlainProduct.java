package ru.mifi.practice.boring.number;

/**
 * Умножение матриц по определению: элемент ответа — скалярное произведение строки на столбец.
 * Три вложенных цикла, n³ умножений, никаких хитростей. Долго считалось, что меньше n³ и быть не
 * может — пока Штрассен не показал обратное.
 */
public final class PlainProduct implements MatrixProduct {

    @Override
    public int[][] product(int[][] left, int[][] right) {
        int size = left.length;
        int[][] product = new int[size][size];
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                int total = 0;
                for (int step = 0; step < size; step++) {
                    total += left[row][step] * right[step][column];
                }
                product[row][column] = total;
            }
        }
        return product;
    }
}
