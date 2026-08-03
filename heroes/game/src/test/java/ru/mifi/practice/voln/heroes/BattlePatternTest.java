package ru.mifi.practice.voln.heroes;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.nullValue;

@DisplayName("Поле боя")
final class BattlePatternTest {

    @DisplayName("Стек знает своё количество")
    @Test
    void countsItsUnits() {
        assertThat("stack dont count its units", walkers().size(), is(2));
    }

    @DisplayName("Наибольшая атака стека — сумма атак юнитов")
    @Test
    void sumsUnitAttacksIntoTheMaximum() {
        assertThat("maximum attack dont sum the unit attacks", walkers().maximumAttack(), is(150));
    }

    /**
     * Атака случайна в пределах разброса, детерминированную сумму даёт maximumAttack. Прежний
     * тест сравнивал сумму с результатом attack и падал на любом броске, кроме верхнего.
     */
    @DisplayName("Атака не выходит за разброс от наибольшей")
    @Test
    void keepsTheAttackWithinTheSpread() {
        assertThat("attack escapes the spread below the maximum",
            walkers().attack(), allOf(greaterThanOrEqualTo(130), lessThan(150)));
    }

    @DisplayName("Здоровье стека — сумма здоровья юнитов")
    @Test
    void sumsUnitHealth() {
        assertThat("stack dont sum the unit health", walkers().totalHealth(), is(150));
    }

    @DisplayName("Скорость стека — скорость самого медленного юнита")
    @Test
    void takesTheSpeedOfTheSlowestUnit() {
        assertThat("stack dont move at the speed of its slowest unit", walkers().speed(), is(3));
    }

    @DisplayName("Расстановка левого стека уведомляет наблюдателя")
    @Test
    void notifiesTheObserverOnAddingLeft() {
        BattleMap map = new BattleMap();
        AtomicBoolean fired = new AtomicBoolean(false);
        map.addPropertyChangeListener(e -> fired.set(true));
        map.addLeft(0, 0, stack(10, 10, 100, 5));
        assertThat("adding a left stack dont notify the observer", fired.get(), is(true));
    }

    @DisplayName("Расстановка правого стека уведомляет наблюдателя")
    @Test
    void notifiesTheObserverOnAddingRight() {
        BattleMap map = new BattleMap();
        AtomicBoolean fired = new AtomicBoolean(false);
        map.addPropertyChangeListener(e -> fired.set(true));
        map.addRight(0, 14, stack(10, 10, 100, 5));
        assertThat("adding a right stack dont notify the observer", fired.get(), is(true));
    }

    @DisplayName("Случайная расстановка ставит стеки на поле")
    @Test
    void placesStacksOnRandomFill() {
        BattleMap map = new BattleMap();
        map.fillRandomly();
        boolean placed = false;
        for (int r = 0; r < Constants.ROWS && !placed; r++) {
            for (int c = 0; c < Constants.COLS && !placed; c++) {
                placed = map.getStack(r, c) != null;
            }
        }
        assertThat("random fill leaves the field empty", placed, is(true));
    }

    @DisplayName("Препятствия ставятся только в середине поля")
    @Test
    void keepsObstaclesInTheMiddleColumns() {
        BattleMap map = new BattleMap();
        map.fillRandomly();
        boolean outside = false;
        for (int r = 0; r < Constants.ROWS; r++) {
            for (int c = 0; c < Constants.COLS; c++) {
                if (map.isObstacle(r, c) && (c < 5 || c > 9)) {
                    outside = true;
                }
            }
        }
        assertThat("an obstacle lands outside the middle columns", outside, is(false));
    }

    @DisplayName("Число препятствий держится в заданных пределах")
    @Test
    void placesTheAgreedNumberOfObstacles() {
        BattleMap map = new BattleMap();
        map.fillRandomly();
        int obstacles = 0;
        for (int r = 0; r < Constants.ROWS; r++) {
            for (int c = 0; c < Constants.COLS; c++) {
                if (map.isObstacle(r, c)) {
                    ++obstacles;
                }
            }
        }
        assertThat("the number of obstacles escapes its range", obstacles,
            allOf(greaterThanOrEqualTo(5), lessThan(10)));
    }

    @DisplayName("Расстояние до самой клетки равно нулю")
    @Test
    void measuresZeroDistanceToTheStart() {
        assertThat("the start cell is not at distance zero", distances()[5][5], is(0));
    }

    @DisplayName("Соседняя по стороне клетка стоит один шаг")
    @Test
    void measuresOneStepToASideNeighbour() {
        assertThat("a side neighbour dont cost one step", distances()[5][4], is(1));
    }

    /**
     * Ходы четырёхнаправленные — и в getDistances, и в getPath, и в обходе соседей при атаке.
     * Прежний тест ожидал единицу до клетки по диагонали, то есть другую модель движения.
     */
    @DisplayName("Клетка по диагонали стоит два шага")
    @Test
    void measuresTwoStepsToADiagonalNeighbour() {
        assertThat("movement counts a diagonal as a single step", distances()[6][6], is(2));
    }

    @DisplayName("Расстояние растёт по манхэттенской метрике")
    @Test
    void measuresDistanceAlongTheGrid() {
        assertThat("distance dont follow the grid metric", distances()[8][8], is(6));
    }

    @DisplayName("Бой начинается с хода левых")
    @Test
    void startsTheBattleWithTheLeftSide() {
        assertThat("the battle dont start with the left side", twoSided().isLeftTurn(), is(true));
    }

    /**
     * Очередь ходов наполнялась только в fillRandomly, поэтому на карте, собранной вручную,
     * skipTurn выходил сразу и ход не передавался. Теперь очередь обновляется при расстановке.
     */
    @DisplayName("Пропуск хода передаёт очередь правым")
    @Test
    void passesTheTurnToTheRightSide() {
        BattleMap map = twoSided();
        map.skipTurn();
        assertThat("skipping dont pass the turn to the other side", map.isLeftTurn(), is(false));
    }

    @DisplayName("Второй пропуск возвращает ход левым")
    @Test
    void returnsTheTurnToTheLeftSide() {
        BattleMap map = twoSided();
        map.skipTurn();
        map.skipTurn();
        assertThat("the turn dont come back to the left side", map.isLeftTurn(), is(true));
    }

    @DisplayName("Атака уменьшает здоровье цели")
    @Test
    void hurtsTheTarget() {
        BattleMap map = new BattleMap();
        map.addLeft(5, 5, stack(100, 0, 100, 5));
        Unit.Stack target = stack(100, 0, 100, 5);
        map.addRight(5, 6, target);
        map.attack(5, 5, 5, 6);
        assertThat("the attack leaves the target untouched", target.totalHealth(), lessThan(100));
    }

    /**
     * Урон случаен в пределах разброса, поэтому цель со здоровьем в сотню переживает удар силой
     * от восьмидесяти до девяноста девяти. Прежний тест ждал гибели именно такой цели.
     */
    @DisplayName("Слабая цель погибает от удара")
    @Test
    void destroysAWeakTarget() {
        BattleMap map = new BattleMap();
        map.addLeft(5, 5, stack(100, 0, 100, 5));
        Unit.Stack target = stack(100, 0, 50, 5);
        map.addRight(5, 6, target);
        map.attack(5, 5, 5, 6);
        assertThat("a target weaker than the weakest possible hit survives", target.size(), is(0));
    }

    @DisplayName("Погибшая цель снимается с поля")
    @Test
    void clearsTheCellOfADestroyedTarget() {
        BattleMap map = new BattleMap();
        map.addLeft(5, 5, stack(100, 0, 100, 5));
        map.addRight(5, 6, stack(100, 0, 50, 5));
        map.attack(5, 5, 5, 6);
        assertThat("a destroyed stack stays on the field", map.getStack(5, 6), is(nullValue()));
    }

    @DisplayName("Атака попадает в журнал боя")
    @Test
    void logsTheAttack() {
        assertThat("the attack dont reach the battle log", afterAttack(), hasItem(containsString("бьет")));
    }

    @DisplayName("Смена хода попадает в журнал боя")
    @Test
    void logsTheTurnChange() {
        assertThat("the turn change dont reach the battle log", afterAttack(), hasItem(containsString("--- Ход")));
    }

    @DisplayName("Перемещение попадает в журнал боя")
    @Test
    void logsTheMove() {
        BattleMap map = new BattleMap();
        final List<String> logs = logsOf(map);
        map.addLeft(5, 5, stack(100, 0, 100, 5));
        map.addRight(5, 6, stack(100, 0, 100, 5));
        map.addRight(0, 14, stack(100, 0, 100, 5));
        map.attack(5, 5, 5, 6);
        map.move(0, 14, 0, 13);
        assertThat("the move dont reach the battle log", logs, hasItem(containsString("ходит (0, 13)")));
    }

    /**
     * Сообщение «пропустил ход» пишет skipTurn. Метод waitTurn откладывает ход в конец очереди
     * и ничего не пишет — прежний тест ждал этой строки именно от него.
     */
    @DisplayName("Пропуск хода попадает в журнал боя")
    @Test
    void logsTheSkippedTurn() {
        BattleMap map = new BattleMap();
        final List<String> logs = logsOf(map);
        map.addLeft(5, 5, stack(100, 0, 100, 5));
        map.skipTurn();
        assertThat("the skipped turn dont reach the battle log", logs, hasItem(containsString("пропустил ход")));
    }

    private static List<String> afterAttack() {
        BattleMap map = new BattleMap();
        final List<String> logs = logsOf(map);
        map.addLeft(5, 5, stack(100, 0, 100, 5));
        map.addRight(5, 6, stack(100, 0, 100, 5));
        map.attack(5, 5, 5, 6);
        return logs;
    }

    private static List<String> logsOf(BattleMap map) {
        List<String> logs = new ArrayList<>();
        map.addPropertyChangeListener(e -> {
            if ("log".equals(e.getPropertyName())) {
                logs.add((String) e.getNewValue());
            }
        });
        return logs;
    }

    private static int[][] distances() {
        BattleMap map = new BattleMap();
        map.addLeft(5, 5, stack(10, 10, 100, 2));
        return map.getDistances(5, 5, false);
    }

    private static BattleMap twoSided() {
        BattleMap map = new BattleMap();
        map.addLeft(0, 0, stack(10, 10, 100, 5));
        map.addRight(0, 14, stack(10, 10, 100, 5));
        return map;
    }

    private static Unit.Stack walkers() {
        Unit.Stack stack = new Unit.Stack(Unit.Type.WALKER);
        stack.add(new Unit(100, 10, 100, 5));
        stack.add(new Unit(100, 10, 50, 3));
        return stack;
    }

    private static Unit.Stack stack(int attack, int defense, int health, int speed) {
        Unit.Stack stack = new Unit.Stack(Unit.Type.WALKER);
        stack.add(new Unit(attack, defense, health, speed));
        return stack;
    }
}
