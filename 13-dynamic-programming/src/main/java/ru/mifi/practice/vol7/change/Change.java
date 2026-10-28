package ru.mifi.practice.vol7.change;

import ru.mifi.practice.commons.Counter;

import java.util.Arrays;
import java.util.List;
import java.util.OptionalInt;

/**
 * Размен суммы наименьшим числом монет заданных достоинств. Монеты берутся сколько угодно раз, и
 * жадность здесь ошибается: на монетах 1, 3, 4 шестёрка жадно разменивается как 4+1+1, тогда как
 * хватает двух троек. Пустой ответ означает, что такую сумму этими монетами не собрать.
 */
@FunctionalInterface
public interface Change {

    OptionalInt change(int amount, List<Integer> coins, Counter counter);

    /**
     * Перебор: сумма уменьшается на каждую монету по очереди, и лучший из ответов поднимается
     * наверх. Одни и те же остатки пересчитываются заново по многу раз, поэтому цена растёт
     * экспоненциально с суммой.
     */
    final class Recursion implements Change {

        @Override
        public OptionalInt change(int amount, List<Integer> coins, Counter counter) {
            counter.increment();
            if (amount == 0) {
                return OptionalInt.of(0);
            }
            int fewest = Integer.MAX_VALUE;
            for (int coin : coins) {
                if (coin <= amount) {
                    OptionalInt rest = change(amount - coin, coins, counter);
                    if (rest.isPresent() && rest.getAsInt() + 1 < fewest) {
                        fewest = rest.getAsInt() + 1;
                    }
                }
            }
            return fewest == Integer.MAX_VALUE ? OptionalInt.empty() : OptionalInt.of(fewest);
        }
    }

    /**
     * Таблица: ячейка k хранит наименьшее число монет для суммы k и считается один раз, опираясь
     * на уже посчитанные меньшие суммы. Цена — O(сумма × число достоинств), память — линейная.
     */
    final class Dynamited implements Change {

        @Override
        public OptionalInt change(int amount, List<Integer> coins, Counter counter) {
            int[] fewest = new int[amount + 1];
            Arrays.fill(fewest, Integer.MAX_VALUE);
            fewest[0] = 0;
            for (int sum = 1; sum <= amount; sum++) {
                for (int coin : coins) {
                    counter.increment();
                    if (coin <= sum && fewest[sum - coin] != Integer.MAX_VALUE && fewest[sum - coin] + 1 < fewest[sum]) {
                        fewest[sum] = fewest[sum - coin] + 1;
                    }
                }
            }
            return fewest[amount] == Integer.MAX_VALUE ? OptionalInt.empty() : OptionalInt.of(fewest[amount]);
        }
    }
}
