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

    private RaptorDecoder(RaptorConfig config, int k, int originalLength) {
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
    }

    /**
     * Создаёт декодер.
     * @param config конфигурация
     * @param k число исходных символов у источника
     * @param originalLength исходная длина потока байтов
     * @return декодер
     */
    public static RaptorDecoder create(RaptorConfig config, int k, int originalLength) {
        return new RaptorDecoder(config, k, originalLength);
    }

    /**
     * Добавляет один принятый закодированный символ.
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
        // Нормализация: отсортируем и удалим повторы по модулю 2 (двойные включения обнуляются)
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
            // Полезной информации нет
            return;
        }
        int[] row = Arrays.copyOf(compact, degree);
        rows.add(row);
        rhs.add(symbol.payload());
    }

    /**
     * Пытается декодировать исходные данные.
     * @return восстановленные данные (обрезанные до исходной длины)
     */
    public byte[] decode() {
        if (originalLength == 0) {
            return new byte[0];
        }
        if (rows.isEmpty()) {
            throw new DecodingFailedException("Недостаточно символов для декодирования");
        }
        int m = rows.size();
        int n = totalIntermediates;
        // Построим матрицу MxN над GF(2) и правую часть Mx(symbolSize)
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

        int r = 0; // текущая строка-пивот
        for (int c = 0; c < n && r < m; c++) {
            int pivot = -1;
            for (int i = r; i < m; i++) {
                if (a[i][c]) {
                    pivot = i;
                    break;
                }
            }
            if (pivot == -1) {
                continue; // в этом столбце пивота нет
            }
            // Переместить найденную строку наверх блока
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

            // Обнулим все остальные единицы в столбце c
            for (int i = 0; i < m; i++) {
                if (i != r && a[i][c]) {
                    // row_i = row_i XOR row_r
                    for (int j = c; j < n; j++) {
                        a[i][j] ^= a[r][j];
                    }
                    BytesXor.xorInPlace(b[i], b[r]);
                }
            }
            r++;
        }

        // Соберём решения для всех столбцов, где есть пивот
        byte[][] x = new byte[n][symbolSize];
        for (int row = 0; row < m; row++) {
            int c = pivotColByRow[row];
            if (c >= 0) {
                x[c] = Arrays.copyOf(b[row], symbolSize);
            }
        }

        // Проверим, что все первые k столбцов решены (есть пивоты)
        for (int c = 0; c < k; c++) {
            if (pivotRowByCol[c] < 0) {
                throw new DecodingFailedException("Недостаточно независимых символов для восстановления исходных данных");
            }
        }

        // Соберём байтовый массив исходной длины из первых k символов
        byte[] result = new byte[k * symbolSize];
        for (int i = 0; i < k; i++) {
            System.arraycopy(x[i], 0, result, i * symbolSize, symbolSize);
        }
        if (originalLength == result.length) {
            return result;
        }
        return Arrays.copyOf(result, originalLength);
    }

    public int k() {
        return k;
    }

    public int totalIntermediates() {
        return totalIntermediates;
    }

    public int symbolSize() {
        return symbolSize;
    }

    public int originalLength() {
        return originalLength;
    }
}
