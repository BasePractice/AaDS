package ru.mifi.practice.voln.heroes;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

/** Проверка локальной партии: решение игрока и ответный ход программы. */
@DisplayName("Локальная партия")
final class BattleTest {

    @DisplayName("Пока ходят левые, ход наш")
    @Test
    @Timeout(1)
    void countsTheLeftTurnAsOurs() {
        BattleMap map = new BattleMap();
        map.addLeft(5, 5, stack(100, 0, 100, 5));
        assertThat("the left turn dont count as ours",
            new Battle.Local(map, new Tactics.Greedy()).ours(), is(true));
    }

    @DisplayName("Пока ходят правые, ход не наш")
    @Test
    @Timeout(1)
    void countsTheRightTurnAsTheirs() {
        BattleMap map = new BattleMap();
        map.addLeft(5, 5, stack(100, 0, 100, 5));
        map.addRight(5, 9, stack(100, 0, 100, 5));
        map.skipTurn();
        assertThat("the right turn still counts as ours",
            new Battle.Local(map, new Tactics.Greedy()).ours(), is(false));
    }

    @DisplayName("Решение игрока переносит отряд на поле")
    @Test
    @Timeout(1)
    void movesTheStackOnTheDecision() {
        BattleMap map = new BattleMap();
        map.addLeft(5, 5, stack(100, 0, 100, 5));
        new Battle.Local(map, new Tactics.Greedy()).apply(new Tactics.Decision(Tactics.Kind.MOVE, 5, 7));
        assertThat("the player decision dont move the stack", map.getStack(5, 7), is(notNullValue()));
    }

    @DisplayName("Пропуск игрока передаёт ход правым")
    @Test
    @Timeout(1)
    void passesTheTurnOnTheSkipDecision() {
        BattleMap map = new BattleMap();
        map.addLeft(5, 5, stack(100, 0, 100, 5));
        map.addRight(5, 9, stack(100, 0, 100, 5));
        new Battle.Local(map, new Tactics.Greedy()).apply(new Tactics.Decision(Tactics.Kind.SKIP, -1, -1));
        assertThat("the player skip dont pass the turn", map.isLeftTurn(), is(false));
    }

    /**
     * Ожидание меняет очередь ходов, поэтому идёт тем же путём, что удар и перемещение: иначе
     * в сетевой партии очередь у сторон разошлась бы после первого же нажатия.
     */
    @DisplayName("Ожидание игрока откладывает отряд в конец очереди")
    @Test
    @Timeout(1)
    void postponesTheStackOnTheWaitDecision() {
        BattleMap map = new BattleMap();
        Unit.Stack first = stack(100, 0, 100, 5);
        map.addLeft(5, 5, first);
        map.addLeft(6, 5, stack(100, 0, 100, 5));
        new Battle.Local(map, new Tactics.Greedy()).apply(new Tactics.Decision(Tactics.Kind.WAIT, -1, -1));
        assertThat("the wait decision dont postpone the stack",
            map.getStackById(map.getTurnQueue().peekFirst()), is(not(first)));
    }

    @DisplayName("Ответ программы приходит только на её ходу")
    @Test
    @Timeout(1)
    void keepsSilentOnOurTurn() {
        BattleMap map = new BattleMap();
        map.addLeft(5, 5, stack(100, 0, 100, 5));
        map.addRight(5, 9, stack(100, 0, 100, 5));
        new Battle.Local(map, new Tactics.Greedy()).answer();
        assertThat("the machine acts on our turn", map.isAnimating(), is(false));
    }

    @DisplayName("На своём ходу программа сближается с противником")
    @Test
    @Timeout(1)
    void approachesOnItsOwnTurn() {
        BattleMap map = new BattleMap();
        map.addLeft(5, 5, stack(100, 0, 100, 5));
        map.addRight(5, 14, stack(100, 0, 100, 5));
        map.skipTurn();
        new Battle.Local(map, new Tactics.Greedy()).answer();
        assertThat("the machine stands still on its own turn", map.isAnimating(), is(true));
    }

    private static Unit.Stack stack(int attack, int defense, int health, int speed) {
        Unit.Stack stack = new Unit.Stack(Unit.Type.WALKER);
        stack.add(new Unit(attack, defense, health, speed));
        return stack;
    }
}
