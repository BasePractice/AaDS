package ru.mifi.practice.voln.fsm;

import java.util.concurrent.atomic.AtomicInteger;

public final class GoL {
    private final AtomicInteger ticks = new AtomicInteger(0);
    private BytesMatrix matrix;

    public GoL(BytesMatrix matrix) {
        this.matrix = matrix;
    }

    private static byte mutate(BytesMatrix matrix, int row, int col) {
        byte v = matrix.get(row, col);
        int total = count(matrix.get(row, col - 1)) + count(matrix.get(row, col + 1)) +
            count(matrix.get(row - 1, col)) + count(matrix.get(row + 1, col)) +
            count(matrix.get(row - 1, col - 1)) + count(matrix.get(row - 1, col + 1)) +
            count(matrix.get(row + 1, col - 1)) + count(matrix.get(row + 1, col + 1));
        if (is(matrix.get(row, col))) {
            if (total < 2 || total > 3) {
                return 0;
            }
        } else if (total == 3) {
            return 1;
        }
        return v;
    }

    private static boolean is(byte v) {
        return v == 1;
    }

    private static int count(byte v) {
        return v;
    }

    public void tick() {
        BytesMatrix copied = matrix.copy();
        for (int row = 0; row < matrix.rows(); row++) {
            for (int col = 0; col < matrix.cols(); col++) {
                copied.set(row, col, mutate(matrix, row, col));
            }
        }
        matrix = copied;
        ticks.incrementAndGet();
    }
}
