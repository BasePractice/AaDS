package ru.mifi.practice.boring.number;

/**
 * Умножение столбиком — то самое, которому учат в третьем классе. Каждая цифра одного числа
 * умножается на каждую цифру другого, произведение кладётся в разряд, равный сумме разрядов
 * сомножителей, а переносы разбираются в конце одним проходом.
 *
 * <p>Цена — n·m умножений цифр, то есть O(n²) на числах одной длины. Долгое время считалось, что
 * быстрее и нельзя; Карацуба в 1960 году показал, что можно, и этим закрыл гипотезу Колмогорова
 * о квадратичной нижней оценке.
 */
public final class Schoolbook implements Multiplication {

    @Override
    public Digits product(Digits left, Digits right) {
        if (left.size() == 0 || right.size() == 0) {
            return new Digits(new int[0]);
        }
        int[] product = new int[left.size() + right.size()];
        for (int at = 0; at < left.size(); at++) {
            for (int other = 0; other < right.size(); other++) {
                product[at + other] += left.digit(at) * right.digit(other);
            }
        }
        int carry = 0;
        for (int at = 0; at < product.length; at++) {
            int total = product[at] + carry;
            product[at] = total % 10;
            carry = total / 10;
        }
        return new Digits(product);
    }
}
