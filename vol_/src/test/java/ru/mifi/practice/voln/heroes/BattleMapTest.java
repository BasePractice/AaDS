package ru.mifi.practice.voln.heroes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class BattleMapTest {
    @Test
    void testAttackMovement() {
        BattleMap map = new BattleMap();
        Unit.Stack attacker = new Unit.Stack(Unit.Type.WALKER);
        attacker.add(new Unit(10, 5, 100, 5));

        Unit.Stack target = new Unit.Stack(Unit.Type.WALKER);
        target.add(new Unit(10, 5, 100, 5));

        map.addLeft(0, 0, attacker);
        map.addRight(0, 2, target);

        // Attack target at (0, 2) from (0, 0)
        map.attack(0, 0, 0, 2);

        // Attacker moves to (0, 1)
        Unit.Stack at00 = map.getStack(0, 0);
        Unit.Stack at01 = map.getStack(0, 1);

        assertNull(at00, "Attacker should have moved from (0,0)");
        assertNotNull(at01, "Attacker should have moved to (0,1)");
        assertEquals(attacker, at01);
    }

    @Test
    void testMoveToOccupiedCellShouldFail() {
        BattleMap map = new BattleMap();
        Unit.Stack walker = new Unit.Stack(Unit.Type.WALKER);
        walker.add(new Unit(10, 5, 100, 5));
        Unit.Stack target = new Unit.Stack(Unit.Type.WALKER);
        target.add(new Unit(10, 5, 100, 5));

        map.addLeft(0, 0, walker);
        map.addRight(0, 1, target);

        // Try to move to (0, 1) which is occupied
        map.move(0, 0, 0, 1);

        assertNotNull(map.getStack(0, 0), "Walker should stay at (0, 0)");
        assertNotNull(map.getStack(0, 1), "Target should stay at (0, 1)");
        assertEquals(target, map.getStack(0, 1), "Target should not be overwritten");
    }
}
