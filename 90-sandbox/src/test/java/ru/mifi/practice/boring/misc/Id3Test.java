package ru.mifi.practice.boring.misc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

/**
 * Проверка на учебной таблице про игру в теннис: погода, влажность и ветер решают, играть или нет.
 */
@DisplayName("Дерево решений по алгоритму ID3")
final class Id3Test {

    @DisplayName("Дерево повторяет ответы обучающей таблицы")
    @Test
    @Timeout(1)
    void repeatsTheAnswersOfTheTable() {
        Id3 id3 = tennis();
        Id3.Node tree = id3.tree();
        int mismatches = 0;
        for (String[] row : rows()) {
            Map<String, String> values = Map.of("погода", row[0], "влажность", row[1], "ветер", row[2]);
            mismatches += id3.answer(tree, values).equals(row[3]) ? 0 : 1;
        }
        assertThat("the tree disagrees with the table it was built from", mismatches, is(0));
    }

    @DisplayName("Первым спрашивается признак с наибольшим приростом информации")
    @Test
    @Timeout(1)
    void asksTheMostInformativeFeatureFirst() {
        assertThat("the root asks about a less informative feature",
            tennis().tree().toString().startsWith("погода"), is(true));
    }

    @DisplayName("Однозначный признак делает лист сразу")
    @Test
    @Timeout(1)
    void makesALeafOfAnUnambiguousAnswer() {
        List<String[]> rows = List.of(
            new String[]{"дождь", "да"}, new String[]{"дождь", "да"});
        Id3 id3 = new Id3(rows, new String[]{"погода", "играем"}, 1);
        assertThat("the unambiguous table got a whole tree", id3.tree().size(), is(1));
    }

    @DisplayName("Дерево ветвится, когда ответы расходятся")
    @Test
    @Timeout(1)
    void branchesWhenTheAnswersDiffer() {
        assertThat("the tree of a mixed table is a single leaf", tennis().tree().size(), greaterThan(1));
    }

    @DisplayName("Незнакомое значение признака получает самый частый ответ")
    @Test
    @Timeout(1)
    void answersTheUnknownValueWithTheFrequentAnswer() {
        Id3 id3 = tennis();
        Map<String, String> values = Map.of("погода", "снег", "влажность", "низкая", "ветер", "нет");
        assertThat("the unknown value got no answer at all",
            id3.answer(id3.tree(), values).isEmpty(), is(false));
    }

    private static Id3 tennis() {
        return new Id3(rows(), new String[]{"погода", "влажность", "ветер", "играем"}, 3);
    }

    private static List<String[]> rows() {
        return List.of(
            new String[]{"солнце", "высокая", "нет", "нет"},
            new String[]{"солнце", "высокая", "да", "нет"},
            new String[]{"облачно", "высокая", "нет", "да"},
            new String[]{"дождь", "высокая", "нет", "да"},
            new String[]{"дождь", "низкая", "нет", "да"},
            new String[]{"дождь", "низкая", "да", "нет"},
            new String[]{"облачно", "низкая", "да", "да"},
            new String[]{"солнце", "высокая", "нет", "нет"},
            new String[]{"солнце", "низкая", "нет", "да"},
            new String[]{"дождь", "низкая", "нет", "да"},
            new String[]{"солнце", "низкая", "да", "да"},
            new String[]{"облачно", "высокая", "да", "да"},
            new String[]{"облачно", "низкая", "нет", "да"},
            new String[]{"дождь", "высокая", "да", "нет"});
    }
}
