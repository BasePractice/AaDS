package ru.mifi.practice.voln.features;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Счетчик")
final class CacheableValueRangeTest {

    @Test
    @Timeout(1)
    @DisplayName("Значение счётчика после первого инкремента")
    void countValueAfterFirstIncrement() {
        CountRange.Default count = new CountRange.Default();
        long countId = count.addCount("3", new long[]{0, 100, 200, 300}, new long[]{0, 0, 1001, 1002});
        count.increment(countId, 10067L, 1);
        assertThat("count value after first increment is wrong",
            count.countValue(countId, 10067L), is(Optional.of(0L)));
    }

    @Test
    @Timeout(1)
    @DisplayName("Значение пользователя после первого инкремента")
    void userValueAfterFirstIncrement() {
        CountRange.Default count = new CountRange.Default();
        long countId = count.addCount("3", new long[]{0, 100, 200, 300}, new long[]{0, 0, 1001, 1002});
        count.increment(countId, 10067L, 1);
        assertThat("user value after first increment is wrong",
            count.userValue(countId, 10067L), is(Optional.of(1L)));
    }

    @Test
    @Timeout(1)
    @DisplayName("Значение доступно после первого инкремента")
    void valueAvailableAfterFirstIncrement() {
        CountRange.Default count = new CountRange.Default();
        long countId = count.addCount("3", new long[]{0, 100, 200, 300}, new long[]{0, 0, 1001, 1002});
        count.increment(countId, 10067L, 1);
        assertThat("value is not available after first increment",
            count.availableValue(countId, 10067L), is(true));
    }

    @Test
    @Timeout(1)
    @DisplayName("Значение недоступно после принятия")
    void valueUnavailableAfterAccept() {
        CountRange.Default count = new CountRange.Default();
        long countId = count.addCount("3", new long[]{0, 100, 200, 300}, new long[]{0, 0, 1001, 1002});
        count.increment(countId, 10067L, 1);
        count.acceptValue(countId, 10067L);
        assertThat("value stays available after accept",
            count.availableValue(countId, 10067L), is(false));
    }

    @Test
    @Timeout(1)
    @DisplayName("Значение счётчика после второго инкремента")
    void countValueAfterSecondIncrement() {
        CountRange.Default count = new CountRange.Default();
        long countId = count.addCount("3", new long[]{0, 100, 200, 300}, new long[]{0, 0, 1001, 1002});
        count.increment(countId, 10067L, 1);
        count.acceptValue(countId, 10067L);
        count.increment(countId, 10067L, 99);
        assertThat("count value after second increment is wrong",
            count.countValue(countId, 10067L), is(Optional.of(0L)));
    }

    @Test
    @Timeout(1)
    @DisplayName("Значение пользователя после второго инкремента")
    void userValueAfterSecondIncrement() {
        CountRange.Default count = new CountRange.Default();
        long countId = count.addCount("3", new long[]{0, 100, 200, 300}, new long[]{0, 0, 1001, 1002});
        count.increment(countId, 10067L, 1);
        count.acceptValue(countId, 10067L);
        count.increment(countId, 10067L, 99);
        assertThat("user value after second increment is wrong",
            count.userValue(countId, 10067L), is(Optional.of(100L)));
    }

    @Test
    @Timeout(1)
    @DisplayName("Значение счётчика после третьего инкремента")
    void countValueAfterThirdIncrement() {
        CountRange.Default count = new CountRange.Default();
        long countId = count.addCount("3", new long[]{0, 100, 200, 300}, new long[]{0, 0, 1001, 1002});
        count.increment(countId, 10067L, 1);
        count.acceptValue(countId, 10067L);
        count.increment(countId, 10067L, 99);
        count.increment(countId, 10067L, 1);
        assertThat("count value after third increment is wrong",
            count.countValue(countId, 10067L), is(Optional.of(1001L)));
    }

    @Test
    @Timeout(1)
    @DisplayName("Значение пользователя после третьего инкремента")
    void userValueAfterThirdIncrement() {
        CountRange.Default count = new CountRange.Default();
        long countId = count.addCount("3", new long[]{0, 100, 200, 300}, new long[]{0, 0, 1001, 1002});
        count.increment(countId, 10067L, 1);
        count.acceptValue(countId, 10067L);
        count.increment(countId, 10067L, 99);
        count.increment(countId, 10067L, 1);
        assertThat("user value after third increment is wrong",
            count.userValue(countId, 10067L), is(Optional.of(101L)));
    }
}
