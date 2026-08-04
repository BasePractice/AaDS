package ru.mifi.practice.voln.heroes;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

/** Проверка исхода боя: побеждает сторона, у которой на поле остались отряды. */
@DisplayName("Исход боя")
final class OutcomeTest {

    @DisplayName("Пустое поле никому не отдаёт победу")
    @Test
    @Timeout(1)
    void leavesTheEmptyFieldWithoutAWinner() {
        assertThat("an empty field hands somebody the victory",
            new BattleMap().outcome(), is(BattleMap.Outcome.NONE));
    }

    @DisplayName("Одна сторона на поле — бой ещё не начат")
    @Test
    @Timeout(1)
    void waitsForTheSecondSideToTakeTheField() {
        BattleMap map = new BattleMap();
        map.addLeft(5, 5, stack(100, 0, 100, 5));
        assertThat("a battle with nobody to fight counts as won",
            map.outcome(), is(BattleMap.Outcome.NONE));
    }

    @DisplayName("Пока обе стороны на поле, победителя нет")
    @Test
    @Timeout(1)
    void keepsTheBattleRunningWhileBothSidesStand() {
        BattleMap map = new BattleMap();
        map.addLeft(5, 5, stack(100, 0, 100, 5));
        map.addRight(5, 6, stack(100, 0, 100, 5));
        assertThat("the battle ends while both sides still stand",
            map.outcome(), is(BattleMap.Outcome.NONE));
    }

    @DisplayName("Побеждают левые, когда правых не осталось")
    @Test
    @Timeout(1)
    void givesTheVictoryToTheSurvivingLeftSide() {
        BattleMap map = new BattleMap();
        map.addLeft(5, 5, stack(100, 0, 100, 5));
        map.addRight(5, 6, stack(10, 0, 20, 5));
        map.attack(5, 5, 5, 6);
        assertThat("the last side standing dont win", map.outcome(), is(BattleMap.Outcome.LEFT));
    }

    @DisplayName("Побеждают правые, когда левых не осталось")
    @Test
    @Timeout(1)
    void givesTheVictoryToTheSurvivingRightSide() {
        BattleMap map = new BattleMap();
        map.addLeft(5, 5, stack(10, 0, 20, 5));
        map.addRight(5, 6, stack(100, 0, 100, 5));
        map.skipTurn();
        map.attack(5, 6, 5, 5);
        assertThat("the right side dont win with the left side wiped out",
            map.outcome(), is(BattleMap.Outcome.RIGHT));
    }

    @DisplayName("Победа попадает в журнал боя")
    @Test
    @Timeout(1)
    void logsTheVictory() {
        BattleMap map = new BattleMap();
        List<String> logs = new ArrayList<>();
        map.addPropertyChangeListener(e -> {
            if ("log".equals(e.getPropertyName())) {
                logs.add((String) e.getNewValue());
            }
        });
        map.addLeft(5, 5, stack(100, 0, 100, 5));
        map.addRight(5, 6, stack(10, 0, 20, 5));
        map.attack(5, 5, 5, 6);
        assertThat("the victory dont reach the battle log", logs, hasItem(containsString("Бой окончен")));
    }

    @DisplayName("Оконченный бой не пускает новый ход")
    @Test
    @Timeout(1)
    void refusesToMoveAfterTheVictory() {
        BattleMap map = new BattleMap();
        map.addLeft(5, 5, stack(100, 0, 100, 5));
        map.addLeft(0, 0, stack(100, 0, 100, 5));
        map.addRight(5, 6, stack(10, 0, 20, 5));
        map.attack(5, 5, 5, 6);
        map.move(0, 0, 0, 1);
        assertThat("a stack walks on when the battle is already over",
            map.getStack(0, 0), is(notNullValue()));
    }

    private static Unit.Stack stack(int attack, int defense, int health, int speed) {
        Unit.Stack stack = new Unit.Stack(Unit.Type.WALKER);
        stack.add(new Unit(attack, defense, health, speed));
        return stack;
    }
}
