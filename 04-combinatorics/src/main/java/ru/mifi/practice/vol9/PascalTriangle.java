package ru.mifi.practice.vol9;

import ru.mifi.practice.commons.Counter;
import ru.mifi.practice.commons.ObjectCounter;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Треугольник Паскаля: каждая строка начинается и заканчивается единицей, а внутренний элемент
 * равен сумме двух элементов над ним. Элемент строки r с номером k — это C(r, k).
 */
public final class PascalTriangle {

    private PascalTriangle() {
    }

    public static List<List<BigInteger>> of(int rows) {
        return of(rows, Counter.create());
    }

    public static List<List<BigInteger>> of(int rows, Counter counter) {
        return of(rows, counter, ObjectCounter.create());
    }

    public static List<List<BigInteger>> of(int rows, Counter counter, ObjectCounter objects) {
        if (rows < 0) {
            throw new IllegalArgumentException("Количество строк не может быть отрицательным: " + rows);
        }
        if (rows == 0) {
            return Collections.emptyList();
        }
        objects.allocate();
        List<List<BigInteger>> triangle = new ArrayList<>();
        for (int row = 0; row < rows; row++) {
            counter.increment();
            objects.allocate();
            List<BigInteger> values = new ArrayList<>(row + 1);
            values.add(BigInteger.ONE);
            for (int column = 1; column < row; column++) {
                counter.increment();
                objects.allocate();
                List<BigInteger> previous = triangle.get(row - 1);
                values.add(previous.get(column - 1).add(previous.get(column)));
            }
            if (row > 0) {
                values.add(BigInteger.ONE);
            }
            triangle.add(values);
        }
        return triangle;
    }
}
