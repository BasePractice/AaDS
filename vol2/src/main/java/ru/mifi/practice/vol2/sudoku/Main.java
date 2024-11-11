package ru.mifi.practice.vol2.sudoku;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public abstract class Main {

    public static void main(String[] args) throws IOException, URISyntaxException {
        Path path = Path.of(Objects.requireNonNull(Main.class.getResource("/sudoku.sud")).toURI());
        List<String> lines = Files.readAllLines(path);
        int row = 0;
        int[][] block = new int[9][9];
        Sudoku.Factory factory = Sudoku.recursionFactory(false);
        for (String line : lines) {
            if (row == 9) {
                Sudoku sudoku = factory.create(block.length, block);
                sudoku.print();
                System.out.println();
                System.out.println();
                sudoku.solve();
                sudoku.print();
                row = 0;
                System.out.printf("------%7d-----%n", sudoku.iterations());
                sudoku.clear();
                continue;
            }
            for (int i = 0; i < 9; i++) {
                block[row][i] = Integer.parseInt(String.valueOf(line.charAt(i)));
            }
            ++row;
        }


    }
}
