package ru.mifi.practice.voln.slab;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Slab-кэш")
final class CacheTest {

    @DisplayName("Укладывает объекты в одну страницу, пока она не заполнится")
    @Test
    @Timeout(1)
    void packsObjectsIntoOnePage() {
        Cache cache = new Cache.Slabs(new Memory.Arena(4, 64), 16);
        for (int index = 0; index < 4; index++) {
            cache.allocate();
        }
        assertThat("the cache takes a page before the first one is full", cache.slabs(), is(1));
    }

    @DisplayName("Берёт новую страницу за полной")
    @Test
    @Timeout(1)
    void growsAfterTheFullPage() {
        Cache cache = new Cache.Slabs(new Memory.Arena(4, 64), 16);
        for (int index = 0; index < 5; index++) {
            cache.allocate();
        }
        assertThat("the cache does not grow past the full page", cache.slabs(), is(2));
    }

    @DisplayName("Держит пустую плиту до явного сжатия")
    @Test
    @Timeout(1)
    void keepsTheEmptySlab() {
        Cache cache = new Cache.Slabs(new Memory.Arena(2, 64), 16);
        cache.free(cache.allocate());
        assertThat("the empty slab leaves the cache by itself", cache.slabs(), is(1));
    }

    @DisplayName("Сжатие возвращает пустые плиты памяти")
    @Test
    @Timeout(1)
    void shrinksTheEmptySlabs() {
        Cache cache = new Cache.Slabs(new Memory.Arena(2, 64), 16);
        cache.free(cache.allocate());
        cache.shrink();
        assertThat("the empty slab survives the shrink", cache.slabs(), is(0));
    }

    @DisplayName("Сжатие не трогает занятые плиты")
    @Test
    @Timeout(1)
    void shrinkKeepsTheBusySlabs() {
        Cache cache = new Cache.Slabs(new Memory.Arena(2, 64), 16);
        cache.allocate();
        cache.shrink();
        assertThat("the busy slab is lost in the shrink", cache.slabs(), is(1));
    }

    @DisplayName("Отданная страница снова доступна памяти")
    @Test
    @Timeout(1)
    void returnsThePageToTheMemory() {
        Memory memory = new Memory.Arena(1, 64);
        Cache cache = new Cache.Slabs(memory, 32);
        cache.free(cache.allocate());
        cache.shrink();
        assertThat("the shrunk page does not come back to the memory", memory.page(), is(0));
    }

    @DisplayName("Частичную плиту предпочитает пустой")
    @Test
    @Timeout(1)
    void prefersThePartialSlab() {
        Cache cache = new Cache.Slabs(new Memory.Arena(2, 64), 16);
        for (int index = 0; index < 8; index++) {
            cache.allocate();
        }
        for (int address = 0; address < 64; address += 16) {
            cache.free(address);
        }
        cache.free(80);
        assertThat("the empty slab is used before the partial one", cache.allocate(), is(80));
    }

    @DisplayName("Не принимает чужой адрес")
    @Test
    @Timeout(1)
    void refusesTheForeignAddress() {
        Cache cache = new Cache.Slabs(new Memory.Arena(2, 64), 16);
        cache.allocate();
        assertThat("a foreign address is accepted",
            assertThrows(IllegalArgumentException.class, () -> cache.free(64)).getMessage(),
            is("Адрес 64 лежит вне плит этого кэша"));
    }

    @DisplayName("Не берёт объекты меньше ссылки")
    @Test
    @Timeout(1)
    void refusesTheTinyObject() {
        assertThat("an object too small for the link is accepted",
            assertThrows(IllegalArgumentException.class,
                () -> new Cache.Slabs(new Memory.Arena(1, 64), 3)).getMessage(),
            is("Объект в 3 байт не вмещает ссылку на следующий свободный"));
    }

    @DisplayName("Не берёт объекты больше страницы")
    @Test
    @Timeout(1)
    void refusesTheHugeObject() {
        assertThat("an object larger than the page is accepted",
            assertThrows(IllegalArgumentException.class,
                () -> new Cache.Slabs(new Memory.Arena(1, 64), 65)).getMessage(),
            is("Объект в 65 байт не помещается в страницу из 64"));
    }

    @DisplayName("Живые объекты не перекрываются и хранят свои данные")
    @Test
    @Timeout(5)
    void keepsLiveObjectsApart() {
        Random random = new Random(20261024L);
        Memory memory = new Memory.Arena(64, 128);
        Cache cache = new Cache.Slabs(memory, 12);
        Map<Integer, Integer> live = new HashMap<>();
        for (int step = 0; step < 5000; step++) {
            if (live.isEmpty() || random.nextInt(3) > 0 && live.size() < 600) {
                int address = cache.allocate();
                int value = random.nextInt();
                memory.write(address, value);
                live.put(address, value);
            } else {
                List<Integer> addresses = new ArrayList<>(live.keySet());
                int address = addresses.get(random.nextInt(addresses.size()));
                cache.free(address);
                live.remove(address);
            }
        }
        long broken = live.entrySet().stream().filter(entry -> memory.read(entry.getKey()) != entry.getValue()).count();
        assertThat("live objects overwrite each other", broken, is(0L));
    }
}
