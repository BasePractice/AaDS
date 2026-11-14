package ru.mifi.practice.vol1.array;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class ArrayGrowthTest {
    @Test
    void countsCopiesAtEachGrowth() {
        StandardArray<Integer> array = new StandardArray<>(1);
        List<Long> copies = IntStream.range(0, 9).mapToObj(value -> {
            long previous = array.copies();
            array.addLast(value);
            return array.copies() - previous;
        }).toList();
        assertEquals(List.of(0L, 1L, 2L, 0L, 4L, 0L, 0L, 0L, 8L), copies);
    }

    @Test
    void keepsInsertionOrder() {
        StandardArray<Integer> array = new StandardArray<>(1);
        IntStream.range(0, 9).forEach(array::addLast);
        assertEquals(IntStream.range(0, 9).boxed().toList(),
            IntStream.range(0, array.size()).mapToObj(array::get).toList());
    }

    @Test
    void comparesGrowthStrategies() {
        int size = 1_000_000;
        StandardArray<Integer> array = new StandardArray<>(1);
        IntStream.range(0, size).forEach(array::addLast);
        long linearGrowth = (long) size * (size - 1) / 2;
        assertEquals(List.of(1_048_575L, 499_999_500_000L), List.of(array.copies(), linearGrowth));
    }
}
