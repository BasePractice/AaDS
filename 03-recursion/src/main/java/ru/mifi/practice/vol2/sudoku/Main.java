package ru.mifi.practice.vol2.sudoku;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class Main {
    private static final int SIZE = 9;

    public static void main(String[] args) throws IOException, URISyntaxException {
        Path path = Path.of(Objects.requireNonNull(Main.class.getResource("/sudoku.sud")).toURI());
        Sudoku.Factory factory = Sudoku.recursionFactory(false);
        int maxIt = 0;
        int maxIndex = 0;
        int index = 0;
        for (int[][] block : blocks(Files.readAllLines(path))) {
            ++index;
            int it = solve(factory, block, index);
            if (maxIt < it) {
                maxIt = it;
                maxIndex = index;
            }
        }
        System.out.println();
        System.out.printf("%2d: %d%n", maxIndex, maxIt);
    }

    static List<int[][]> blocks(List<String> lines) {
        List<int[][]> blocks = new ArrayList<>();
        int[][] block = new int[SIZE][SIZE];
        int row = 0;
        for (String line : lines) {
            if (line.length() < SIZE) {
                continue;
            }
            for (int i = 0; i < SIZE; i++) {
                block[row][i] = Integer.parseInt(String.valueOf(line.charAt(i)));
            }
            ++row;
            if (row == SIZE) {
                blocks.add(block);
                block = new int[SIZE][SIZE];
                row = 0;
            }
        }
        return blocks;
    }

    private static int solve(Sudoku.Factory factory, int[][] block, int index) {
        Sudoku sudoku = factory.create(block.length, block);
        sudoku.print();
        System.out.println();
        System.out.println();
        sudoku.solve();
        sudoku.print();
        int it = sudoku.iterations();
        System.out.printf("%2d ----%9d----%n", index, it);
        sudoku.clear();
        return it;
    }
}
