package ru.mifi.practice.voln.codes;

/**
 * Устойчивое солитонное распределение (Robust Soliton) для выбора степени LT.
 * Реализация следует определению из Luby 2002, в упрощённом виде.
 */
public final class RobustSolitonDistribution {
    private static final int MIN_DEGREE = 1;

    private final int k;
    private final double c;
    private final double delta;
    private final double[] cdf; // накопленные вероятности для d = 1..k

    /**
     * Создаёт распределение для K исходных символов.
     * @param k число исходных символов
     * @param c параметр c > 0
     * @param delta параметр delta в (0, 1)
     */
    public RobustSolitonDistribution(int k, double c, double delta) {
        if (k <= 0) {
            throw new IllegalArgumentException("k должно быть > 0");
        }
        if (c <= 0.0d) {
            throw new IllegalArgumentException("c должно быть > 0");
        }
        if (delta <= 0.0d || delta >= 1.0d) {
            throw new IllegalArgumentException("delta должно быть в (0, 1)");
        }
        this.k = k;
        this.c = c;
        this.delta = delta;
        this.cdf = buildCdf();
    }

    private double[] buildCdf() {
        double r = c * Math.log(k / delta) * Math.sqrt(k);
        int threshold = (int) Math.floor(k / r);
        if (threshold < 1) {
            threshold = 1;
        }

        double[] rho = new double[k + 1];
        // Идеальное солитонное распределение
        rho[1] = 1.0d / k;
        for (int d = 2; d <= k; d++) {
            rho[d] = 1.0d / (d * (d - 1.0d));
        }

        double[] tau = new double[k + 1];
        for (int d = 1; d < threshold; d++) {
            tau[d] = r / (d * k);
        }
        if (threshold <= k) {
            tau[threshold] = r * Math.log(r / delta) / k;
        }
        // Нормировка
        double z = 0.0d;
        for (int d = 1; d <= k; d++) {
            z += rho[d] + tau[d];
        }
        double[] cdfLocal = new double[k + 1];
        double acc = 0.0d;
        for (int d = 1; d <= k; d++) {
            acc += (rho[d] + tau[d]) / z;
            cdfLocal[d] = acc;
        }
        cdfLocal[k] = 1.0d; // защита от накопленной ошибки
        return cdfLocal;
    }

    /**
     * Выбор степени d в диапазоне [1..k] согласно распределению.
     * @param rnd генератор случайных чисел
     * @return степень d
     */
    public int sampleDegree(DeterministicRandom rnd) {
        double r = rnd.nextDouble();
        for (int d = MIN_DEGREE; d <= k; d++) {
            if (r <= cdf[d]) {
                return d;
            }
        }
        return k;
    }
}
