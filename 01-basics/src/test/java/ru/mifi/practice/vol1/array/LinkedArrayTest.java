package ru.mifi.practice.vol1.array;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Список")
final class LinkedArrayTest {

    @DisplayName("Читает значение, записанное по второму индексу")
    @Test
    @Timeout(1)
    void readsBackValueStoredAtSecondIndex() {
        Array<Integer> array = new LinkedArray<>();
        array.set(0, 10);
        array.set(1, 20);
        assertThat("second index dont keep its own value", array.get(1), is(20));
    }

    @DisplayName("Запись по второму индексу не затирает первый")
    @Test
    @Timeout(1)
    void keepsFirstElementWhenSecondIsStored() {
        Array<Integer> array = new LinkedArray<>();
        array.set(0, 10);
        array.set(1, 20);
        assertThat("storing second element overwrites the first", array.get(0), is(10));
    }

    @DisplayName("Считает записанные элементы")
    @Test
    @Timeout(1)
    void countsEveryStoredElement() {
        Array<Integer> array = new LinkedArray<>();
        array.set(0, 10);
        array.set(1, 20);
        array.set(2, 30);
        assertThat("size dont match the number of stored elements", array.size(), is(3));
    }

    @DisplayName("Добавление в конец увеличивает размер на единицу")
    @Test
    @Timeout(1)
    void growsByOneOnAppend() {
        Array<Integer> array = new LinkedArray<>();
        array.addLast(10);
        array.addLast(20);
        assertThat("append dont grow the list by exactly one", array.size(), is(2));
    }

    @DisplayName("Удаление возвращает удалённое значение")
    @Test
    @Timeout(1)
    void returnsTheRemovedValue() {
        Array<Integer> array = new LinkedArray<>();
        array.set(0, 10);
        array.set(1, 20);
        assertThat("delete dont return the value it removed", array.delete(0), is(10));
    }

    @DisplayName("Удаление уменьшает размер")
    @Test
    @Timeout(1)
    void shrinksOnDelete() {
        Array<Integer> array = new LinkedArray<>();
        array.set(0, 10);
        array.set(1, 20);
        array.delete(0);
        assertThat("delete dont shrink the list", array.size(), is(1));
    }

    @DisplayName("После удаления оставшиеся элементы сдвигаются")
    @Test
    @Timeout(1)
    void shiftsRemainingElementsOnDelete() {
        Array<Integer> array = new LinkedArray<>();
        array.set(0, 10);
        array.set(1, 20);
        array.delete(0);
        assertThat("survivor dont move to the freed index", array.get(0), is(20));
    }

    @DisplayName("Чтение за последним индексом запрещено")
    @Test
    @Timeout(1)
    void cannotReadBeyondTheLastIndex() {
        Array<Integer> array = new LinkedArray<>();
        array.set(0, 10);
        assertThrows(IndexOutOfBoundsException.class, () -> array.get(1),
            "reading past the end dont fail");
    }
}
