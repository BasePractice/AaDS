package ru.mifi.practice.boring.number;

/**
 * Умножение по Карацубе — первый алгоритм, побивший школьный столбик. Каждое число делится
 * пополам: x = x₁·10^k + x₀, y = y₁·10^k + y₀. Прямое раскрытие скобок даёт четыре произведения
 * половин, и выигрыша нет. Карацуба заметил, что среднее слагаемое x₁y₀ + x₀y₁ считается через
 * два уже вычисленных: (x₁+x₀)(y₁+y₀) − x₁y₁ − x₀y₀.
 *
 * <p>Три умножения вместо четырёх — и рекуррентность T(n) = 3·T(n/2) вместо 4·T(n/2), то есть
 * O(n^log₂3) ≈ O(n^1.585) вместо O(n²). На тысяче цифр это выигрыш примерно вдесятеро, на
 * миллионе — в сотни раз.
 *
 * <p>На коротких числах рекурсия дороже самого умножения, поэтому ниже порога работа передаётся
 * столбику. Так делают и в настоящих библиотеках, только пороги там подбирают замерами.
 */
public final class Karatsuba implements Multiplication {
    private static final int THRESHOLD = 8;
    private final Multiplication schoolbook = new Schoolbook();

    @Override
    public Digits product(Digits left, Digits right) {
        if (left.size() <= THRESHOLD || right.size() <= THRESHOLD) {
            return schoolbook.product(left, right);
        }
        int half = Math.max(left.size(), right.size()) / 2;
        Digits lowLeft = left.low(half);
        Digits highLeft = left.high(half);
        Digits lowRight = right.low(half);
        Digits highRight = right.high(half);
        Digits lower = product(lowLeft, lowRight);
        Digits higher = product(highLeft, highRight);
        Digits middle = product(lowLeft.plus(highLeft), lowRight.plus(highRight))
            .minus(lower).minus(higher);
        return higher.shifted(half * 2).plus(middle.shifted(half)).plus(lower);
    }
}
