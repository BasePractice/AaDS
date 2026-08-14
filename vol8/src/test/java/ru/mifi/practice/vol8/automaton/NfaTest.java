package ru.mifi.practice.vol8.automaton;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Недетерминированный автомат")
final class NfaTest {

    @DisplayName("Принимает слово своего языка")
    @Test
    @Timeout(1)
    void acceptsTheWordOfItsLanguage() {
        assertThat("the automaton rejects the word ending with abb",
            endsWithAbb().accepts("aababb"), is(true));
    }

    @DisplayName("Отвергает слово вне своего языка")
    @Test
    @Timeout(1)
    void rejectsTheWordOutsideItsLanguage() {
        assertThat("the automaton accepts the word ending with aba",
            endsWithAbb().accepts("aabab"), is(false));
    }

    @DisplayName("Отвергает символ вне алфавита")
    @Test
    @Timeout(1)
    void rejectsTheSymbolOutsideTheAlphabet() {
        assertThat("the automaton accepts an unknown symbol",
            endsWithAbb().accepts("abbc"), is(false));
    }

    @DisplayName("Проходит пустые переходы")
    @Test
    @Timeout(1)
    void walksTheEmptyMoves() {
        assertThat("the automaton dont follow the epsilon moves",
            new Nfa(0, Set.of(2)).epsilon(0, 1).with(1, 'a', 2).accepts("a"), is(true));
    }

    @DisplayName("Принимает пустое слово, если пустые переходы ведут в допускающее состояние")
    @Test
    @Timeout(1)
    void acceptsTheEmptyWordThroughEmptyMoves() {
        assertThat("the epsilon closure of the start dont reach the accepting state",
            new Nfa(0, Set.of(1)).epsilon(0, 1).accepts(""), is(true));
    }

    @DisplayName("Детерминизация сохраняет язык")
    @Test
    @Timeout(5)
    void keepsTheLanguageOnDetermining() {
        assertThat("the determined automaton disagrees with the source one",
            new Language(endsWithAbb(), "ab", 6).disagreements(endsWithAbb().determined()), is(0));
    }

    @DisplayName("Детерминизация убирает пустые переходы")
    @Test
    @Timeout(5)
    void dropsTheEmptyMovesOnDetermining() {
        Nfa nfa = new Nfa(0, Set.of(2)).epsilon(0, 1).with(1, 'a', 2).with(2, 'a', 2);
        assertThat("the determined automaton disagrees with the source one",
            new Language(nfa, "a", 6).disagreements(nfa.determined()), is(0));
    }

    private static Nfa endsWithAbb() {
        return new Nfa(0, Set.of(3))
            .with(0, 'a', 0)
            .with(0, 'b', 0)
            .with(0, 'a', 1)
            .with(1, 'b', 2)
            .with(2, 'b', 3);
    }
}
