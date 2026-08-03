package ru.mifi.practice.vol1.array;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Массив")
final class StandardArrayTest {

    @DisplayName("Читает значение, записанное по индексу")
    @Test
    void readsBackValueStoredAtIndex() {
        Array<Integer> array = new StandardArray<>(4);
        array.set(2, 20);
        assertThat("index dont keep its own value", array.get(2), is(20));
    }

    @DisplayName("Растёт под индекс, превышающий двойную ёмкость")
    @Test
    void growsBeyondDoubledCapacity() {
        Array<Integer> array = new StandardArray<>(4);
        array.set(100, 20);
        assertThat("array dont grow enough for a far index", array.get(100), is(20));
    }

    @DisplayName("Добавление в конец увеличивает размер на единицу")
    @Test
    void growsByOneOnAppend() {
        Array<Integer> array = new StandardArray<>(4);
        array.addLast(10);
        array.addLast(20);
        assertThat("append dont grow the array by exactly one", array.size(), is(2));
    }

    @DisplayName("Удаление возвращает удалённое значение")
    @Test
    void returnsTheRemovedValue() {
        Array<Integer> array = new StandardArray<>(4);
        array.set(0, 10);
        array.set(1, 20);
        assertThat("delete dont return the value it removed", array.delete(0), is(10));
    }

    @DisplayName("После удаления оставшиеся элементы сдвигаются")
    @Test
    void shiftsRemainingElementsOnDelete() {
        Array<Integer> array = new StandardArray<>(4);
        array.set(0, 10);
        array.set(1, 20);
        array.delete(0);
        assertThat("survivor dont move to the freed index", array.get(0), is(20));
    }

    @DisplayName("Удаление последнего элемента освобождает ссылку")
    @Test
    void releasesReferenceOnDeletingLast() {
        Array<Integer> array = new StandardArray<>(4);
        array.set(0, 10);
        array.delete(0);
        assertThrows(IndexOutOfBoundsException.class, () -> array.get(0),
            "deleted tail is still readable");
    }

    @DisplayName("Чтение за последним индексом запрещено")
    @Test
    void cannotReadBeyondTheLastIndex() {
        Array<Integer> array = new StandardArray<>(4);
        array.set(0, 10);
        assertThrows(IndexOutOfBoundsException.class, () -> array.get(1),
            "reading past the end dont fail");
    }
}
