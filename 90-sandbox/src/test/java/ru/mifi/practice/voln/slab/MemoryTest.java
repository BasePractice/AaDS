package ru.mifi.practice.voln.slab;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Страничная память")
final class MemoryTest {

    @DisplayName("Читает записанное число")
    @Test
    @Timeout(1)
    void readsTheWrittenValue() {
        Memory memory = new Memory.Arena(2, 64);
        int value = new Random().nextInt();
        memory.write(68, value);
        assertThat("the memory forgets the written value", memory.read(68), is(value));
    }

    @DisplayName("Выдаёт разные страницы")
    @Test
    @Timeout(1)
    void handsOutDistinctPages() {
        Memory memory = new Memory.Arena(3, 32);
        assertThat("the same page is handed out twice", memory.page(), is(not(memory.page())));
    }

    @DisplayName("Возвращённую страницу выдаёт первой")
    @Test
    @Timeout(1)
    void reusesTheReleasedPageFirst() {
        Memory memory = new Memory.Arena(4, 32);
        memory.page();
        int page = memory.page();
        memory.release(page);
        assertThat("the released page is not reused first", memory.page(), is(page));
    }

    @DisplayName("Отказывает, когда страницы кончились")
    @Test
    @Timeout(1)
    void refusesThePageWhenExhausted() {
        Memory memory = new Memory.Arena(1, 32);
        memory.page();
        assertThat("the exhausted memory hands out a page",
            assertThrows(IllegalStateException.class, memory::page).getMessage(),
            is("Все 1 страниц памяти уже выданы"));
    }

    @DisplayName("Не принимает невыданную страницу")
    @Test
    @Timeout(1)
    void refusesTheReleaseOfAFreePage() {
        assertThat("a free page is released twice",
            assertThrows(IllegalArgumentException.class, () -> new Memory.Arena(2, 16).release(1)).getMessage(),
            is("Страница 1 не выдана и вернуть её нельзя"));
    }
}
