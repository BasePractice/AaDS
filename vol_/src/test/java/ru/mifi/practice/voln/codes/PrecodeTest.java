package ru.mifi.practice.voln.codes;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Precode в Raptor-коде")
final class PrecodeTest {

    @DisplayName("Число уравнений совпадает с числом проверочных символов")
    @Test
    void givesOneRelationPerParitySymbol() {
        RaptorConfiguration config = RaptorConfiguration.defaults(16, 12345L);
        assertThat("the number of relations dont match the number of parity symbols",
            Precode.relations(20, config).size(), is(config.parityCount()));
    }

    @DisplayName("Уравнение начинается со своего проверочного символа")
    @Test
    void startsEveryRelationWithItsOwnParitySymbol() {
        RaptorConfiguration config = RaptorConfiguration.defaults(16, 12345L);
        assertThat("a relation dont start with the parity symbol it defines",
            Precode.relations(20, config).get(0)[0], is(20));
    }

    @DisplayName("Правила детерминированы")
    @Test
    void repeatsTheSameRelations() {
        RaptorConfiguration config = RaptorConfiguration.defaults(16, 12345L);
        assertThat("the relations differ between two calls",
            Precode.relations(20, config).get(0), is(Precode.relations(20, config).get(0)));
    }

    /**
     * Раньше уравнения precode знал только кодировщик: проверочные символы добавляли декодеру
     * неизвестные, не добавляя ни одного уравнения, и восстановление требовало шестнадцати
     * символов вместо семи исходных. Теперь хватает ровно k — это теоретический минимум.
     */
    @DisplayName("Восстановление укладывается в число исходных символов")
    @Test
    void decodesWithoutPayingForTheParitySymbols() {
        assertThat("decoding needs more symbols than there are source symbols",
            symbolsNeeded(), lessThanOrEqualTo(sources()));
    }

    /**
     * Наличие ведущего элемента в столбце ещё не значит, что значение определено: если в той же
     * строке осталась единица в свободном столбце, ответ зависит от неизвестной величины.
     * Раньше декодер этого не проверял и на одном символе возвращал искажённые данные.
     */
    @DisplayName("Недоопределённая система отвергается, а не решается неверно")
    @Test
    void refusesAnUnderdeterminedSystem() {
        byte[] data = payload();
        RaptorConfiguration config = RaptorConfiguration.defaults(16, 12345L);
        RaptorEncoder encoder = RaptorEncoder.fromData(data, config);
        RaptorDecoder decoder = RaptorDecoder.create(config, encoder.k(), data.length);
        decoder.addSymbol(encoder.nextSymbol(0L));
        assertThrows(IllegalStateException.class, decoder::decode,
            "an underdetermined system is reported as solved");
    }

    @DisplayName("Восстановленные данные совпадают с исходными")
    @Test
    void restoresTheOriginalData() {
        byte[] data = payload();
        RaptorConfiguration config = RaptorConfiguration.defaults(16, 12345L);
        RaptorEncoder encoder = RaptorEncoder.fromData(data, config);
        RaptorDecoder decoder = RaptorDecoder.create(config, encoder.k(), data.length);
        for (long id = 0; id < 200; id++) {
            decoder.addSymbol(encoder.nextSymbol(id));
        }
        assertThat("the restored data differ from the original", decoder.decode(), is(data));
    }

    private static int symbolsNeeded() {
        byte[] data = payload();
        RaptorConfiguration config = RaptorConfiguration.defaults(16, 12345L);
        RaptorEncoder encoder = RaptorEncoder.fromData(data, config);
        RaptorDecoder decoder = RaptorDecoder.create(config, encoder.k(), data.length);
        for (long id = 0; id < 500; id++) {
            decoder.addSymbol(encoder.nextSymbol(id));
            if (restores(decoder, data)) {
                return (int) id + 1;
            }
        }
        throw new IllegalStateException("Декодирование не сошлось за 500 символов");
    }

    private static boolean restores(RaptorDecoder decoder, byte[] data) {
        try {
            return java.util.Arrays.equals(decoder.decode(), data);
        } catch (IllegalStateException notEnough) {
            return false;
        }
    }

    private static int sources() {
        return RaptorEncoder.fromData(payload(), RaptorConfiguration.defaults(16, 12345L)).k();
    }

    private static byte[] payload() {
        return "Алгоритмы и структуры данных: помехоустойчивое кодирование".getBytes(StandardCharsets.UTF_8);
    }
}
