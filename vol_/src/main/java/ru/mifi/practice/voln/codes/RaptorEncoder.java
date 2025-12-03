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
public record RaptorEncoder(RaptorConfiguration config,
                            int k,
                            int totalIntermediates,
                            int symbolSize,
                            int parityCount,
                            long seed,
                            int originalLength,
                            byte[][] intermediates,
                            RobustSolitonDistribution distribution) {
    private static final int MIN_PARITY_DEGREE = 2;

    private RaptorEncoder(RaptorConfiguration config, byte[][] sources, int originalLength) {
        this(config, sources.length, sources.length + config.parityCount(), config.symbolSize(),
            config.parityCount(), config.seed(), originalLength,
            buildIntermediates(sources, sources.length, config),
            new RobustSolitonDistribution(sources.length + config.parityCount(), config.c(), config.delta()));
    }

    /**
     * Создаёт энкодер из исходного массива байтов.
     * Паддинг нулями добавляется в последний блок при необходимости.
     *
     * @param data   данные
     * @param config конфигурация
     * @return энкодер
     */
    public static RaptorEncoder fromData(byte[] data, RaptorConfiguration config) {
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

    private static Set<Integer> uniqueIndices(int bound, int count, DeterministicRandom rnd) {
        Set<Integer> set = new LinkedHashSet<>();
        while (set.size() < count) {
            set.add(rnd.nextInt(bound));
        }
        return set;
    }

    private static byte[][] buildIntermediates(byte[][] sources, int k, RaptorConfiguration config) {
        int n = k + config.parityCount();
        byte[][] intermediates = new byte[n][];
        for (int i = 0; i < k; i++) {
            intermediates[i] = Arrays.copyOf(sources[i], config.symbolSize());
        }
        int baseDegree = Math.max(MIN_PARITY_DEGREE, (int) Math.round(Math.log(Math.max(2, k))));
        for (int j = 0; j < config.parityCount(); j++) {
            int deg = Math.min(baseDegree + (j % 2), k);
            Set<Integer> idx = uniqueIndices(k, deg, new DeterministicRandom(config.seed() ^ (0x5EEDL + j)));
            byte[] acc = BytesXor.zeros(config.symbolSize());
            for (int id : idx) {
                BytesXor.xorInPlace(acc, intermediates[id]);
            }
            intermediates[k + j] = acc;
        }
        return intermediates;
    }

    /**
     * Генерирует очередной LT-символ с заданным идентификатором.
     * Идентификатор влияет на детерминированный выбор соседей.
     *
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
        for (int index : neigh) {
            neighbors[i] = index;
            i++;
            BytesXor.xorInPlace(acc, intermediates[index]);
        }
        return new EncodedSymbol(id, neighbors, acc);
    }
}
