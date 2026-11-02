package ru.mifi.practice.vol9;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.math.BigInteger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Звёзды и перегородки")
final class StarsAndBarsTest {

    @DisplayName("Пять шаров в три ящика раскладываются двадцать одним способом")
    @Test
    @Timeout(1)
    void countsDistributions() {
        assertThat("the number of distributions dont equal 21",
            StarsAndBars.number(5, 3), is(BigInteger.valueOf(21)));
    }

    @DisplayName("Пустая раскладка единственна")
    @Test
    @Timeout(1)
    void countsEmptyDistribution() {
        assertThat("an empty distribution dont have exactly one way",
            StarsAndBars.number(0, 3), is(BigInteger.ONE));
    }

    @DisplayName("Без ящиков раскладок нет")
    @Test
    @Timeout(1)
    void countsNothingWithoutBins() {
        assertThat("distributions without bins are counted",
            StarsAndBars.number(5, 0), is(BigInteger.ZERO));
    }
}
