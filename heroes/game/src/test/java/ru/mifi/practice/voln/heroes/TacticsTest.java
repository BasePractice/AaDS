package ru.mifi.practice.voln.heroes;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

/** Проверка жадной тактики: выбор цели, сближение и пропуск хода. */
@DisplayName("Жадная тактика")
final class TacticsTest {

    @DisplayName("Соседнюю цель тактика атакует")
    @Test
    @Timeout(1)
    void attacksTheNeighbourTarget() {
        BattleMap map = new BattleMap();
        map.addLeft(5, 5, stack(100, 0, 100, 5));
        map.addRight(5, 6, stack(10, 0, 10, 5));
        assertThat("tactics dont attack a target standing next to it",
            new Tactics.Greedy().decide(map).kind(), is(Tactics.Kind.ATTACK));
    }

    @DisplayName("Из двух целей тактика выбирает добиваемую")
    @Test
    @Timeout(1)
    void picksTheTargetItCanFinish() {
        BattleMap map = new BattleMap();
        map.addLeft(5, 5, stack(100, 0, 100, 5));
        map.addRight(4, 5, stack(10, 0, 500, 5));
        map.addRight(5, 6, stack(10, 0, 10, 5));
        assertThat("tactics dont prefer the target it can finish off",
            new Tactics.Greedy().decide(map).column(), is(6));
    }

    @DisplayName("Далёкую цель тактика не атакует, а сближается")
    @Test
    @Timeout(1)
    void approachesTheDistantTarget() {
        BattleMap map = new BattleMap();
        map.addLeft(5, 0, stack(100, 0, 100, 3));
        map.addRight(5, 14, stack(10, 0, 10, 3));
        assertThat("tactics dont approach a target out of reach",
            new Tactics.Greedy().decide(map).kind(), is(Tactics.Kind.MOVE));
    }

    @DisplayName("Шаг сближения не длиннее скорости отряда")
    @Test
    @Timeout(1)
    void keepsTheApproachWithinTheSpeed() {
        BattleMap map = new BattleMap();
        map.addLeft(5, 0, stack(100, 0, 100, 3));
        map.addRight(5, 14, stack(10, 0, 10, 3));
        assertThat("the approach step goes further than the stack speed",
            new Tactics.Greedy().decide(map).column(), is(lessThanOrEqualTo(3)));
    }

    @DisplayName("Без противника тактика пропускает ход")
    @Test
    @Timeout(1)
    void skipsTheTurnWithoutAnEnemy() {
        BattleMap map = new BattleMap();
        map.addLeft(5, 5, stack(100, 0, 100, 5));
        assertThat("tactics dont skip the turn when there is nobody to fight",
            new Tactics.Greedy().decide(map).kind(), is(Tactics.Kind.SKIP));
    }

    @DisplayName("На пустом поле тактика пропускает ход")
    @Test
    @Timeout(1)
    void skipsTheTurnOnAnEmptyField() {
        assertThat("tactics dont skip the turn on an empty field",
            new Tactics.Greedy().decide(new BattleMap()).kind(), is(Tactics.Kind.SKIP));
    }

    private static Unit.Stack stack(int attack, int defense, int health, int speed) {
        Unit.Stack stack = new Unit.Stack(Unit.Type.WALKER);
        stack.add(new Unit(attack, defense, health, speed));
        return stack;
    }
}
