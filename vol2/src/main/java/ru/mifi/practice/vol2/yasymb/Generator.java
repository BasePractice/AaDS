package ru.mifi.practice.vol2.yasymb;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public final class Generator {
    private final Random random = new Random();
    private final int xSize;
    private final int ySize;

    public Generator(int xSize, int ySize) {
        this.xSize = xSize;
        this.ySize = ySize;
    }

    public static void main(String[] args) {
        Result result = new Generator(4, 4).generate();
        System.out.println(result);
    }

    private static void exchange(String text, int i, Map<Integer, Character> characters, Deque<Character> symbols, StringBuilder exchange) {
        if (text.length() > i) {
            char n = text.charAt(i);
            Character c = characters.get((int) (n - '0'));
            if (c == null) {
                Character ch = symbols.pop();
                exchange.append(ch);
                characters.put(n - '0', ch);
            } else {
                exchange.append(c);
            }
        }
    }

    public Result generate() {
        int xFactor = 1;
        for (int i = 0; i < xSize - 1; i++) {
            xFactor *= 10;
        }
        int yFactor = 1;
        for (int i = 0; i < ySize - 1; i++) {
            yFactor *= 10;
        }
        int x = random.nextInt(xFactor + 1) + xFactor;
        int y = random.nextInt(yFactor + 1) + xFactor;

        Map<Integer, Character> characters = new HashMap<>();
        Deque<Character> symbols = new ArrayDeque<>();
        for (int i = 'a'; i < 'z'; i++) {
            symbols.add((char) i);
        }
        String xT = String.valueOf(x);
        String yT = String.valueOf(y);
        String zT = String.valueOf(x + y);
        int max = Math.max(xT.length(), Math.max(yT.length(), zT.length()));
        StringBuilder xR = new StringBuilder();
        StringBuilder yR = new StringBuilder();
        StringBuilder zR = new StringBuilder();
        for (int i = 0; i < max; i++) {
            exchange(xT, i, characters, symbols, xR);
            exchange(yT, i, characters, symbols, yR);
            exchange(zT, i, characters, symbols, zR);
        }

        return new Result(new Equation(xT, yT, zT), new Equation(xR.toString(), yR.toString(), zR.toString()));
    }

    public record Result(Equation math, Equation letters) {
    }

    public record Equation(String x, String y, String z) {
        @Override
        public String toString() {
            return x + " + " + y + " = " + z;
        }
    }
}
