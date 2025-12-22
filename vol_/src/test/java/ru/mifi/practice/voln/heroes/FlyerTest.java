package ru.mifi.practice.voln.heroes;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class FlyerTest {
    @Test
    void testFlyerPathOverUnit() {
        BattleMap map = new BattleMap();
        Unit.Stack flyer = new Unit.Stack(Unit.Type.FLYER);
        flyer.add(new Unit(10, 5, 100, 5));

        Unit.Stack obstacleUnit = new Unit.Stack(Unit.Type.WALKER);
        obstacleUnit.add(new Unit(10, 5, 100, 5));

        map.addLeft(0, 0, flyer);
        map.addRight(0, 1, obstacleUnit); // Occupy (0,1)

        // Try to get path to (0,2)
        List<int[]> path = map.getPath(0, 0, 0, 2, true);

        assertFalse(path.isEmpty(), "Flyer should be able to find path over another unit");
        assertEquals(3, path.size());
        assertArrayEquals(new int[]{0, 0}, path.get(0));
        assertArrayEquals(new int[]{0, 1}, path.get(1));
        assertArrayEquals(new int[]{0, 2}, path.get(2));
    }
}
