package ru.mifi.practice.vol9;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Код Грея")
final class GrayCodeTest {

    @DisplayName("Код Грея из трёх битов идёт в отражённом порядке")
    @Test
    @Timeout(1)
    void generatesReflectedGrayCode() {
        assertThat("the gray code dont follow the reflected order",
            texts(3), contains("000", "001", "011", "010", "110", "111", "101", "100"));
    }

    @DisplayName("Количество кодов равно двум в степени битов")
    @Test
    @Timeout(1)
    void generatesPowerOfTwoCodes() {
        assertThat("the number of gray codes dont equal two to the bits", collect(4).size(), is(16));
    }

    @DisplayName("Соседние коды отличаются одним битом")
    @Test
    @Timeout(1)
    void changesOneBitBetweenCodes() {
        List<int[]> codes = collect(4);
        int distant = 0;
        for (int i = 1; i < codes.size(); i++) {
            if (distance(codes.get(i - 1), codes.get(i)) != 1) {
                ++distant;
            }
        }
        assertThat("consecutive gray codes dont differ by one bit", distant, is(0));
    }

    @DisplayName("Код без битов содержит один пустой набор")
    @Test
    @Timeout(1)
    void generatesOneCodeWithoutBits() {
        assertThat("zero bits dont produce exactly one code", collect(0).size(), is(1));
    }

    @DisplayName("Отрицательное количество битов отвергается")
    @Test
    @Timeout(1)
    void cannotGenerateNegativeBits() {
        assertThrows(IllegalArgumentException.class, () -> GrayCode.of(-1),
            "a negative number of bits is accepted");
    }

    private static List<String> texts(int bits) {
        List<String> texts = new ArrayList<>();
        for (int[] code : GrayCode.of(bits)) {
            StringBuilder text = new StringBuilder(code.length);
            for (int bit : code) {
                text.append(bit);
            }
            texts.add(text.toString());
        }
        return texts;
    }

    private static List<int[]> collect(int bits) {
        List<int[]> codes = new ArrayList<>();
        for (int[] code : GrayCode.of(bits)) {
            codes.add(code);
        }
        return codes;
    }

    private static int distance(int[] left, int[] right) {
        int distance = 0;
        for (int i = 0; i < left.length; i++) {
            if (left[i] != right[i]) {
                ++distance;
            }
        }
        return distance;
    }
}
