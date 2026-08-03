package ru.mifi.practice.voln.heroes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TurnQueueTest {
    @Test
    void testTurnDelayedUntilEndAction() {
        BattleMap map = new BattleMap();
        Unit.Stack s1 = new Unit.Stack(Unit.Type.WALKER);
        s1.add(new Unit(10, 5, 100, 5));
        Unit.Stack s2 = new Unit.Stack(Unit.Type.WALKER);
        s2.add(new Unit(10, 5, 100, 5));

        map.addLeft(0, 0, s1);
        map.addLeft(1, 0, s2);
        map.fillTurnQueue();

        // Initial queue should have s1 then s2 (if leftTurn is true)
        assertEquals(2, map.getTurnQueue().size());
        Long id1 = map.getTurnQueue().peekFirst();
        assertEquals(s1, map.getStackById(id1));

        // Move s1
        map.move(0, 0, 0, 1);

        // Queue should STILL have s1 at the head because animation is not finished
        assertEquals(2, map.getTurnQueue().size());
        assertEquals(id1, map.getTurnQueue().peekFirst(), "s1 should still be at the head of queue during animation");
        assertTrue(map.isAnimating());
        assertFalse(s1.hasActed(), "s1 should not be marked as acted yet");

        // End animation
        map.endAction();

        // Now queue should have s2 at the head
        assertFalse(map.isAnimating());
        assertTrue(s1.hasActed(), "s1 should be marked as acted after animation");
        assertEquals(1, map.getTurnQueue().size());
        Long id2 = map.getTurnQueue().peekFirst();
        assertEquals(s2, map.getStackById(id2));
    }

    @Test
    void testAttackDelayedUntilEndAction() {
        Unit.Stack s1 = new Unit.Stack(Unit.Type.WALKER);
        s1.add(new Unit(100, 5, 100, 5)); // Strong attacker

        Unit.Stack s3 = new Unit.Stack(Unit.Type.WALKER);
        s3.add(new Unit(10, 5, 100, 5)); // Another unit to keep queue non-empty

        Unit.Stack s2 = new Unit.Stack(Unit.Type.WALKER);
        s2.add(new Unit(10, 5, 50, 5)); // Weak target

        BattleMap map = new BattleMap();
        map.addLeft(0, 0, s1);
        map.addLeft(1, 0, s3);
        map.addRight(0, 2, s2);
        map.fillTurnQueue();

        // Attack target at (0, 2) from (0, 0) - should move to (0, 1) first
        map.attack(0, 0, 0, 2);

        assertTrue(map.isAnimating(), "Should be animating movement before attack");
        assertEquals(1, s2.size(), "Target should NOT be damaged yet");

        // End animation
        map.endAction();

        assertFalse(map.isAnimating());
        assertEquals(0, s2.size(), "Target should be damaged/killed after animation ends");
        assertTrue(s1.hasActed(), "Attacker should be marked as acted");
        assertEquals(1, map.getTurnQueue().size(), "One unit should remain in queue");
    }
}
