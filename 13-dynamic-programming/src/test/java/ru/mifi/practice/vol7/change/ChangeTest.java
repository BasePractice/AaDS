package ru.mifi.practice.vol7.change;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import ru.mifi.practice.commons.Counter;

import java.util.List;
import java.util.OptionalInt;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;

@DisplayName("Размен монет")
final class ChangeTest {

    @DisplayName("Разменивает наименьшим числом монет")
    @Test
    @Timeout(1)
    void changesWithTheFewestCoins() {
        assertThat("the change takes more coins than needed",
            new Change.Dynamited().change(11, List.of(1, 2, 5), Counter.create()), is(OptionalInt.of(3)));
    }

    @DisplayName("Нулевая сумма разменивается пустой горстью")
    @Test
    @Timeout(1)
    void changesZeroWithNoCoins() {
        assertThat("the zero amount takes a coin",
            new Change.Dynamited().change(0, List.of(1, 2, 5), Counter.create()), is(OptionalInt.of(0)));
    }

    @DisplayName("Отказывается разменивать сумму, которую монетами не собрать")
    @Test
    @Timeout(1)
    void refusesTheAmountItCannotCollect() {
        assertThat("the impossible amount is reported as changed",
            new Change.Dynamited().change(3, List.of(5), Counter.create()), is(OptionalInt.empty()));
    }

    @DisplayName("Берёт крупную монету вместо горсти мелких")
    @Test
    @Timeout(1)
    void prefersTheLargeCoinToTheHandfulOfSmallOnes() {
        assertThat("the greedy handful wins over the single large coin",
            new Change.Dynamited().change(6, List.of(1, 3, 4), Counter.create()), is(OptionalInt.of(2)));
    }

    @DisplayName("Перебор находит то же, что таблица")
    @Test
    @Timeout(5)
    void bruteAgreesWithTheTable() {
        Random random = new Random(20260815L);
        int mismatches = 0;
        for (int attempt = 0; attempt < 30; attempt++) {
            int amount = random.nextInt(11);
            List<Integer> coins = List.of(1 + random.nextInt(4), 1 + random.nextInt(7), 1 + random.nextInt(9));
            if (!new Change.Recursion().change(amount, coins, Counter.create())
                .equals(new Change.Dynamited().change(amount, coins, Counter.create()))) {
                ++mismatches;
            }
        }
        assertThat("the brute force search disagrees with the table", mismatches, is(0));
    }

    @DisplayName("Таблица обходится дешевле перебора")
    @Test
    @Timeout(5)
    void costsLessThanTheBruteForce() {
        Counter table = Counter.create();
        Counter brute = Counter.create();
        new Change.Dynamited().change(12, List.of(1, 3, 4), table);
        new Change.Recursion().change(12, List.of(1, 3, 4), brute);
        assertThat("the table costs no less than the brute force search",
            table.count(), lessThan(brute.count()));
    }
}
