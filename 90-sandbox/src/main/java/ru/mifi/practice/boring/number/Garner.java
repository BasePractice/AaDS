package ru.mifi.practice.boring.number;

import java.util.ArrayList;
import java.util.List;

/**
 * Восстановление числа по остаткам — алгоритм Гарнера, практичная форма китайской теоремы об
 * остатках. Теорема говорит: если модули попарно взаимно просты, то число, меньшее их
 * произведения, однозначно определяется своими остатками. Гарнер показывает, как его собрать, не
 * считая само произведение — а оно может не поместиться ни в какой машинный тип.
 *
 * <p>Число записывается в смешанной системе счисления: x = a₁ + a₂·m₁ + a₃·m₁·m₂ + ... Цифры aᵢ
 * находятся по очереди, и каждая требует обратного элемента по модулю — его даёт расширенный
 * алгоритм Евклида. Все вычисления идут по модулю очередного mᵢ, то есть в пределах небольших
 * чисел.
 *
 * <p>Так считают в криптографии и компьютерной алгебре: длинное число заменяют набором остатков
 * по нескольким простым модулям, все операции делают с остатками независимо — их можно ещё и
 * распараллелить, — а в конце собирают ответ Гарнером.
 */
public final class Garner {

    /** Число, дающее заданные остатки по заданным попарно взаимно простым модулям. */
    public long restore(List<Long> remainders, List<Long> modules) {
        if (remainders.size() != modules.size()) {
            throw new IllegalArgumentException("Остатков и модулей должно быть поровну");
        }
        List<Long> digits = digits(remainders, modules);
        long restored = 0;
        long weight = 1;
        for (int at = 0; at < digits.size(); at++) {
            restored += digits.get(at) * weight;
            weight *= modules.get(at);
        }
        return restored;
    }

    /** Цифры смешанной системы счисления — по одной на каждый модуль. */
    private List<Long> digits(List<Long> remainders, List<Long> modules) {
        List<Long> digits = new ArrayList<>();
        for (int at = 0; at < modules.size(); at++) {
            long digit = remainders.get(at) % modules.get(at);
            for (int before = 0; before < at; before++) {
                digit = (digit - digits.get(before)) % modules.get(at);
                digit = digit * inverse(modules.get(before), modules.get(at)) % modules.get(at);
                digit = (digit + modules.get(at)) % modules.get(at);
            }
            digits.add(digit);
        }
        return digits;
    }

    /** Обратный элемент по модулю расширенным алгоритмом Евклида. */
    private long inverse(long value, long modulus) {
        long previous = 0;
        long current = 1;
        long left = modulus;
        long right = value % modulus;
        while (right != 0) {
            long quotient = left / right;
            long rest = left - quotient * right;
            left = right;
            right = rest;
            long carry = previous - quotient * current;
            previous = current;
            current = carry;
        }
        if (left != 1) {
            throw new IllegalArgumentException("Модули " + value + " и " + modulus + " не взаимно просты");
        }
        return (previous % modulus + modulus) % modulus;
    }
}
