package ru.mifi.practice.vol8.regexp.machine;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import ru.mifi.practice.vol8.regexp.tree.Tree;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Классы символов в регулярном выражении")
final class CharacterClassTest {

    @DisplayName("Точка принимает любой символ")
    @Test
    @Timeout(5)
    void letsTheDotAcceptAnySymbol() {
        assertThat("the dot dont accept an arbitrary symbol", matches("a.c", "abc"), is(true));
    }

    @DisplayName("Точка принимает ровно один символ")
    @Test
    @Timeout(5)
    void letsTheDotAcceptExactlyOneSymbol() {
        assertThat("the dot swallows more than one symbol", matches("a.c", "abbc"), is(false));
    }

    @DisplayName("Точка требует символ")
    @Test
    @Timeout(5)
    void makesTheDotRequireASymbol() {
        assertThat("the dot matches an absent symbol", matches("a.c", "ac"), is(false));
    }

    @DisplayName("Диапазон принимает символ из середины")
    @Test
    @Timeout(5)
    void letsARangeAcceptASymbolFromTheMiddle() {
        assertThat("a range dont accept a symbol between its ends", matches("[a-e]", "c"), is(true));
    }

    @DisplayName("Диапазон принимает свои границы")
    @Test
    @Timeout(5)
    void letsARangeAcceptItsOwnEnds() {
        assertThat("a range dont accept its own end", matches("[a-e]", "e"), is(true));
    }

    @DisplayName("Диапазон отвергает символ за границей")
    @Test
    @Timeout(5)
    void makesARangeRejectASymbolOutside() {
        assertThat("a range accepts a symbol outside it", matches("[a-e]", "z"), is(false));
    }

    @DisplayName("Отрицание множества отвергает перечисленные символы")
    @Test
    @Timeout(5)
    void makesANegatedSetRejectItsOwnSymbols() {
        assertThat("a negated set accepts the symbol it lists", matches("[^of]", "o"), is(false));
    }

    @DisplayName("Отрицание множества принимает остальные символы")
    @Test
    @Timeout(5)
    void letsANegatedSetAcceptOtherSymbols() {
        assertThat("a negated set rejects a symbol it dont list", matches("[^of]", "x"), is(true));
    }

    @DisplayName("Отрицание диапазона отвергает символ из диапазона")
    @Test
    @Timeout(5)
    void makesANegatedRangeRejectASymbolInside() {
        assertThat("a negated range accepts a symbol inside it", matches("[^a-e]", "c"), is(false));
    }

    @DisplayName("Отрицание диапазона принимает символ за диапазоном")
    @Test
    @Timeout(5)
    void letsANegatedRangeAcceptASymbolOutside() {
        assertThat("a negated range rejects a symbol outside it", matches("[^a-e]", "z"), is(true));
    }

    @DisplayName("Экранированная точка принимает саму точку")
    @Test
    @Timeout(5)
    void letsAnEscapedDotAcceptTheDotItself() {
        assertThat("an escaped dot dont accept the dot", matches("a\\.c", "a.c"), is(true));
    }

    @DisplayName("Экранированная точка отвергает другой символ")
    @Test
    @Timeout(5)
    void makesAnEscapedDotRejectAnotherSymbol() {
        assertThat("an escaped dot behaves as a wildcard", matches("a\\.c", "abc"), is(false));
    }

    @DisplayName("Класс символов работает под звёздочкой")
    @Test
    @Timeout(5)
    void letsACharacterClassRepeat() {
        assertThat("a repeated character class dont match", matches("[a-c]+", "abcab"), is(true));
    }

    @DisplayName("Прежний набор шаблонов остаётся рабочим")
    @Test
    @Timeout(5)
    void keepsTheOldPatternWorking() {
        assertThat("the previously supported pattern stopped matching",
            matches("(a|b*(c?d)+|e)|(of|pt)", "bcddcd"), is(true));
    }

    private static boolean matches(String pattern, String input) {
        return new Matcher.Default(new Tree.Default(pattern)).match(input);
    }
}
