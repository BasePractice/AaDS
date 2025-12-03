package ru.mifi.practice.voln.codes;

/**
 * Конфигурация кодера/декодера Raptor.
 * По умолчанию использует устойчивое солитонное распределение степеней
 * с параметрами c=0.1 и delta=0.5 и небольшое число проверочных символов S.
 */
public record RaptorConfiguration(int symbolSize, int parityCount, double c, double delta, long seed) {
    /**
     * Значение по умолчанию для параметра c устойчивого солитона.
     */
    public static final double DEFAULT_C = 0.1d;
    /**
     * Значение по умолчанию для параметра delta устойчивого солитона.
     */
    public static final double DEFAULT_DELTA = 0.5d;
    /**
     * Значение по умолчанию для количества проверочных символов.
     */
    public static final int DEFAULT_PARITIES = 8;

    public RaptorConfiguration {
        if (symbolSize <= 0) {
            throw new IllegalArgumentException("Размер символа должен быть > 0");
        }
        if (parityCount < 0) {
            throw new IllegalArgumentException("Число проверочных символов не может быть отрицательным");
        }
        if (c <= 0.0d || delta <= 0.0d) {
            throw new IllegalArgumentException("Параметры распределения должны быть положительными");
        }
    }

    /**
     * Создать конфигурацию с параметрами по умолчанию.
     *
     * @param symbolSize размер символа
     * @param seed       базовое зерно
     * @return конфигурация
     */
    public static RaptorConfiguration defaults(int symbolSize, long seed) {
        return new RaptorConfiguration(symbolSize, DEFAULT_PARITIES, DEFAULT_C, DEFAULT_DELTA, seed);
    }
}
