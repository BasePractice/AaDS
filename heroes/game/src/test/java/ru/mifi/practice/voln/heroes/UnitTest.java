package ru.mifi.practice.voln.heroes;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/** Проверка запроса потерь: тактика считает исход удара, не нанося его. */
@DisplayName("Стек бойцов")
final class UnitTest {

    @DisplayName("Удар слабее защиты никого не убивает")
    @Test
    @Timeout(1)
    void spillsNobodyWhenTheHitIsWeakerThanTheDefense() {
        Unit.Stack stack = new Unit.Stack(Unit.Type.WALKER);
        stack.add(new Unit(10, 50, 100, 5));
        assertThat("a hit weaker than the defense still kills", stack.casualties(40), is(0));
    }

    @DisplayName("Удара по здоровью хватает ровно на одного бойца")
    @Test
    @Timeout(1)
    void killsExactlyOneUnit() {
        Unit.Stack stack = new Unit.Stack(Unit.Type.WALKER);
        stack.add(new Unit(10, 0, 50, 5));
        stack.add(new Unit(10, 0, 50, 5));
        assertThat("a hit worth one unit health kills a different number of units",
            stack.casualties(50), is(1));
    }

    @DisplayName("Мощный удар выкашивает весь стек")
    @Test
    @Timeout(1)
    void killsTheWholeStack() {
        Unit.Stack stack = new Unit.Stack(Unit.Type.WALKER);
        stack.add(new Unit(10, 0, 50, 5));
        stack.add(new Unit(10, 0, 50, 5));
        assertThat("a hit worth the whole stack health leaves survivors", stack.casualties(100), is(2));
    }

    @DisplayName("Запрос потерь не трогает стек")
    @Test
    @Timeout(1)
    void keepsTheStackUntouched() {
        Unit.Stack stack = new Unit.Stack(Unit.Type.WALKER);
        stack.add(new Unit(10, 0, 50, 5));
        stack.casualties(1000);
        assertThat("asking about casualties destroys the stack", stack.size(), is(1));
    }
}
