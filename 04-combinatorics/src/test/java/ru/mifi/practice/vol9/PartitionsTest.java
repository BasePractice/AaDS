package ru.mifi.practice.vol9;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Разбиения числа")
final class PartitionsTest {

    @DisplayName("Число пять имеет семь разбиений")
    @Test
    @Timeout(1)
    void generatesEveryPartition() {
        assertThat("the number of partitions of five dont equal seven",
            Combinatorics.collect(Partitions.of(5)).size(), is(7));
    }

    @DisplayName("Разбиения идут по невозрастанию слагаемых")
    @Test
    @Timeout(1)
    void generatesPartitionsInCanonicalOrder() {
        assertThat("partitions dont come out in canonical order",
            Combinatorics.collect(Partitions.of(5)),
            contains("[5]", "[4, 1]", "[3, 2]", "[3, 1, 1]", "[2, 2, 1]", "[2, 1, 1, 1]", "[1, 1, 1, 1, 1]"));
    }

    @DisplayName("Ноль имеет одно пустое разбиение")
    @Test
    @Timeout(1)
    void generatesOnePartitionOfZero() {
        List<String> partitions = Combinatorics.collect(Partitions.of(0));
        assertThat("zero dont have exactly one partition", partitions.size(), is(1));
    }

    @DisplayName("Отрицательное число не разбивается")
    @Test
    @Timeout(1)
    void cannotPartitionNegativeNumber() {
        assertThrows(IllegalArgumentException.class, () -> Partitions.of(-1),
            "a negative number is partitioned");
    }
}
