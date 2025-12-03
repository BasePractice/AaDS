package ru.mifi.practice.voln.features;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Счетчик")
class CountRangeTest {
    private static final long USER_ID = 10067;
    private CountRange.Default count;
    private long countId;

    @BeforeEach
    void setUp() {
        count = new CountRange.Default();
        countId = count.addCount("3", new long[]{0, 100, 200, 300}, new long[]{0, 0, 1, 2});
    }

    @Test
    @DisplayName("Входит в диапазон")
    void inRange() {
        Optional<Long> counted = count.countValue(countId, USER_ID);
        assertTrue(counted.isEmpty());
        Optional<Long> add = count.add(countId, USER_ID, 1);
        assertFalse(add.isPresent());
        add = count.add(countId, USER_ID, 99);
        assertTrue(add.isPresent());
        assertEquals(1L, add.get().longValue());
    }
}
