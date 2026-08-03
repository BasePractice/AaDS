package ru.mifi.practice.vol2.sudoku;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Чтение задач судоку")
final class SudokuBlocksTest {

    @DisplayName("Читает все задачи из файла, включая последнюю")
    @Test
    void readsEveryPuzzleIncludingTheLast() throws IOException {
        assertThat("the puzzle without a trailing separator is lost", Main.blocks(sudoku()).size(), is(50));
    }

    @DisplayName("Пропускает строки-разделители")
    @Test
    void skipsSeparatorLines() throws IOException {
        assertThat("separator lines leak into a puzzle",
            Main.blocks(List.of("", "   ", "000000000")).size(), is(0));
    }

    @DisplayName("Разбирает цифры задачи по позициям")
    @Test
    void readsDigitsByPosition() {
        List<String> lines = Collections.nCopies(9, "123456789");
        assertThat("digit dont land on its own position", Main.blocks(lines).get(0)[0][4], is(5));
    }

    private static List<String> sudoku() throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
            Objects.requireNonNull(SudokuBlocksTest.class.getResourceAsStream("/sudoku.sud")),
            StandardCharsets.UTF_8))) {
            return reader.lines().toList();
        }
    }
}
