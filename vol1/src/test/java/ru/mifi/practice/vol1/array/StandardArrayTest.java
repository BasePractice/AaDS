package ru.mifi.practice.vol1.array;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Массив")
class StandardArrayTest {

    private Array<Integer> array;

    @BeforeEach
    void setUp() {
        array = new StandardArray<>(100);
        for (int i = 0; i < 100; i++) {
            array.set(i, i);
        }
    }

    @DisplayName("Получение значения по индексу")
    @Test
    void get() {
        Integer i = array.get(0);
        assertNotNull(i);
    }

    @DisplayName("Установка значения по индексу")
    @Test
    void set() {
        array.set(0, 10);
        Integer i = array.get(0);
        assertNotNull(i);
        assertEquals(10, i);
    }

    @DisplayName("Удаление значения по индексу")
    @Test
    void delete() {
        assertEquals(100, array.size());
        Integer i = array.get(0);
        assertNotNull(i);
        array.delete(0);
        i = array.get(0);
        assertNotNull(i);
        assertEquals(99, array.size());
    }

    @Test
    void size() {
        assertEquals(100, array.size());
    }

    @DisplayName("Добавить последний элемент")
    @Test
    void addLast() {
        array.addLast(10);
        assertEquals(101, array.size());
    }
}
