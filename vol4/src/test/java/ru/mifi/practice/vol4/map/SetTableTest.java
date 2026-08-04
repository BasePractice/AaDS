package ru.mifi.practice.vol4.map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import ru.mifi.practice.commons.Counter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Проверка множества поверх хеш-таблицы.
 *
 * <p>Ёмкость берётся с запасом: перехеширование в таблице не сделано намеренно и оставлено
 * учебным заданием, поэтому проверки множества не должны на него натыкаться.
 */
@DisplayName("Множество на хеш-таблице")
final class SetTableTest {

    @DisplayName("Новое множество пусто")
    @Test
    @Timeout(1)
    void startsEmpty() {
        assertThat("a fresh set is not empty", new SetTable.Default<String>(64).size(), is(0));
    }

    @DisplayName("Добавленный элемент находится")
    @Test
    @Timeout(1)
    void findsTheAddedElement() {
        SetTable<String> set = new SetTable.Default<>(64);
        set.put("алгоритм", true, Counter.create());
        assertThat("the added element is not found",
            set.get("алгоритм", Counter.create()).orElseThrow(), is(true));
    }

    @DisplayName("Чужой элемент не находится")
    @Test
    @Timeout(1)
    void findsNothingForAForeignElement() {
        SetTable<String> set = new SetTable.Default<>(64);
        set.put("алгоритм", true, Counter.create());
        assertThat("a foreign element is found",
            set.get("структура", Counter.create()).isPresent(), is(false));
    }

    @DisplayName("Повторное добавление не увеличивает размер")
    @Test
    @Timeout(1)
    void keepsTheSizeOnARepeatedAdd() {
        SetTable<String> set = new SetTable.Default<>(64);
        set.put("алгоритм", true, Counter.create());
        set.put("алгоритм", true, Counter.create());
        assertThat("a repeated add grows the set", set.size(), is(1));
    }

    @DisplayName("Удаление убирает элемент из множества")
    @Test
    @Timeout(1)
    void removesTheElement() {
        SetTable<String> set = new SetTable.Default<>(64);
        set.put("алгоритм", true, Counter.create());
        set.remove("алгоритм", Counter.create());
        assertThat("removing dont take the element out",
            set.get("алгоритм", Counter.create()).isPresent(), is(false));
    }

    @DisplayName("Очистка опустошает множество")
    @Test
    @Timeout(1)
    void emptiesOnClear() {
        SetTable<String> set = new SetTable.Default<>(64);
        set.put("алгоритм", true, Counter.create());
        set.put("структура", true, Counter.create());
        set.clear();
        assertThat("clearing dont empty the set", set.size(), is(0));
    }

    @DisplayName("Разные элементы живут в множестве вместе")
    @Test
    @Timeout(1)
    void holdsDifferentElementsTogether() {
        SetTable<String> set = new SetTable.Default<>(64);
        set.put("алгоритм", true, Counter.create());
        set.put("структура", true, Counter.create());
        assertThat("different elements dont live together", set.size(), is(2));
    }
}
