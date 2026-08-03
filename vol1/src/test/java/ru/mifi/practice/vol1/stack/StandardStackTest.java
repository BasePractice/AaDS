package ru.mifi.practice.vol1.stack;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import ru.mifi.practice.vol1.array.LinkedArray;
import ru.mifi.practice.vol1.array.StandardArray;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Стек")
final class StandardStackTest {

    @DisplayName("Новый стек пуст")
    @Test
    @Timeout(1)
    void startsEmpty() {
        assertThat("a fresh stack dont look empty",
            new StandardStack<>(new StandardArray<Integer>(4)).isEmpty(), is(true));
    }

    @DisplayName("После помещения стек не пуст")
    @Test
    @Timeout(1)
    void stopsBeingEmptyOnPush() {
        Stack<Integer> stack = new StandardStack<>(new StandardArray<>(4));
        stack.push(1);
        assertThat("stack still looks empty after push", stack.isEmpty(), is(false));
    }

    @DisplayName("Извлекает элементы в обратном порядке")
    @Test
    @Timeout(1)
    void servesElementsInReverseOrder() {
        Stack<Integer> stack = new StandardStack<>(new StandardArray<>(4));
        stack.push(1);
        stack.push(2);
        assertThat("stack dont serve the newest element first", stack.pop(), is(2));
    }

    @DisplayName("Просмотр вершины не снимает элемент")
    @Test
    @Timeout(1)
    void keepsTheTopOnPeek() {
        Stack<Integer> stack = new StandardStack<>(new StandardArray<>(4));
        stack.push(1);
        stack.peek();
        assertThat("peek removes the element it shows", stack.pop(), is(1));
    }

    @DisplayName("Извлечение опустошает стек")
    @Test
    @Timeout(1)
    void becomesEmptyWhenEveryElementIsPopped() {
        Stack<Integer> stack = new StandardStack<>(new StandardArray<>(4));
        stack.push(1);
        stack.pop();
        assertThat("stack dont become empty after every element is popped", stack.isEmpty(), is(true));
    }

    @DisplayName("Работает поверх связного списка")
    @Test
    @Timeout(1)
    void servesElementsInReverseOrderOnLinkedStorage() {
        Stack<Integer> stack = new StandardStack<>(new LinkedArray<>());
        stack.push(1);
        stack.push(2);
        assertThat("linked storage dont keep the stack order", stack.pop(), is(2));
    }
}
