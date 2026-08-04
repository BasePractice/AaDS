package ru.mifi.practice.voln.heroes;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

@DisplayName("Карта боя")
final class BattleMapTest {

    @DisplayName("Атака уводит атакующего со стартовой клетки")
    @Test
    @Timeout(1)
    void movesAwayFromTheStartCellOnAttack() {
        BattleMap map = new BattleMap();
        Unit.Stack attacker = new Unit.Stack(Unit.Type.WALKER);
        attacker.add(new Unit(10, 5, 100, 5));
        Unit.Stack target = new Unit.Stack(Unit.Type.WALKER);
        target.add(new Unit(10, 5, 100, 5));
        map.addLeft(0, 0, attacker);
        map.addRight(0, 2, target);
        map.attack(0, 0, 0, 2);
        assertThat("attacker doesnt leave the start cell after attacking", map.getStack(0, 0), is(nullValue()));
    }

    @DisplayName("Атака приводит атакующего на соседнюю клетку")
    @Test
    @Timeout(1)
    void reachesTheNextCellOnAttack() {
        BattleMap map = new BattleMap();
        Unit.Stack attacker = new Unit.Stack(Unit.Type.WALKER);
        attacker.add(new Unit(10, 5, 100, 5));
        Unit.Stack target = new Unit.Stack(Unit.Type.WALKER);
        target.add(new Unit(10, 5, 100, 5));
        map.addLeft(0, 0, attacker);
        map.addRight(0, 2, target);
        map.attack(0, 0, 0, 2);
        assertThat("attacker doesnt reach the next cell after attacking", map.getStack(0, 1), is(notNullValue()));
    }

    @DisplayName("На соседней клетке оказывается именно атакующий")
    @Test
    @Timeout(1)
    void putsTheAttackerOnTheNextCell() {
        BattleMap map = new BattleMap();
        Unit.Stack attacker = new Unit.Stack(Unit.Type.WALKER);
        attacker.add(new Unit(10, 5, 100, 5));
        Unit.Stack target = new Unit.Stack(Unit.Type.WALKER);
        target.add(new Unit(10, 5, 100, 5));
        map.addLeft(0, 0, attacker);
        map.addRight(0, 2, target);
        map.attack(0, 0, 0, 2);
        assertThat("the next cell doesnt hold the attacker after attacking", map.getStack(0, 1), is(attacker));
    }

    @DisplayName("Ход на занятую клетку оставляет ходока на месте")
    @Test
    @Timeout(1)
    void keepsTheWalkerWhenTargetCellIsOccupied() {
        BattleMap map = new BattleMap();
        Unit.Stack walker = new Unit.Stack(Unit.Type.WALKER);
        walker.add(new Unit(10, 5, 100, 5));
        Unit.Stack target = new Unit.Stack(Unit.Type.WALKER);
        target.add(new Unit(10, 5, 100, 5));
        map.addLeft(0, 0, walker);
        map.addRight(0, 1, target);
        map.move(0, 0, 0, 1);
        assertThat("walker doesnt stay when the target cell is occupied", map.getStack(0, 0), is(notNullValue()));
    }

    @DisplayName("Ход на занятую клетку не сдвигает цель")
    @Test
    @Timeout(1)
    void keepsTheTargetWhenItsCellIsOccupied() {
        BattleMap map = new BattleMap();
        Unit.Stack walker = new Unit.Stack(Unit.Type.WALKER);
        walker.add(new Unit(10, 5, 100, 5));
        Unit.Stack target = new Unit.Stack(Unit.Type.WALKER);
        target.add(new Unit(10, 5, 100, 5));
        map.addLeft(0, 0, walker);
        map.addRight(0, 1, target);
        map.move(0, 0, 0, 1);
        assertThat("target doesnt stay on its occupied cell", map.getStack(0, 1), is(notNullValue()));
    }

    @DisplayName("Цель на занятой клетке не перезаписывается")
    @Test
    @Timeout(1)
    void doesntOverwriteTheOccupiedTarget() {
        BattleMap map = new BattleMap();
        Unit.Stack walker = new Unit.Stack(Unit.Type.WALKER);
        walker.add(new Unit(10, 5, 100, 5));
        Unit.Stack target = new Unit.Stack(Unit.Type.WALKER);
        target.add(new Unit(10, 5, 100, 5));
        map.addLeft(0, 0, walker);
        map.addRight(0, 1, target);
        map.move(0, 0, 0, 1);
        assertThat("the occupied target gets overwritten by the walker", map.getStack(0, 1), is(target));
    }

    /**
     * На этом держится сетевой бой: обе стороны собирают поле у себя, обмениваясь одним зерном,
     * а не пересылая расстановку целиком.
     */
    @DisplayName("Одно зерно даёт одинаковую расстановку")
    @Test
    @Timeout(1)
    void repeatsTheFieldForTheSameSeed() {
        BattleMap one = new BattleMap();
        one.fillRandomly(20260804L);
        BattleMap other = new BattleMap();
        other.fillRandomly(20260804L);
        assertThat("the same seed lays out different fields", picture(one), is(picture(other)));
    }

    @DisplayName("Разные зёрна дают разную расстановку")
    @Test
    @Timeout(1)
    void variesTheFieldForDifferentSeeds() {
        BattleMap one = new BattleMap();
        one.fillRandomly(1L);
        BattleMap other = new BattleMap();
        other.fillRandomly(2L);
        assertThat("different seeds lay out the same field", picture(one), is(not(picture(other))));
    }

    private static String picture(BattleMap map) {
        StringBuilder drawing = new StringBuilder();
        for (int row = 0; row < Constants.ROWS; row++) {
            for (int column = 0; column < Constants.COLS; column++) {
                Unit.Stack stack = map.getStack(row, column);
                if (map.isObstacle(row, column)) {
                    drawing.append('#');
                } else if (stack == null) {
                    drawing.append('.');
                } else {
                    drawing.append(map.isLeft(row, column) ? 'L' : 'R').append(stack.size());
                }
            }
        }
        return drawing.toString();
    }
}
