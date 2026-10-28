package ru.mifi.practice.boring.number;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.math.BigInteger;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Умножение длинных чисел по Карацубе")
final class KaratsubaTest {

    @DisplayName("Перемножает короткие числа")
    @Test
    @Timeout(1)
    void multipliesShortNumbers() {
        assertThat("the short numbers are multiplied wrong",
            new Karatsuba().product(new Digits("12"), new Digits("34")).toString(), is("408"));
    }

    @DisplayName("Перемножает числа длиннее порога рекурсии")
    @Test
    @Timeout(1)
    void multipliesNumbersBeyondTheThreshold() {
        assertThat("the long numbers are multiplied wrong",
            new Karatsuba().product(new Digits("123456789"), new Digits("987654321")).toString(),
            is("121932631112635269"));
    }

    @DisplayName("Умножение на ноль даёт ноль")
    @Test
    @Timeout(1)
    void multipliesByZero() {
        assertThat("the product with zero is not zero",
            new Karatsuba().product(new Digits("123456789012345"), new Digits("0")).toString(), is("0"));
    }

    @DisplayName("Числа разной длины перемножаются верно")
    @Test
    @Timeout(1)
    void multipliesNumbersOfDifferentLength() {
        assertThat("the numbers of different length are multiplied wrong",
            new Karatsuba().product(new Digits("1000000000000"), new Digits("7")).toString(),
            is("7000000000000"));
    }

    @DisplayName("Считает то же, что умножение столбиком")
    @Test
    @Timeout(10)
    void agreesWithTheSchoolbook() {
        Random random = new Random(20260815L);
        Multiplication karatsuba = new Karatsuba();
        Multiplication schoolbook = new Schoolbook();
        int mismatches = 0;
        for (int attempt = 0; attempt < 200; attempt++) {
            Digits left = new Digits(number(random, 1 + random.nextInt(40)));
            Digits right = new Digits(number(random, 1 + random.nextInt(40)));
            if (!karatsuba.product(left, right).equals(schoolbook.product(left, right))) {
                ++mismatches;
            }
        }
        assertThat("karatsuba disagrees with the schoolbook multiplication", mismatches, is(0));
    }

    @DisplayName("Считает то же, что штатная длинная арифметика")
    @Test
    @Timeout(10)
    void agreesWithTheStandardArithmetic() {
        Random random = new Random(20260815L);
        int mismatches = 0;
        for (int attempt = 0; attempt < 100; attempt++) {
            String left = number(random, 1 + random.nextInt(60));
            String right = number(random, 1 + random.nextInt(60));
            String expected = new BigInteger(left).multiply(new BigInteger(right)).toString();
            if (!new Karatsuba().product(new Digits(left), new Digits(right)).toString().equals(expected)) {
                ++mismatches;
            }
        }
        assertThat("karatsuba disagrees with the standard arithmetic", mismatches, is(0));
    }

    private static String number(Random random, int length) {
        StringBuilder number = new StringBuilder().append((char) ('1' + random.nextInt(9)));
        for (int at = 1; at < length; at++) {
            number.append((char) ('0' + random.nextInt(10)));
        }
        return number.toString();
    }
}
