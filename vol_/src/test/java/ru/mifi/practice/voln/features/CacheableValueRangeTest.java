package ru.mifi.practice.voln.features;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Счетчик")
class CacheableValueRangeTest {
    private static final long USER_ID = 10067;
    private CountRange.Default count;
    private long countId;

    @BeforeEach
    void setUp() {
        count = new CountRange.Default();
        countId = count.addCount("3", new long[]{0, 100, 200, 300}, new long[]{0, 0, 1001, 1002});
    }

    @Test
    @DisplayName("Входит в диапазон")
    void inRange() {
        count.increment(countId, USER_ID, 1);
        checkCountValue(0L);
        checkUserValue(1L);
        assertTrue(count.availableValue(countId, USER_ID));
        count.acceptValue(countId, USER_ID);
        assertFalse(count.availableValue(countId, USER_ID));
        count.increment(countId, USER_ID, 99);
        checkCountValue(0L);
        checkUserValue(100L);
        count.increment(countId, USER_ID, 1);
        checkCountValue(1001L);
        checkUserValue(101L);
    }

    private void checkUserValue(long expected) {
        Optional<Long> usered = count.userValue(countId, USER_ID);
        assertTrue(usered.isPresent());
        assertEquals(expected, usered.get().longValue());
    }

    private void checkCountValue(long expected) {
        Optional<Long> counted = count.countValue(countId, USER_ID);
        assertTrue(counted.isPresent());
        assertEquals(expected, counted.get().longValue());
    }
}
