package ru.mifi.practice.voln.codes;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Энкодер упрощённого Raptor-кода (LT + разрежённый precode над GF(2)).
 *
 * <p>Использование:
 * <pre>
 * byte[] data = ...;
 * RaptorConfig cfg = RaptorConfig.defaults(1024, 12345L);
 * RaptorEncoder enc = RaptorEncoder.fromData(data, cfg);
 * EncodedSymbol s0 = enc.nextSymbol(0L);
 * EncodedSymbol s1 = enc.nextSymbol(1L);
 * // ... отправляйте символы sN в произвольном количестве и порядке
 * </pre>
 */
public final class RaptorEncoder {
    private static final int MIN_PARITY_DEGREE = 2;

    private final RaptorConfig config;
    private final int k;
    private final int totalIntermediates; // k + s
    private final int symbolSize;
    private final int parityCount;
    private final long seed;
    private final int originalLength;

    private final byte[][] intermediates; // 0..k-1 исходные блоки, далее проверки
    private final RobustSolitonDistribution distribution;

    private RaptorEncoder(RaptorConfig config, byte[][] sources, int originalLength) {
        this.config = config;
        this.symbolSize = config.symbolSize();
        this.parityCount = config.parityCount();
        this.seed = config.seed();
        this.k = sources.length;
        this.totalIntermediates = k + parityCount;
        this.originalLength = originalLength;
        this.intermediates = buildIntermediates(sources);
        this.distribution = new RobustSolitonDistribution(totalIntermediates, config.c(), config.delta());
    }

    /**
     * Создаёт энкодер из исходного массива байтов.
     * Паддинг нулями добавляется в последний блок при необходимости.
     * @param data данные
     * @param config конфигурация
     * @return энкодер
     */
    public static RaptorEncoder fromData(byte[] data, RaptorConfig config) {
        if (data == null) {
            throw new IllegalArgumentException("data == null");
        }
        int symbolSize = config.symbolSize();
        if (data.length == 0) {
            // Пустой поток: k = 0, но для совместимости создаём k=1 с нулевым символом и длиной 0
            byte[][] oneZero = new byte[][]{new byte[symbolSize]};
            return new RaptorEncoder(config, oneZero, 0);
        }
        int k = (data.length + symbolSize - 1) / symbolSize;
        byte[][] sources = new byte[k][symbolSize];
        int pos = 0;
        for (int i = 0; i < k; i++) {
            int remain = Math.min(symbolSize, data.length - pos);
            System.arraycopy(data, pos, sources[i], 0, remain);
            if (remain < symbolSize) {
                Arrays.fill(sources[i], remain, symbolSize, (byte) 0);
            }
            pos += remain;
        }
        return new RaptorEncoder(config, sources, data.length);
    }

    private byte[][] buildIntermediates(byte[][] sources) {
        int n = k + parityCount;
        byte[][] interm = new byte[n][];
        for (int i = 0; i < k; i++) {
            interm[i] = Arrays.copyOf(sources[i], symbolSize);
        }
        int baseDegree = Math.max(MIN_PARITY_DEGREE, (int) Math.round(Math.log(Math.max(2, k))));
        for (int j = 0; j < parityCount; j++) {
            int deg = Math.min(baseDegree + (j % 2), k); // чуть варьируем степень
            Set<Integer> idx = uniqueIndices(k, deg, new DeterministicRandom(seed ^ (0x5EEDL + j)));
            byte[] acc = BytesXor.zeros(symbolSize);
            for (int id : idx) {
                BytesXor.xorInPlace(acc, interm[id]);
            }
            interm[k + j] = acc;
        }
        return interm;
    }

    private static Set<Integer> uniqueIndices(int bound, int count, DeterministicRandom rnd) {
        Set<Integer> set = new LinkedHashSet<>();
        while (set.size() < count) {
            set.add(rnd.nextInt(bound));
        }
        return set;
    }

    /**
     * Генерирует очередной LT-символ с заданным идентификатором.
     * Идентификатор влияет на детерминированный выбор соседей.
     * @param id идентификатор символа (может быть номер по порядку)
     * @return закодированный символ
     */
    public EncodedSymbol nextSymbol(long id) {
        DeterministicRandom rnd = new DeterministicRandom(seed ^ id);
        int degree = distribution.sampleDegree(rnd);
        if (degree <= 0) {
            degree = 1;
        }
        Set<Integer> neigh = uniqueIndices(totalIntermediates, degree, rnd);
        byte[] acc = BytesXor.zeros(symbolSize);
        int[] neighbors = new int[neigh.size()];
        int i = 0;
        for (int idx : neigh) {
            neighbors[i] = idx;
            i++;
            BytesXor.xorInPlace(acc, intermediates[idx]);
        }
        return new EncodedSymbol(id, neighbors, acc);
    }

    public int k() {
        return k;
    }

    public int parityCount() {
        return parityCount;
    }

    public int totalIntermediates() {
        return totalIntermediates;
    }

    public int symbolSize() {
        return symbolSize;
    }

    public long seed() {
        return seed;
    }

    public int originalLength() {
        return originalLength;
    }

    public RaptorConfig config() {
        return config;
    }
}
