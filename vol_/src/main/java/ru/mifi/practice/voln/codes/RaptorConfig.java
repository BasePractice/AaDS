package ru.mifi.practice.voln.codes;

/**
 * Конфигурация кодера/декодера Raptor.
 * По умолчанию использует устойчивое солитонное распределение степеней
 * с параметрами c=0.1 и delta=0.5 и небольшое число проверочных символов S.
 */
public final class RaptorConfig {
    /** Значение по умолчанию для параметра c устойчивого солитона. */
    public static final double DEFAULT_C = 0.1d;
    /** Значение по умолчанию для параметра delta устойчивого солитона. */
    public static final double DEFAULT_DELTA = 0.5d;
    /** Значение по умолчанию для количества проверочных символов. */
    public static final int DEFAULT_PARITIES = 8;

    private final int symbolSize;
    private final int parityCount;
    private final double c;
    private final double delta;
    private final long seed;

    private RaptorConfig(int symbolSize, int parityCount, double c, double delta, long seed) {
        if (symbolSize <= 0) {
            throw new IllegalArgumentException("Размер символа должен быть > 0");
        }
        if (parityCount < 0) {
            throw new IllegalArgumentException("Число проверочных символов не может быть отрицательным");
        }
        if (c <= 0.0d || delta <= 0.0d) {
            throw new IllegalArgumentException("Параметры распределения должны быть положительными");
        }
        this.symbolSize = symbolSize;
        this.parityCount = parityCount;
        this.c = c;
        this.delta = delta;
        this.seed = seed;
    }

    /**
     * Создать конфигурацию с явными параметрами.
     * @param symbolSize размер одного символа в байтах
     * @param parityCount количество проверочных символов (precode)
     * @param c параметр устойчивого солитона
     * @param delta параметр устойчивого солитона
     * @param seed базовое зерно для детерминированного выбора соседей
     * @return конфигурация
     */
    public static RaptorConfig of(int symbolSize, int parityCount, double c, double delta, long seed) {
        return new RaptorConfig(symbolSize, parityCount, c, delta, seed);
    }

    /**
     * Создать конфигурацию с параметрами по умолчанию.
     * @param symbolSize размер символа
     * @param seed базовое зерно
     * @return конфигурация
     */
    public static RaptorConfig defaults(int symbolSize, long seed) {
        return new RaptorConfig(symbolSize, DEFAULT_PARITIES, DEFAULT_C, DEFAULT_DELTA, seed);
    }

    public int symbolSize() {
        return symbolSize;
    }

    public int parityCount() {
        return parityCount;
    }

    public double c() {
        return c;
    }

    public double delta() {
        return delta;
    }

    public long seed() {
        return seed;
    }
}
