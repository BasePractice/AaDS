package ru.mifi.practice.vol9;

import ru.mifi.practice.commons.Counter;
import ru.mifi.practice.commons.ObjectCounter;

import java.math.BigInteger;

/**
 * Числа Белла: количество разбиений множества из n элементов на непустые части. Считаются по
 * треугольнику Белла, в котором первое число строки равно последнему числу предыдущей.
 */
public final class Bell {

    private Bell() {
    }

    public static BigInteger number(int n) {
        return number(n, Counter.create());
    }

    public static BigInteger number(int n, Counter counter) {
        return number(n, counter, ObjectCounter.create());
    }

    public static BigInteger number(int n, Counter counter, ObjectCounter objects) {
        if (n < 0) {
            throw new IllegalArgumentException("Номер числа Белла не может быть отрицательным: " + n);
        }
        BigInteger[][] triangle = new BigInteger[n + 1][];
        objects.allocate();
        for (int row = 0; row <= n; row++) {
            objects.allocate();
            triangle[row] = new BigInteger[row + 1];
        }
        triangle[0][0] = BigInteger.ONE;
        for (int row = 1; row <= n; row++) {
            counter.increment();
            triangle[row][0] = triangle[row - 1][row - 1];
            for (int column = 1; column <= row; column++) {
                counter.increment();
                objects.allocate();
                triangle[row][column] = triangle[row][column - 1].add(triangle[row - 1][column - 1]);
            }
        }
        return triangle[n][0];
    }
}
