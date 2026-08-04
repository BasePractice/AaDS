package ru.mifi.practice.vol4.hash;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import ru.mifi.practice.commons.Counter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

/**
 * Проверка накопленных степеней и префиксных хешей.
 *
 * <p>Здесь проверяется только сам накопитель: длина, детерминированность и границы. Сравнение
 * хешей подстрок остаётся учебным заданием в PolynomialSearchCached и здесь не закрепляется.
 */
@DisplayName("Полиномиальный хеш с кэшем степеней")
final class CachedPolynomialTest {

    @DisplayName("Длина массива хешей равна длине текста")
    @Test
    @Timeout(1)
    void givesOneHashPerCharacter() {
        assertThat("the hashing dont give one hash per character",
            new CachedPolynomial().hashing("алгоритм", Counter.create()).length, is(8));
    }

    @DisplayName("Пустой текст даёт пустой массив хешей")
    @Test
    @Timeout(1)
    void givesNoHashesForAnEmptyText() {
        assertThat("an empty text gives hashes",
            new CachedPolynomial().hashing("", Counter.create()).length, is(0));
    }

    @DisplayName("Хеширование детерминировано")
    @Test
    @Timeout(1)
    void repeatsTheHashesForTheSameText() {
        int first = hashOf("алгоритм");
        assertThat("the same text gives different hashes", hashOf("алгоритм"), is(first));
    }

    /** Хеш по индексу накопительный, поэтому тексты берутся разной последней буквой, а не длиной. */
    @DisplayName("Разные тексты дают разные хеши")
    @Test
    @Timeout(1)
    void separatesDifferentTexts() {
        int other = hashOf("алгоритп");
        assertThat("different texts give the same hash", hashOf("алгоритм"), is(not(other)));
    }

    /** Хеш последнего символа: он накопил всю строку, поэтому по нему тексты и различаются. */
    private static int hashOf(String text) {
        return new CachedPolynomial().hashing(text, Counter.create())[text.length() - 1];
    }

    @DisplayName("Каждый символ стоит один шаг")
    @Test
    @Timeout(1)
    void countsOneStepPerCharacter() {
        Counter counter = Counter.create();
        new CachedPolynomial().hashing("алгоритм", counter);
        assertThat("a character costs a different number of steps", counter.count(), is(8));
    }

    @DisplayName("Перевёрнутый диапазон даёт ноль")
    @Test
    @Timeout(1)
    void givesZeroForAnInvertedRange() {
        CachedPolynomial polynomial = new CachedPolynomial();
        assertThat("an inverted range gives something other than zero",
            polynomial.hash(polynomial.hashing("алгоритм", Counter.create()), 5, 2), is(0));
    }

    @DisplayName("Пустой массив хешей даёт ноль")
    @Test
    @Timeout(1)
    void givesZeroForAnEmptyHashArray() {
        assertThat("an empty hash array gives something other than zero",
            new CachedPolynomial().hash(new int[0], 0, 0), is(0));
    }

    @DisplayName("Первая степень равна единице")
    @Test
    @Timeout(1)
    void startsThePowersFromOne() {
        assertThat("the powers dont start from one", new CachedPolynomial().polynomials[0], is(1));
    }
}
