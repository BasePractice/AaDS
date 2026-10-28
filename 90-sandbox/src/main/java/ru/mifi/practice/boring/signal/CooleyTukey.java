package ru.mifi.practice.boring.signal;

/**
 * Быстрое преобразование Фурье по Кули — Тьюки. Сумма по всем отсчётам разбивается на две: по
 * чётным номерам и по нечётным. Каждая из половин — то же преобразование вдвое меньшей длины, а
 * склеиваются они умножением на поворачивающий множитель. Отсюда n log n вместо n².
 *
 * <p>Алгоритм 1965 года перевернул цифровую обработку сигналов: спектр, который считался часами,
 * стал считаться секунды, и на этом выросли и сжатие звука, и томография, и цифровая связь.
 * Позже выяснилось, что то же самое знал ещё Гаусс в 1805 году, но его записи остались в
 * черновиках.
 *
 * <p>Реализация итеративная, без рекурсии. Сначала отсчёты переставляются в порядке битовой
 * инверсии: номер записывается двоичным числом задом наперёд. После такой перестановки половины,
 * которые рекурсия разделяла бы, оказываются рядом, и остаётся log n проходов «бабочками» —
 * парами, где одна пара даёт сумму и разность.
 *
 * <p>Длина сигнала должна быть степенью двойки — иначе пополам не поделить.
 */
public final class CooleyTukey implements Fourier {

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
        if (Integer.bitCount(values.length) != 1) {
            throw new IllegalArgumentException("Длина сигнала должна быть степенью двойки, а не " + values.length);
        }
        Complex[] transformed = reordered(values);
        for (int length = 2; length <= transformed.length; length *= 2) {
            Complex step = Complex.turn(direction * 2 * Math.PI / length);
            for (int start = 0; start < transformed.length; start += length) {
                butterfly(transformed, start, length, step);
            }
        }
        return transformed;
    }

    /** Бабочка: пара отсчётов заменяется своей суммой и разностью с поворотом. */
    private void butterfly(Complex[] values, int start, int length, Complex step) {
        Complex turn = new Complex(1, 0);
        for (int at = 0; at < length / 2; at++) {
            Complex even = values[start + at];
            Complex odd = values[start + at + length / 2].times(turn);
            values[start + at] = even.plus(odd);
            values[start + at + length / 2] = even.minus(odd);
            turn = turn.times(step);
        }
    }

    /** Перестановка в порядке битовой инверсии: номер читается справа налево. */
    private Complex[] reordered(Complex[] values) {
        Complex[] reordered = values.clone();
        int bits = Integer.numberOfTrailingZeros(values.length);
        for (int at = 0; at < values.length; at++) {
            int mirrored = Integer.reverse(at) >>> 32 - bits;
            if (at < mirrored) {
                Complex carry = reordered[at];
                reordered[at] = reordered[mirrored];
                reordered[mirrored] = carry;
            }
        }
        return reordered;
    }
}
