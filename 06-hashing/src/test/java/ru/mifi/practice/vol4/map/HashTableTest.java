package ru.mifi.practice.vol4.map;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import ru.mifi.practice.commons.Counter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Хеш-таблица")
final class HashTableTest {

    @DisplayName("Возвращает положенное значение")
    @Test
    @Timeout(1)
    void readsBackTheStoredValue() {
        HashTable<String, Integer> table = new HashTable.Default<>(8);
        table.put("a", 1, new Counter.Default());
        assertThat("stored value dont come back",
            table.get("a", new Counter.Default()).orElseThrow(), is(1));
    }

    @DisplayName("Повторная запись заменяет значение")
    @Test
    @Timeout(1)
    void replacesValueOnRepeatedPut() {
        HashTable<String, Integer> table = new HashTable.Default<>(8);
        table.put("a", 1, new Counter.Default());
        table.put("a", 2, new Counter.Default());
        assertThat("a repeated put silently drops the new value",
            table.get("a", new Counter.Default()).orElseThrow(), is(2));
    }

    @DisplayName("Повторная запись не увеличивает размер")
    @Test
    @Timeout(1)
    void keepsSizeOnRepeatedPut() {
        HashTable<String, Integer> table = new HashTable.Default<>(8);
        table.put("a", 1, new Counter.Default());
        table.put("a", 2, new Counter.Default());
        assertThat("size counts put calls instead of stored entries", table.size(), is(1));
    }

    @DisplayName("Размер считает различные ключи")
    @Test
    @Timeout(1)
    void countsDistinctKeys() {
        HashTable<String, Integer> table = new HashTable.Default<>(8);
        table.put("a", 1, new Counter.Default());
        table.put("b", 2, new Counter.Default());
        assertThat("size dont match the number of distinct keys", table.size(), is(2));
    }

    @DisplayName("Отсутствующий ключ не читается")
    @Test
    @Timeout(1)
    void reportsMissingKey() {
        HashTable<String, Integer> table = new HashTable.Default<>(8);
        table.put("a", 1, new Counter.Default());
        assertThat("a missing key is reported as present",
            table.get("b", new Counter.Default()).isPresent(), is(false));
    }

    @DisplayName("Удаление отсутствующего ключа ничего не возвращает")
    @Test
    @Timeout(1)
    void removesMissingKeyWithoutReturningAValue() {
        HashTable<String, Integer> table = new HashTable.Default<>(1);
        table.put("a", 1, new Counter.Default());
        assertThat("removing a missing key returns a value from a neighbour",
            table.remove("b", new Counter.Default()).isPresent(), is(false));
    }

    @DisplayName("Удаление возвращает удалённое значение")
    @Test
    @Timeout(1)
    void returnsTheRemovedValue() {
        HashTable<String, Integer> table = new HashTable.Default<>(8);
        table.put("a", 1, new Counter.Default());
        assertThat("delete dont return the value it removed",
            table.remove("a", new Counter.Default()).orElseThrow(), is(1));
    }

    @DisplayName("Удаление уменьшает размер")
    @Test
    @Timeout(1)
    void shrinksOnRemove() {
        HashTable<String, Integer> table = new HashTable.Default<>(8);
        table.put("a", 1, new Counter.Default());
        table.remove("a", new Counter.Default());
        assertThat("remove dont shrink the table", table.size(), is(0));
    }

    @DisplayName("Разные ключи в одной корзине не мешают друг другу")
    @Test
    @Timeout(1)
    void keepsCollidingKeysApart() {
        HashTable<String, Integer> table = new HashTable.Default<>(1);
        table.put("a", 1, new Counter.Default());
        table.put("b", 2, new Counter.Default());
        assertThat("colliding keys share a value",
            table.get("a", new Counter.Default()).orElseThrow(), is(1));
    }

    /**
     * Задание к разделу «Хеширование»: реализация Default помечена вопросом «Почему реализация
     * не оптимальна? Как минимум две причины». Первая — resize с пустым телом: перехеширования нет,
     * и при переполнении корзин таблица вырождается в связные списки, то есть заявленная
     * амортизированная O(1) не выполняется. Вторая — массив entries объявлен final, поэтому
     * перехеширование в текущем дизайне невозможно в принципе. Снимите @Disabled и почините.
     */
    @Disabled("Учебное задание: в HashTable.Default нет перехеширования")
    @DisplayName("Таблица перехешируется при переполнении корзин")
    @Test
    @Timeout(1)
    void rehashesWhenBucketsOverflow() {
        HashTable<Integer, Integer> table = new HashTable.Default<>(1);
        for (int i = 0; i < 32; i++) {
            table.put(i, i, new Counter.Default());
        }
        Counter counter = new Counter.Default();
        table.get(31, counter);
        assertThat("lookup walks the whole chain because there is no rehashing",
            Integer.parseInt(counter.toString()) < 32, is(true));
    }
}
