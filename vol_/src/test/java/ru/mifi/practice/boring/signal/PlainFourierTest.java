package ru.mifi.practice.boring.signal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;

@DisplayName("Преобразование Фурье по определению")
final class PlainFourierTest {

    @DisplayName("Нулевая частота равна сумме отсчётов")
    @Test
    @Timeout(1)
    void countsTheZeroFrequencyAsTheSum() {
        Complex[] spectrum = new PlainFourier().direct(signal(1, 2, 3, 4, 5));
        assertThat("the zero frequency is not the sum of the samples",
            spectrum[0].real(), closeTo(15.0, 1e-9));
    }

    @DisplayName("Обратное преобразование возвращает сигнал")
    @Test
    @Timeout(1)
    void restoresTheSignal() {
        Fourier fourier = new PlainFourier();
        assertThat("the signal dont survive the round trip",
            fourier.inverse(fourier.direct(signal(1, 2, 3, 4, 5)))[2].real(), closeTo(3.0, 1e-9));
    }

    @DisplayName("Длина, не равная степени двойки, тоже считается")
    @Test
    @Timeout(1)
    void handlesTheLengthOutOfPowerOfTwo() {
        Complex[] spectrum = new PlainFourier().direct(signal(1, 1, 1));
        assertThat("an odd length breaks the plain transform", spectrum[0].real(), closeTo(3.0, 1e-9));
    }

    private static Complex[] signal(double... values) {
        Complex[] signal = new Complex[values.length];
        for (int at = 0; at < values.length; at++) {
            signal[at] = new Complex(values[at], 0);
        }
        return signal;
    }
}
