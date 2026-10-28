package ru.mifi.practice.vol2.recurs;

import java.util.ArrayList;
import java.util.List;

/**
 * Ханойские башни: башню из n дисков нужно перенести на другой стержень, беря по одному диску и
 * никогда не кладя больший на меньший. Рекурсия здесь не приём, а формулировка задачи: перенести
 * n дисков — это перенести n−1 на запасной стержень, переложить самый большой и вернуть n−1
 * сверху. Отсюда ровно 2^n − 1 ходов, и меньшим числом обойтись нельзя.
 */
public final class Hanoi {
    private final int size;

    public Hanoi(int size) {
        this.size = size;
    }

    public List<Move> moves() {
        List<Move> moves = new ArrayList<>();
        move(size, 'a', 'c', 'b', moves);
        return moves;
    }

    public int size() {
        return size;
    }

    private void move(int disk, char from, char to, char spare, List<Move> moves) {
        if (disk == 0) {
            return;
        }
        move(disk - 1, from, spare, to, moves);
        moves.add(new Move(disk, from, to));
        move(disk - 1, spare, to, from, moves);
    }

    /** Ход: какой диск и с какого стержня на какой перекладывается. */
    public record Move(int disk, char from, char to) {
        @Override
        public String toString() {
            return disk + ": " + from + " -> " + to;
        }
    }
}
