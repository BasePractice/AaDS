package ru.mifi.practice.vol8.automaton;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Детерминированный автомат")
final class DfaTest {

    @DisplayName("Принимает слово своего языка")
    @Test
    @Timeout(1)
    void acceptsTheWordOfItsLanguage() {
        assertThat("the automaton rejects the non empty word of the letter a",
            oneOrMoreA().accepts("aaa"), is(true));
    }

    @DisplayName("Отвергает пустое слово, если начальное состояние не допускающее")
    @Test
    @Timeout(1)
    void rejectsTheEmptyWord() {
        assertThat("the automaton accepts the empty word", oneOrMoreA().accepts(""), is(false));
    }

    @DisplayName("Минимизация сохраняет язык")
    @Test
    @Timeout(5)
    void keepsTheLanguageOnMinimizing() {
        assertThat("the minimal automaton disagrees with the source one",
            new Language(oneOrMoreA(), "ab", 6).disagreements(oneOrMoreA().minimal()), is(0));
    }

    @DisplayName("Минимизация склеивает неразличимые состояния")
    @Test
    @Timeout(1)
    void collapsesIndistinguishableStates() {
        assertThat("the minimal automaton keeps the indistinguishable states apart",
            oneOrMoreA().minimal().size(), is(2));
    }

    @DisplayName("Минимизация выбрасывает недостижимые состояния")
    @Test
    @Timeout(1)
    void dropsUnreachableStates() {
        assertThat("the minimal automaton keeps a state no word leads to",
            oneOrMoreA().with(7, 'a', 7).minimal().size(), is(2));
    }

    @DisplayName("Минимизация оставляет минимальный автомат как есть")
    @Test
    @Timeout(1)
    void leavesTheMinimalAutomatonAlone() {
        assertThat("the minimal automaton shrinks on the second minimizing",
            oneOrMoreA().minimal().minimal().size(), is(2));
    }

    @DisplayName("Ловушка добавляется, когда переход по символу алфавита отсутствует")
    @Test
    @Timeout(1)
    void addsTheTrapForTheMissingMove() {
        assertThat("the minimal automaton leaves the move undefined",
            new Dfa(0, Set.of(1)).with(0, 'a', 1).with(1, 'b', 1).minimal().size(), is(3));
    }

    private static Dfa oneOrMoreA() {
        return new Dfa(0, Set.of(1, 2))
            .with(0, 'a', 1)
            .with(1, 'a', 2)
            .with(2, 'a', 2);
    }
}
