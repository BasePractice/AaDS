package ru.mifi.practice.boring.signal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Быстрое преобразование Фурье")
final class CooleyTukeyTest {

    @DisplayName("Постоянный сигнал даёт единственную нулевую частоту")
    @Test
    @Timeout(1)
    void findsTheConstantAsZeroFrequency() {
        Complex[] spectrum = new CooleyTukey().direct(signal(1, 1, 1, 1));
        assertThat("the constant signal is not a single zero frequency",
            spectrum[0].real(), closeTo(4.0, 1e-9));
    }

    @DisplayName("У постоянного сигнала прочие частоты пусты")
    @Test
    @Timeout(1)
    void findsNoOtherFrequencies() {
        Complex[] spectrum = new CooleyTukey().direct(signal(1, 1, 1, 1));
        assertThat("the constant signal got a non zero frequency",
            spectrum[1].magnitude(), closeTo(0.0, 1e-9));
    }

    @DisplayName("Синусоида даёт всплеск на своей частоте")
    @Test
    @Timeout(1)
    void findsTheFrequencyOfASine() {
        Complex[] signal = new Complex[8];
        for (int at = 0; at < signal.length; at++) {
            signal[at] = new Complex(Math.cos(2 * Math.PI * at / 8), 0);
        }
        Complex[] spectrum = new CooleyTukey().direct(signal);
        assertThat("the frequency of the sine is missed", spectrum[1].magnitude(), closeTo(4.0, 1e-9));
    }

    @DisplayName("Обратное преобразование возвращает сигнал")
    @Test
    @Timeout(1)
    void restoresTheSignal() {
        Fourier fourier = new CooleyTukey();
        Complex[] signal = signal(1, 2, 3, 4, 5, 6, 7, 8);
        assertThat("the signal dont survive the round trip",
            fourier.inverse(fourier.direct(signal))[3].real(), closeTo(4.0, 1e-9));
    }

    @DisplayName("Считает то же, что преобразование по определению")
    @Test
    @Timeout(10)
    void agreesWithThePlainTransform() {
        Random random = new Random(20260815L);
        Fourier fast = new CooleyTukey();
        Fourier plain = new PlainFourier();
        double difference = 0;
        for (int attempt = 0; attempt < 50; attempt++) {
            Complex[] signal = new Complex[16];
            for (int at = 0; at < signal.length; at++) {
                signal[at] = new Complex(random.nextInt(20) - 10, random.nextInt(20) - 10);
            }
            Complex[] fastSpectrum = fast.direct(signal);
            Complex[] plainSpectrum = plain.direct(signal);
            for (int at = 0; at < signal.length; at++) {
                difference = Math.max(difference, fastSpectrum[at].minus(plainSpectrum[at]).magnitude());
            }
        }
        assertThat("the fast transform disagrees with the plain one", difference, closeTo(0.0, 1e-9));
    }

    @DisplayName("Длина не степени двойки отвергается сразу")
    @Test
    @Timeout(1)
    void refusesTheLengthOutOfPowerOfTwo() {
        assertThat("a length that is not a power of two is accepted",
            assertThrows(IllegalArgumentException.class,
                () -> new CooleyTukey().direct(signal(1, 2, 3))).getMessage(),
            is("Длина сигнала должна быть степенью двойки, а не 3"));
    }

    private static Complex[] signal(double... values) {
        Complex[] signal = new Complex[values.length];
        for (int at = 0; at < values.length; at++) {
            signal[at] = new Complex(values[at], 0);
        }
        return signal;
    }
}
