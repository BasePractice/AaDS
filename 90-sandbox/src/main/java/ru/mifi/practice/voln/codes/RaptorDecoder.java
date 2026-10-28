package ru.mifi.practice.voln.codes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Декодер упрощённого Raptor-кода.
 * Принимает закодированные LT-символы (XOR подмножеств промежуточных символов)
 * и восстанавливает исходные данные, если получено достаточно независимых уравнений.
 */
public final class RaptorDecoder {
    private final int k;
    private final int totalIntermediates; // k + s
    private final int symbolSize;
    private final int originalLength;

    private final List<int[]> rows = new ArrayList<>(); // соседние индексы для каждой строки
    private final List<byte[]> rhs = new ArrayList<>(); // правая часть (payload)

    private RaptorDecoder(RaptorConfiguration config, int k, int originalLength) {
        if (k <= 0) {
            throw new IllegalArgumentException("k должно быть > 0");
        }
        if (originalLength < 0) {
            throw new IllegalArgumentException("originalLength < 0");
        }
        this.k = k;
        this.totalIntermediates = k + config.parityCount();
        this.symbolSize = config.symbolSize();
        this.originalLength = originalLength;
        for (int[] relation : Precode.relations(k, config)) {
            rows.add(relation.clone());
            rhs.add(BytesXor.zeros(symbolSize));
        }
    }

    /**
     * Создаёт декодер.
     *
     * @param config         конфигурация
     * @param k              число исходных символов у источника
     * @param originalLength исходная длина потока байтов
     * @return декодер
     */
    public static RaptorDecoder create(RaptorConfiguration config, int k, int originalLength) {
        return new RaptorDecoder(config, k, originalLength);
    }

    /**
     * Добавляет один принятый закодированный символ.
     *
     * @param symbol символ
     */
    public void addSymbol(EncodedSymbol symbol) {
        int[] neigh = symbol.neighbors();
        if (neigh.length == 0) {
            throw new IllegalArgumentException("Пустой список соседей");
        }
        for (int v : neigh) {
            if (v < 0 || v >= totalIntermediates) {
                throw new IllegalArgumentException("Индекс соседа вне допустимых границ: " + v);
            }
        }
        boolean[] mark = new boolean[totalIntermediates];
        int degree = 0;
        for (int v : neigh) {
            mark[v] = !mark[v];
        }
        int[] compact = new int[neigh.length];
        for (int i = 0; i < mark.length; i++) {
            if (mark[i]) {
                compact[degree] = i;
                degree++;
            }
        }
        if (degree == 0) {
            return;
        }
        int[] row = Arrays.copyOf(compact, degree);
        rows.add(row);
        rhs.add(symbol.payload());
    }

    private static boolean dependsOnFree(boolean[] row, int[] pivotRowByCol, int n) {
        for (int c = 0; c < n; c++) {
            if (row[c] && pivotRowByCol[c] < 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Пытается декодировать исходные данные.
     *
     * @return восстановленные данные (обрезанные до исходной длины)
     */
    public byte[] decode() {
        if (originalLength == 0) {
            return new byte[0];
        }
        if (rows.isEmpty()) {
            throw new IllegalStateException("Недостаточно символов для декодирования");
        }
        int m = rows.size();
        int n = totalIntermediates;
        boolean[][] a = new boolean[m][n];
        byte[][] b = new byte[m][symbolSize];
        for (int i = 0; i < m; i++) {
            int[] idx = rows.get(i);
            for (int v : idx) {
                a[i][v] = !a[i][v];
            }
            b[i] = Arrays.copyOf(rhs.get(i), symbolSize);
        }

        int[] pivotColByRow = new int[m];
        Arrays.fill(pivotColByRow, -1);
        int[] pivotRowByCol = new int[n];
        Arrays.fill(pivotRowByCol, -1);

        int r = 0;
        for (int c = 0; c < n && r < m; c++) {
            int pivot = -1;
            for (int i = r; i < m; i++) {
                if (a[i][c]) {
                    pivot = i;
                    break;
                }
            }
            if (pivot == -1) {
                continue;
            }
            if (pivot != r) {
                boolean[] tmpA = a[pivot];
                a[pivot] = a[r];
                a[r] = tmpA;
                byte[] tmpB = b[pivot];
                b[pivot] = b[r];
                b[r] = tmpB;
            }
            pivotColByRow[r] = c;
            pivotRowByCol[c] = r;

            for (int i = 0; i < m; i++) {
                if (i != r && a[i][c]) {
                    for (int j = c; j < n; j++) {
                        a[i][j] ^= a[r][j];
                    }
                    BytesXor.xorInPlace(b[i], b[r]);
                }
            }
            r++;
        }

        byte[][] x = new byte[n][symbolSize];
        for (int row = 0; row < m; row++) {
            int c = pivotColByRow[row];
            if (c >= 0) {
                x[c] = Arrays.copyOf(b[row], symbolSize);
            }
        }

        for (int c = 0; c < k; c++) {
            int row = pivotRowByCol[c];
            if (row < 0 || dependsOnFree(a[row], pivotRowByCol, n)) {
                throw new IllegalStateException("Недостаточно независимых символов для восстановления исходных данных");
            }
        }

        byte[] result = new byte[k * symbolSize];
        for (int i = 0; i < k; i++) {
            System.arraycopy(x[i], 0, result, i * symbolSize, symbolSize);
        }
        if (originalLength == result.length) {
            return result;
        }
        return Arrays.copyOf(result, originalLength);
    }
}
