package ru.mifi.practice.vol2.recurs;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

@DisplayName("Ханойские башни")
final class HanoiTest {

    @DisplayName("Один диск переносится одним ходом")
    @Test
    @Timeout(1)
    void movesTheSingleDiskAtOnce() {
        assertThat("the single disk takes more than one move",
            new Hanoi(1).moves(), contains(new Hanoi.Move(1, 'a', 'c')));
    }

    @DisplayName("Пустая башня не требует ходов")
    @Test
    @Timeout(1)
    void movesNothingWhenThereIsNoDisk() {
        assertThat("the empty tower takes a move", new Hanoi(0).moves().size(), is(0));
    }

    @DisplayName("Башня из n дисков стоит 2 в степени n без единицы ходов")
    @Test
    @Timeout(1)
    void costsExponentiallyManyMoves() {
        assertThat("the tower of ten disks takes other than 1023 moves",
            new Hanoi(10).moves().size(), is(1023));
    }

    @DisplayName("Больший диск никогда не ложится на меньший")
    @Test
    @Timeout(1)
    void neverPutsALargerDiskOnASmallerOne() {
        assertThat("a larger disk lands on a smaller one", violations(new Hanoi(8)), is(0));
    }

    @DisplayName("Вся башня оказывается на целевом стержне")
    @Test
    @Timeout(1)
    void bringsTheWholeTowerToTheTarget() {
        assertThat("the tower dont end up on the target rod", played(new Hanoi(8)).get('c').size(), is(8));
    }

    private static int violations(Hanoi hanoi) {
        Map<Character, Deque<Integer>> rods = rods(hanoi.size());
        int violations = 0;
        for (Hanoi.Move move : hanoi.moves()) {
            int disk = rods.get(move.from()).pop();
            Deque<Integer> target = rods.get(move.to());
            if (!target.isEmpty() && target.peek() < disk) {
                ++violations;
            }
            target.push(disk);
        }
        return violations;
    }

    private static Map<Character, Deque<Integer>> played(Hanoi hanoi) {
        Map<Character, Deque<Integer>> rods = rods(hanoi.size());
        for (Hanoi.Move move : hanoi.moves()) {
            rods.get(move.to()).push(rods.get(move.from()).pop());
        }
        return rods;
    }

    private static Map<Character, Deque<Integer>> rods(int size) {
        Map<Character, Deque<Integer>> rods = new HashMap<>();
        rods.put('a', new ArrayDeque<>());
        rods.put('b', new ArrayDeque<>());
        rods.put('c', new ArrayDeque<>());
        for (int disk = size; disk > 0; disk--) {
            rods.get('a').push(disk);
        }
        return rods;
    }
}
