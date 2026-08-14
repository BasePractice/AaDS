package ru.mifi.practice.boring.number;

/**
 * Умножение матриц по Штрассену. Матрицы делятся на четыре блока, и произведение блочных матриц
 * по определению требует восьми умножений блоков. Штрассен в 1969 году нашёл способ обойтись
 * семью — ценой восемнадцати сложений вместо четырёх.
 *
 * <p>Экономия одного умножения меняет рекуррентность: T(n) = 7·T(n/2) вместо 8·T(n/2), то есть
 * O(n^log₂7) ≈ O(n^2.807) вместо O(n³). Это первый результат, показавший, что кубическая оценка
 * для умножения матриц не обязательна, — с него началась целая гонка, где рекорд сегодня около
 * n^2.37.
 *
 * <p>Семь произведений считаются по формулам, в которых блоки сначала складываются и вычитаются,
 * а блоки ответа собираются обратно из этих семи. Проверять такие формулы глазами бесполезно —
 * их проверяет тест, сверяя ответ с умножением по определению.
 *
 * <p>Сторона матрицы должна быть степенью двойки, иначе блоки не делятся пополам; матрица меньшей
 * стороны дополняется нулями, а лишнее в конце отбрасывается. Ниже порога работает обычный
 * тройной цикл: на маленьких матрицах восемнадцать сложений дороже одного умножения.
 */
public final class Strassen implements MatrixProduct {
    private static final int THRESHOLD = 2;
    private final MatrixProduct plain = new PlainProduct();

    @Override
    public int[][] product(int[][] left, int[][] right) {
        int size = padded(left.length);
        int[][] product = multiplied(grown(left, size), grown(right, size));
        return grown(product, left.length);
    }

    private int[][] multiplied(int[][] left, int[][] right) {
        int size = left.length;
        if (size <= THRESHOLD) {
            return plain.product(left, right);
        }
        int half = size / 2;
        int[][][] a = blocks(left, half);
        int[][][] b = blocks(right, half);
        int[][] first = multiplied(sum(a[0], a[3]), sum(b[0], b[3]));
        int[][] second = multiplied(sum(a[2], a[3]), b[0]);
        int[][] third = multiplied(a[0], difference(b[1], b[3]));
        int[][] fourth = multiplied(a[3], difference(b[2], b[0]));
        int[][] fifth = multiplied(sum(a[0], a[1]), b[3]);
        int[][] sixth = multiplied(difference(a[2], a[0]), sum(b[0], b[1]));
        int[][] seventh = multiplied(difference(a[1], a[3]), sum(b[2], b[3]));
        return joined(
            sum(difference(sum(first, fourth), fifth), seventh),
            sum(third, fifth),
            sum(second, fourth),
            sum(sum(difference(first, second), third), sixth));
    }

    /** Четыре блока матрицы: левый верхний, правый верхний, левый нижний, правый нижний. */
    private int[][][] blocks(int[][] matrix, int half) {
        int[][][] blocks = new int[4][half][half];
        for (int row = 0; row < half; row++) {
            for (int column = 0; column < half; column++) {
                blocks[0][row][column] = matrix[row][column];
                blocks[1][row][column] = matrix[row][column + half];
                blocks[2][row][column] = matrix[row + half][column];
                blocks[3][row][column] = matrix[row + half][column + half];
            }
        }
        return blocks;
    }

    private int[][] joined(int[][] topLeft, int[][] topRight, int[][] bottomLeft, int[][] bottomRight) {
        int half = topLeft.length;
        int[][] joined = new int[half * 2][half * 2];
        for (int row = 0; row < half; row++) {
            for (int column = 0; column < half; column++) {
                joined[row][column] = topLeft[row][column];
                joined[row][column + half] = topRight[row][column];
                joined[row + half][column] = bottomLeft[row][column];
                joined[row + half][column + half] = bottomRight[row][column];
            }
        }
        return joined;
    }

    private int[][] sum(int[][] left, int[][] right) {
        int[][] sum = new int[left.length][left.length];
        for (int row = 0; row < left.length; row++) {
            for (int column = 0; column < left.length; column++) {
                sum[row][column] = left[row][column] + right[row][column];
            }
        }
        return sum;
    }

    private int[][] difference(int[][] left, int[][] right) {
        int[][] difference = new int[left.length][left.length];
        for (int row = 0; row < left.length; row++) {
            for (int column = 0; column < left.length; column++) {
                difference[row][column] = left[row][column] - right[row][column];
            }
        }
        return difference;
    }

    /** Матрица, дополненная нулями до нужной стороны или обрезанная до неё. */
    private int[][] grown(int[][] matrix, int size) {
        int[][] grown = new int[size][size];
        for (int row = 0; row < Math.min(size, matrix.length); row++) {
            System.arraycopy(matrix[row], 0, grown[row], 0, Math.min(size, matrix.length));
        }
        return grown;
    }

    private int padded(int size) {
        int padded = 1;
        while (padded < size) {
            padded *= 2;
        }
        return padded;
    }
}
