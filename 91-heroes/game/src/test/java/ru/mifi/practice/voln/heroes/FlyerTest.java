package ru.mifi.practice.voln.heroes;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@DisplayName("Летающий юнит")
final class FlyerTest {

    @DisplayName("Летун находит путь в обход занятой клетки")
    @Test
    @Timeout(1)
    void findsAPathAroundTheOccupiedCell() {
        BattleMap map = new BattleMap();
        Unit.Stack flyer = new Unit.Stack(Unit.Type.FLYER);
        flyer.add(new Unit(10, 5, 100, 5));
        Unit.Stack obstacleUnit = new Unit.Stack(Unit.Type.WALKER);
        obstacleUnit.add(new Unit(10, 5, 100, 5));
        map.addLeft(0, 0, flyer);
        map.addRight(0, 1, obstacleUnit);
        List<int[]> path = map.getPath(0, 0, 0, 2, true);
        assertThat("flyer cannot find a path around another unit", path, is(not(empty())));
    }

    @DisplayName("Обходной путь длиннее прямого")
    @Test
    @Timeout(1)
    void takesALongerPathAroundTheUnit() {
        BattleMap map = new BattleMap();
        Unit.Stack flyer = new Unit.Stack(Unit.Type.FLYER);
        flyer.add(new Unit(10, 5, 100, 5));
        Unit.Stack obstacleUnit = new Unit.Stack(Unit.Type.WALKER);
        obstacleUnit.add(new Unit(10, 5, 100, 5));
        map.addLeft(0, 0, flyer);
        map.addRight(0, 1, obstacleUnit);
        List<int[]> path = map.getPath(0, 0, 0, 2, true);
        assertThat("the path around the unit is not longer than the direct one", path.size(), is(greaterThan(3)));
    }

    @DisplayName("Путь летуна обходит занятую клетку")
    @Test
    @Timeout(1)
    void avoidsTheOccupiedCellOnTheWay() {
        BattleMap map = new BattleMap();
        Unit.Stack flyer = new Unit.Stack(Unit.Type.FLYER);
        flyer.add(new Unit(10, 5, 100, 5));
        Unit.Stack obstacleUnit = new Unit.Stack(Unit.Type.WALKER);
        obstacleUnit.add(new Unit(10, 5, 100, 5));
        map.addLeft(0, 0, flyer);
        map.addRight(0, 1, obstacleUnit);
        List<int[]> path = map.getPath(0, 0, 0, 2, true);
        List<String> cells = path.stream().map(p -> p[0] + "," + p[1]).toList();
        assertThat("the flyer path goes through the occupied cell", cells, not(hasItem("0,1")));
    }
}
