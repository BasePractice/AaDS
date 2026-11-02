package ru.mifi.practice.commons;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/** Проверка счётчика выделенных объектов, которым оценивается память алгоритмов. */
@DisplayName("Счётчик объектов")
final class ObjectCounterTest {

    @DisplayName("Новый счётчик стоит на нуле")
    @Test
    @Timeout(1)
    void startsFromZero() {
        assertThat("a fresh object counter dont start from zero", ObjectCounter.create().count(), is(0));
    }

    @DisplayName("Один объект увеличивает счётчик на единицу")
    @Test
    @Timeout(1)
    void countsOneObject() {
        ObjectCounter counter = ObjectCounter.create();
        counter.allocate();
        assertThat("one allocated object dont give one", counter.count(), is(1));
    }

    @DisplayName("Несколько объектов учитываются сразу")
    @Test
    @Timeout(1)
    void countsSeveralObjectsAtOnce() {
        ObjectCounter counter = ObjectCounter.create();
        counter.allocate(5);
        assertThat("five allocated objects dont give five", counter.count(), is(5));
    }

    @DisplayName("Сброс возвращает счётчик на ноль")
    @Test
    @Timeout(1)
    void returnsToZeroOnReset() {
        ObjectCounter counter = ObjectCounter.create();
        counter.allocate(3);
        counter.reset();
        assertThat("reset dont return the object counter to zero", counter.count(), is(0));
    }
}
