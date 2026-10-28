package ru.mifi.practice.voln.slab;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Плита")
final class SlabTest {

    @DisplayName("Первый объект лежит в начале своей страницы")
    @Test
    @Timeout(1)
    void placesTheFirstObjectAtThePageStart() {
        assertThat("the first object is not at the page start",
            new Slab(new Memory.Arena(3, 64), 2, 16).allocate(), is(128));
    }

    @DisplayName("Объекты идут друг за другом")
    @Test
    @Timeout(1)
    void placesObjectsOneAfterAnother() {
        Slab slab = new Slab(new Memory.Arena(1, 64), 0, 24);
        slab.allocate();
        assertThat("the second object does not follow the first", slab.allocate(), is(24));
    }

    @DisplayName("Заполняется, когда выданы все объекты страницы")
    @Test
    @Timeout(1)
    void becomesFullAfterTheLastObject() {
        Slab slab = new Slab(new Memory.Arena(1, 64), 0, 20);
        slab.allocate();
        slab.allocate();
        slab.allocate();
        assertThat("the slab with every object handed out is not full", slab.full(), is(true));
    }

    @DisplayName("Освобождённый объект выдаёт первым")
    @Test
    @Timeout(1)
    void reusesTheFreedObjectFirst() {
        Slab slab = new Slab(new Memory.Arena(1, 64), 0, 8);
        slab.allocate();
        int address = slab.allocate();
        slab.allocate();
        slab.free(address);
        assertThat("the freed object is not reused first", slab.allocate(), is(address));
    }

    @DisplayName("Пустеет, когда вернули все объекты")
    @Test
    @Timeout(1)
    void becomesEmptyAfterTheLastFree() {
        Slab slab = new Slab(new Memory.Arena(1, 64), 0, 16);
        int first = slab.allocate();
        int second = slab.allocate();
        slab.free(second);
        slab.free(first);
        assertThat("the slab with every object back is not empty", slab.empty(), is(true));
    }

    @DisplayName("Не принимает объект дважды")
    @Test
    @Timeout(1)
    void refusesTheDoubleFree() {
        Slab slab = new Slab(new Memory.Arena(1, 64), 0, 16);
        int address = slab.allocate();
        slab.free(address);
        assertThat("the double free goes unnoticed",
            assertThrows(IllegalArgumentException.class, () -> slab.free(address)).getMessage(),
            is("Объект по адресу 0 уже свободен"));
    }

    @DisplayName("Не принимает адрес из середины объекта")
    @Test
    @Timeout(1)
    void refusesTheAddressInsideAnObject() {
        Slab slab = new Slab(new Memory.Arena(1, 64), 0, 16);
        slab.allocate();
        assertThat("the address inside an object is accepted",
            assertThrows(IllegalArgumentException.class, () -> slab.free(4)).getMessage(),
            is("Адрес 4 не указывает на начало объекта плиты"));
    }

    @DisplayName("Данные владельцев не портят список свободных")
    @Test
    @Timeout(1)
    void survivesTheOwnerData() {
        Random random = new Random();
        Memory memory = new Memory.Arena(1, 256);
        Slab slab = new Slab(memory, 0, 8);
        int[] addresses = new int[32];
        for (int index = 0; index < addresses.length; index++) {
            addresses[index] = slab.allocate();
            memory.write(addresses[index], random.nextInt());
        }
        for (int address : addresses) {
            slab.free(address);
        }
        Set<Integer> again = new HashSet<>();
        for (int index = 0; index < addresses.length; index++) {
            again.add(slab.allocate());
        }
        assertThat("the owner data breaks the free list", again.size(), is(addresses.length));
    }
}
