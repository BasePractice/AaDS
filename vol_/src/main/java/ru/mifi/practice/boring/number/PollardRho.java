package ru.mifi.practice.boring.number;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Разложение числа на простые множители ро-методом Полларда. Перебор делителей находит множитель
 * за время порядка корня из числа; Поллард — за корень четвёртой степени, и это разница между
 * часами и мгновением на числах в двадцать знаков.
 *
 * <p>Работает на парадоксе дней рождения. Берём псевдослучайную последовательность
 * x → x²+c по модулю n. По модулю неизвестного делителя p она зациклится примерно через корень
 * из p шагов — куда раньше, чем по модулю самого n. Значит найдутся два члена,
 * совпадающие по модулю p и различные по модулю n, и наибольший общий делитель их разности с n
 * даст сам p.
 *
 * <p>Название — от формы траектории: последовательность идёт хвостом, а потом закольцовывается,
 * и картинка похожа на греческую букву ро. Цикл ищется способом Флойда: два указателя, один
 * шагает вдвое быстрее.
 *
 * <p>Метод находит какой-то делитель, а не обязательно простой, поэтому он применяется
 * рекурсивно, а простота проверяется тестом Миллера — Рабина.
 */
public final class PollardRho {
    private final Primality primality = new MillerRabin();

    /** Простые множители числа по возрастанию, с повторами. */
    public List<Long> factors(long number) {
        if (number < 2) {
            throw new IllegalArgumentException("Разложению на множители подлежат числа от двух, а не " + number);
        }
        List<Long> factors = new ArrayList<>();
        split(number, factors);
        Collections.sort(factors);
        return factors;
    }

    private void split(long number, List<Long> factors) {
        if (number == 1) {
            return;
        }
        if (primality.prime(number)) {
            factors.add(number);
            return;
        }
        long divisor = divisor(number);
        split(divisor, factors);
        split(number / divisor, factors);
    }

    /** Какой-нибудь нетривиальный делитель составного числа. */
    private long divisor(long number) {
        if (number % 2 == 0) {
            return 2;
        }
        for (long shift = 1; ; shift++) {
            long found = rho(number, shift);
            if (found != number) {
                return found;
            }
        }
    }

    /** Один заход ро-метода: указатели Флойда идут по x²+c, пока не разойдутся по общему делителю. */
    private long rho(long number, long shift) {
        long slow = 2;
        long fast = 2;
        long divisor = 1;
        while (divisor == 1) {
            slow = step(slow, shift, number);
            fast = step(step(fast, shift, number), shift, number);
            divisor = gcd(Math.abs(slow - fast), number);
        }
        return divisor;
    }

    private long step(long value, long shift, long number) {
        return BigInteger.valueOf(value).multiply(BigInteger.valueOf(value))
            .add(BigInteger.valueOf(shift)).mod(BigInteger.valueOf(number)).longValue();
    }

    private long gcd(long left, long right) {
        long first = left;
        long second = right;
        while (second != 0) {
            long rest = first % second;
            first = second;
            second = rest;
        }
        return first;
    }
}
