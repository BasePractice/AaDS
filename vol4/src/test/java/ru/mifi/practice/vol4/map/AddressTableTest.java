package ru.mifi.practice.vol4.map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import ru.mifi.practice.commons.Counter;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Хеш-таблица с открытой адресацией")
final class AddressTableTest {

    @DisplayName("Возвращает положенное значение")
    @Test
    @Timeout(1)
    void givesBackTheStoredValue() {
        AddressTable<String, Integer> table = new AddressTable<>(8);
        table.put("a", 1, Counter.create());
        assertThat("the stored value is lost", table.get("a", Counter.create()), is(Optional.of(1)));
    }

    @DisplayName("Заменяет значение по тому же ключу, а не заводит второе")
    @Test
    @Timeout(1)
    void replacesTheValueOfTheSameKey() {
        AddressTable<String, Integer> table = new AddressTable<>(8);
        table.put("a", 1, Counter.create());
        table.put("a", 2, Counter.create());
        assertThat("the same key is stored twice", table.size(), is(1));
    }

    @DisplayName("Находит ключ, который сел на занятое место")
    @Test
    @Timeout(1)
    void findsTheKeyBehindACollision() {
        AddressTable<String, Integer> table = collided();
        table.put("a", 1, Counter.create());
        table.put("b", 2, Counter.create());
        assertThat("the key pushed aside by a collision is lost",
            table.get("b", Counter.create()), is(Optional.of(2)));
    }

    @DisplayName("Не находит удалённый ключ")
    @Test
    @Timeout(1)
    void forgetsTheRemovedKey() {
        AddressTable<String, Integer> table = new AddressTable<>(8);
        table.put("a", 1, Counter.create());
        table.remove("a", Counter.create());
        assertThat("the removed key is still found", table.get("a", Counter.create()), is(Optional.empty()));
    }

    @DisplayName("Находит ключ за надгробием удалённого соседа")
    @Test
    @Timeout(1)
    void findsTheKeyBehindATombstone() {
        AddressTable<String, Integer> table = collided();
        table.put("a", 1, Counter.create());
        table.put("b", 2, Counter.create());
        table.remove("a", Counter.create());
        assertThat("the tombstone cuts the probing short",
            table.get("b", Counter.create()), is(Optional.of(2)));
    }

    @DisplayName("Кладёт на освободившееся место, а не в конец цепочки проб")
    @Test
    @Timeout(1)
    void reusesTheTombstone() {
        AddressTable<String, Integer> table = collided();
        table.put("a", 1, Counter.create());
        table.put("b", 2, Counter.create());
        table.remove("a", Counter.create());
        table.put("c", 3, Counter.create());
        assertThat("the freed slot is left unused", table.size(), is(2));
    }

    @DisplayName("Переносит записи в больший массив, когда становится тесно")
    @Test
    @Timeout(1)
    void rehashesWhenItGetsCrowded() {
        AddressTable<Integer, Integer> table = new AddressTable<>(2);
        for (int key = 0; key < 100; key++) {
            table.put(key, key * 10, Counter.create());
        }
        assertThat("the rehashing lost a key", table.get(77, Counter.create()), is(Optional.of(770)));
    }

    @DisplayName("Считает записи после перехеширования")
    @Test
    @Timeout(1)
    void countsTheEntriesAfterRehashing() {
        AddressTable<Integer, Integer> table = new AddressTable<>(2);
        for (int key = 0; key < 100; key++) {
            table.put(key, key * 10, Counter.create());
        }
        assertThat("the size dont survive the rehashing", table.size(), is(100));
    }

    @DisplayName("Пустеет после очистки")
    @Test
    @Timeout(1)
    void emptiesOnClear() {
        AddressTable<String, Integer> table = new AddressTable<>(8);
        table.put("a", 1, Counter.create());
        table.clear();
        assertThat("the cleared table keeps its entries", table.isEmpty(), is(true));
    }

    @DisplayName("Проба стоит шага счётчика")
    @Test
    @Timeout(1)
    void countsEveryProbe() {
        AddressTable<String, Integer> table = collided();
        table.put("a", 1, Counter.create());
        table.put("b", 2, Counter.create());
        Counter counter = Counter.create();
        table.get("b", counter);
        assertThat("the probes are counted wrong", counter.count(), is(2));
    }

    private static AddressTable<String, Integer> collided() {
        return new AddressTable<>(8, (key, mod) -> 0, Object::equals);
    }
}
