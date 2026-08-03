package ru.mifi.practice.vol6.tree;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Куча")
final class HeapTest {

    @DisplayName("В корне лежит минимум")
    @Test
    void keepsTheMinimumAtTheTop() {
        Heap<Integer> heap = new Heap.Minimum<>(10);
        heap.add(5).add(3).add(9).add(1);
        assertThat("the top dont hold the minimum", heap.top(), is(1));
    }

    @DisplayName("Минимум остаётся в корне при добавлении большего")
    @Test
    void keepsTheMinimumWhenALargerValueArrives() {
        Heap<Integer> heap = new Heap.Minimum<>(10);
        heap.add(1).add(7);
        assertThat("a larger value displaces the minimum", heap.top(), is(1));
    }

    @DisplayName("Извлечение корня возвращает минимум")
    @Test
    void returnsTheMinimumOnDelete() {
        Heap<Integer> heap = new Heap.Minimum<>(10);
        heap.add(5).add(3).add(9).add(1);
        assertThat("delete dont return the minimum", heap.deleteRoot(), is(1));
    }

    @DisplayName("Извлечение корня уменьшает размер")
    @Test
    void shrinksOnDelete() {
        Heap<Integer> heap = new Heap.Minimum<>(10);
        heap.add(5).add(3);
        heap.deleteRoot();
        assertThat("delete dont shrink the heap", heap.size(), is(1));
    }

    @DisplayName("Последовательное извлечение даёт возрастающий порядок")
    @Test
    void drainsInAscendingOrder() {
        Heap<Integer> heap = new Heap.Minimum<>(10);
        for (int value : new int[]{5, 3, 9, 1, 7, 2}) {
            heap.add(value);
        }
        List<Integer> drained = new ArrayList<>();
        while (heap.size() > 0) {
            drained.add(heap.deleteRoot());
        }
        assertThat("draining the heap dont produce a sorted sequence", drained, contains(1, 2, 3, 5, 7, 9));
    }

    @DisplayName("Извлечение единственного элемента опустошает кучу")
    @Test
    void becomesEmptyAfterTheOnlyElementIsRemoved() {
        Heap<Integer> heap = new Heap.Minimum<>(10);
        heap.add(5);
        heap.deleteRoot();
        assertThat("the heap dont become empty", heap.size(), is(0));
    }

    @DisplayName("Извлечение не теряет последний элемент")
    @Test
    void keepsTheLastElementOnDelete() {
        Heap<Integer> heap = new Heap.Minimum<>(10);
        heap.add(1).add(2);
        heap.deleteRoot();
        assertThat("the last element is lost and the top becomes null", heap.top(), is(2));
    }

    @DisplayName("Пустая куча не отдаёт корень")
    @Test
    void cannotReadTheTopOfAnEmptyHeap() {
        Heap<Integer> heap = new Heap.Minimum<>(10);
        assertThrows(NoSuchElementException.class, heap::top, "an empty heap returns a top");
    }

    @DisplayName("Переполнение кучи не проходит молча")
    @Test
    void cannotAddBeyondCapacity() {
        Heap<Integer> heap = new Heap.Minimum<>(1);
        heap.add(1);
        assertThrows(IllegalStateException.class, () -> heap.add(2), "overflow is swallowed silently");
    }

    @DisplayName("Восстановление свойства кучи поднимает минимум в корень")
    @Test
    void restoresTheHeapPropertyOnRefresh() {
        Heap<Integer> heap = new Heap.Minimum<>(10);
        heap.add(1).add(2).add(3);
        heap.refresh();
        assertThat("refresh breaks the heap property", heap.top(), is(1));
    }
}
