package ru.mifi.practice.voln.heroes;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Очередь ходов")
final class TurnQueueTest {

    @DisplayName("Очередь держит оба стека после наполнения")
    @Test
    @Timeout(1)
    void queueHoldsBothStacksAfterFill() {
        BattleMap map = new BattleMap();
        Unit.Stack s1 = new Unit.Stack(Unit.Type.WALKER);
        s1.add(new Unit(10, 5, 100, 5));
        Unit.Stack s2 = new Unit.Stack(Unit.Type.WALKER);
        s2.add(new Unit(10, 5, 100, 5));
        map.addLeft(0, 0, s1);
        map.addLeft(1, 0, s2);
        map.fillTurnQueue();
        assertThat("the filled queue doesnt hold both stacks", map.getTurnQueue().size(), is(2));
    }

    @DisplayName("Первый стек стоит в голове очереди")
    @Test
    @Timeout(1)
    void firstStackLeadsTheQueue() {
        BattleMap map = new BattleMap();
        Unit.Stack s1 = new Unit.Stack(Unit.Type.WALKER);
        s1.add(new Unit(10, 5, 100, 5));
        Unit.Stack s2 = new Unit.Stack(Unit.Type.WALKER);
        s2.add(new Unit(10, 5, 100, 5));
        map.addLeft(0, 0, s1);
        map.addLeft(1, 0, s2);
        map.fillTurnQueue();
        assertThat("the first stack doesnt lead the queue", map.getStackById(map.getTurnQueue().peekFirst()), is(s1));
    }

    @DisplayName("Ход не меняет размер очереди во время анимации")
    @Test
    @Timeout(1)
    void keepsQueueSizeDuringAnimation() {
        BattleMap map = new BattleMap();
        Unit.Stack s1 = new Unit.Stack(Unit.Type.WALKER);
        s1.add(new Unit(10, 5, 100, 5));
        Unit.Stack s2 = new Unit.Stack(Unit.Type.WALKER);
        s2.add(new Unit(10, 5, 100, 5));
        map.addLeft(0, 0, s1);
        map.addLeft(1, 0, s2);
        map.fillTurnQueue();
        map.move(0, 0, 0, 1);
        assertThat("a move during animation changes the queue size", map.getTurnQueue().size(), is(2));
    }

    @DisplayName("Ходящий стек остаётся в голове очереди во время анимации")
    @Test
    @Timeout(1)
    void keepsTheMoverAtTheHeadDuringAnimation() {
        BattleMap map = new BattleMap();
        Unit.Stack s1 = new Unit.Stack(Unit.Type.WALKER);
        s1.add(new Unit(10, 5, 100, 5));
        Unit.Stack s2 = new Unit.Stack(Unit.Type.WALKER);
        s2.add(new Unit(10, 5, 100, 5));
        map.addLeft(0, 0, s1);
        map.addLeft(1, 0, s2);
        map.fillTurnQueue();
        Long id1 = map.getTurnQueue().peekFirst();
        map.move(0, 0, 0, 1);
        assertThat("the mover doesnt stay at the head during animation", map.getTurnQueue().peekFirst(), is(id1));
    }

    @DisplayName("Ход запускает анимацию")
    @Test
    @Timeout(1)
    void animatesAfterAMove() {
        BattleMap map = new BattleMap();
        Unit.Stack s1 = new Unit.Stack(Unit.Type.WALKER);
        s1.add(new Unit(10, 5, 100, 5));
        Unit.Stack s2 = new Unit.Stack(Unit.Type.WALKER);
        s2.add(new Unit(10, 5, 100, 5));
        map.addLeft(0, 0, s1);
        map.addLeft(1, 0, s2);
        map.fillTurnQueue();
        map.move(0, 0, 0, 1);
        assertThat("a move doesnt start the animation", map.isAnimating(), is(true));
    }

    @DisplayName("Во время анимации ходящий стек ещё не считается сходившим")
    @Test
    @Timeout(1)
    void doesntMarkTheMoverActedDuringAnimation() {
        BattleMap map = new BattleMap();
        Unit.Stack s1 = new Unit.Stack(Unit.Type.WALKER);
        s1.add(new Unit(10, 5, 100, 5));
        Unit.Stack s2 = new Unit.Stack(Unit.Type.WALKER);
        s2.add(new Unit(10, 5, 100, 5));
        map.addLeft(0, 0, s1);
        map.addLeft(1, 0, s2);
        map.fillTurnQueue();
        map.move(0, 0, 0, 1);
        assertThat("the mover gets marked acted during animation", s1.hasActed(), is(false));
    }

    @DisplayName("Конец действия останавливает анимацию")
    @Test
    @Timeout(1)
    void stopsAnimatingAfterEndAction() {
        BattleMap map = new BattleMap();
        Unit.Stack s1 = new Unit.Stack(Unit.Type.WALKER);
        s1.add(new Unit(10, 5, 100, 5));
        Unit.Stack s2 = new Unit.Stack(Unit.Type.WALKER);
        s2.add(new Unit(10, 5, 100, 5));
        map.addLeft(0, 0, s1);
        map.addLeft(1, 0, s2);
        map.fillTurnQueue();
        map.move(0, 0, 0, 1);
        map.endAction();
        assertThat("ending the action doesnt stop the animation", map.isAnimating(), is(false));
    }

    @DisplayName("Конец действия помечает ходящий стек сходившим")
    @Test
    @Timeout(1)
    void marksTheMoverActedAfterEndAction() {
        BattleMap map = new BattleMap();
        Unit.Stack s1 = new Unit.Stack(Unit.Type.WALKER);
        s1.add(new Unit(10, 5, 100, 5));
        Unit.Stack s2 = new Unit.Stack(Unit.Type.WALKER);
        s2.add(new Unit(10, 5, 100, 5));
        map.addLeft(0, 0, s1);
        map.addLeft(1, 0, s2);
        map.fillTurnQueue();
        map.move(0, 0, 0, 1);
        map.endAction();
        assertThat("ending the action doesnt mark the mover acted", s1.hasActed(), is(true));
    }

    @DisplayName("Конец действия убирает сходивший стек из очереди")
    @Test
    @Timeout(1)
    void shrinksTheQueueAfterEndAction() {
        BattleMap map = new BattleMap();
        Unit.Stack s1 = new Unit.Stack(Unit.Type.WALKER);
        s1.add(new Unit(10, 5, 100, 5));
        Unit.Stack s2 = new Unit.Stack(Unit.Type.WALKER);
        s2.add(new Unit(10, 5, 100, 5));
        map.addLeft(0, 0, s1);
        map.addLeft(1, 0, s2);
        map.fillTurnQueue();
        map.move(0, 0, 0, 1);
        map.endAction();
        assertThat("ending the action doesnt shrink the queue", map.getTurnQueue().size(), is(1));
    }

    @DisplayName("Конец действия выводит второй стек в голову очереди")
    @Test
    @Timeout(1)
    void promotesTheSecondStackAfterEndAction() {
        BattleMap map = new BattleMap();
        Unit.Stack s1 = new Unit.Stack(Unit.Type.WALKER);
        s1.add(new Unit(10, 5, 100, 5));
        Unit.Stack s2 = new Unit.Stack(Unit.Type.WALKER);
        s2.add(new Unit(10, 5, 100, 5));
        map.addLeft(0, 0, s1);
        map.addLeft(1, 0, s2);
        map.fillTurnQueue();
        map.move(0, 0, 0, 1);
        map.endAction();
        assertThat("the second stack doesnt reach the head of the queue",
            map.getStackById(map.getTurnQueue().peekFirst()), is(s2));
    }

    @DisplayName("Атака анимирует движение перед ударом")
    @Test
    @Timeout(1)
    void animatesTheMoveBeforeTheAttack() {
        Unit.Stack s1 = new Unit.Stack(Unit.Type.WALKER);
        s1.add(new Unit(100, 5, 100, 5));
        Unit.Stack s3 = new Unit.Stack(Unit.Type.WALKER);
        s3.add(new Unit(10, 5, 100, 5));
        Unit.Stack s2 = new Unit.Stack(Unit.Type.WALKER);
        s2.add(new Unit(10, 5, 50, 5));
        BattleMap map = new BattleMap();
        map.addLeft(0, 0, s1);
        map.addLeft(1, 0, s3);
        map.addRight(0, 2, s2);
        map.fillTurnQueue();
        map.attack(0, 0, 0, 2);
        assertThat("the attack doesnt animate the move before striking", map.isAnimating(), is(true));
    }

    @DisplayName("Цель не получает урон во время анимации атаки")
    @Test
    @Timeout(1)
    void keepsTheTargetUnhurtDuringAnimation() {
        Unit.Stack s1 = new Unit.Stack(Unit.Type.WALKER);
        s1.add(new Unit(100, 5, 100, 5));
        Unit.Stack s3 = new Unit.Stack(Unit.Type.WALKER);
        s3.add(new Unit(10, 5, 100, 5));
        Unit.Stack s2 = new Unit.Stack(Unit.Type.WALKER);
        s2.add(new Unit(10, 5, 50, 5));
        BattleMap map = new BattleMap();
        map.addLeft(0, 0, s1);
        map.addLeft(1, 0, s3);
        map.addRight(0, 2, s2);
        map.fillTurnQueue();
        map.attack(0, 0, 0, 2);
        assertThat("the target takes damage before the animation ends", s2.size(), is(1));
    }

    @DisplayName("Конец действия останавливает анимацию атаки")
    @Test
    @Timeout(1)
    void stopsAnimatingAfterTheAttackEnds() {
        Unit.Stack s1 = new Unit.Stack(Unit.Type.WALKER);
        s1.add(new Unit(100, 5, 100, 5));
        Unit.Stack s3 = new Unit.Stack(Unit.Type.WALKER);
        s3.add(new Unit(10, 5, 100, 5));
        Unit.Stack s2 = new Unit.Stack(Unit.Type.WALKER);
        s2.add(new Unit(10, 5, 50, 5));
        BattleMap map = new BattleMap();
        map.addLeft(0, 0, s1);
        map.addLeft(1, 0, s3);
        map.addRight(0, 2, s2);
        map.fillTurnQueue();
        map.attack(0, 0, 0, 2);
        map.endAction();
        assertThat("the attack keeps animating after the action ends", map.isAnimating(), is(false));
    }

    @DisplayName("Конец действия наносит урон цели")
    @Test
    @Timeout(1)
    void damagesTheTargetAfterTheAttackEnds() {
        Unit.Stack s1 = new Unit.Stack(Unit.Type.WALKER);
        s1.add(new Unit(100, 5, 100, 5));
        Unit.Stack s3 = new Unit.Stack(Unit.Type.WALKER);
        s3.add(new Unit(10, 5, 100, 5));
        Unit.Stack s2 = new Unit.Stack(Unit.Type.WALKER);
        s2.add(new Unit(10, 5, 50, 5));
        BattleMap map = new BattleMap();
        map.addLeft(0, 0, s1);
        map.addLeft(1, 0, s3);
        map.addRight(0, 2, s2);
        map.fillTurnQueue();
        map.attack(0, 0, 0, 2);
        map.endAction();
        assertThat("the target survives after the attack animation ends", s2.size(), is(0));
    }

    @DisplayName("Атака помечает нападающего сходившим")
    @Test
    @Timeout(1)
    void marksTheAttackerActedAfterTheAttack() {
        Unit.Stack s1 = new Unit.Stack(Unit.Type.WALKER);
        s1.add(new Unit(100, 5, 100, 5));
        Unit.Stack s3 = new Unit.Stack(Unit.Type.WALKER);
        s3.add(new Unit(10, 5, 100, 5));
        Unit.Stack s2 = new Unit.Stack(Unit.Type.WALKER);
        s2.add(new Unit(10, 5, 50, 5));
        BattleMap map = new BattleMap();
        map.addLeft(0, 0, s1);
        map.addLeft(1, 0, s3);
        map.addRight(0, 2, s2);
        map.fillTurnQueue();
        map.attack(0, 0, 0, 2);
        map.endAction();
        assertThat("the attacker doesnt get marked acted", s1.hasActed(), is(true));
    }

    @DisplayName("После атаки в очереди остаётся один стек")
    @Test
    @Timeout(1)
    void leavesOneUnitInTheQueueAfterTheAttack() {
        Unit.Stack s1 = new Unit.Stack(Unit.Type.WALKER);
        s1.add(new Unit(100, 5, 100, 5));
        Unit.Stack s3 = new Unit.Stack(Unit.Type.WALKER);
        s3.add(new Unit(10, 5, 100, 5));
        Unit.Stack s2 = new Unit.Stack(Unit.Type.WALKER);
        s2.add(new Unit(10, 5, 50, 5));
        Unit.Stack s4 = new Unit.Stack(Unit.Type.WALKER);
        s4.add(new Unit(10, 5, 100, 5));
        BattleMap map = new BattleMap();
        map.addLeft(0, 0, s1);
        map.addLeft(1, 0, s3);
        map.addRight(0, 2, s2);
        map.addRight(10, 14, s4);
        map.fillTurnQueue();
        map.attack(0, 0, 0, 2);
        map.endAction();
        assertThat("more than one unit stays in the queue after the attack", map.getTurnQueue().size(), is(1));
    }
}
