package ru.mifi.practice.boring.signal;

/**
 * Преобразование Фурье по определению: каждая частота считается своей суммой по всем отсчётам
 * сигнала. n частот, n слагаемых в каждой — n² умножений комплексных чисел.
 *
 * <p>Формула здесь ровно та, что в учебнике, и потому этот класс годится в эталоны: быстрое
 * преобразование считает то же самое, но такой хитрой перестановкой действий, что глазами его
 * правильность не проверить.
 */
public final class PlainFourier implements Fourier {

    @Override
    public Complex[] direct(Complex[] signal) {
        return transformed(signal, -1);
    }

    @Override
    public Complex[] inverse(Complex[] spectrum) {
        Complex[] signal = transformed(spectrum, 1);
        for (int at = 0; at < signal.length; at++) {
            signal[at] = signal[at].divided(signal.length);
        }
        return signal;
    }

    private Complex[] transformed(Complex[] values, int direction) {
        Complex[] transformed = new Complex[values.length];
        for (int frequency = 0; frequency < values.length; frequency++) {
            Complex total = new Complex(0, 0);
            for (int at = 0; at < values.length; at++) {
                double angle = direction * 2 * Math.PI * frequency * at / values.length;
                total = total.plus(values[at].times(Complex.turn(angle)));
            }
            transformed[frequency] = total;
        }
        return transformed;
    }
}
