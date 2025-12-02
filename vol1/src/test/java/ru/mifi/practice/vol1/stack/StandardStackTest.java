package ru.mifi.practice.vol1.stack;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.mifi.practice.vol1.array.StandardArray;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Стек")
class StandardStackTest {

    private Stack<Integer> stack;

    @BeforeEach
    void setUp() {
        stack = new StandardStack<>(new StandardArray<>(100));
    }

    @DisplayName("Помещение элемента на вершину стека")
    @Test
    void push() {
        stack.push(1);
    }

    @DisplayName("Извлечение элемента из вершины стека")
    @Test
    void pop() {
        stack.push(1);
        assertEquals(1, stack.pop());
    }

    @DisplayName("Получение элемента на вершине стека")
    @Test
    void peek() {
        stack.push(1);
        assertEquals(1, stack.peek());
        assertFalse(stack.isEmpty());
    }

    @Test
    void isEmpty() {
        assertTrue(stack.isEmpty());
        stack.push(1);
        assertFalse(stack.isEmpty());
    }
}
