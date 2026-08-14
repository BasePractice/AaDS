package ru.mifi.practice.vol8.automaton;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.IntPredicate;

/**
 * Разбиение состояний детерминированного автомата на классы неразличимых — алгоритм Хопкрофта.
 * Начальное разбиение грубое: допускающие отдельно от остальных. Дальше берётся класс-разделитель
 * и по каждому символу разрезает те классы, часть которых в него ведёт, а часть нет: раз слово
 * различает состояния, они не могут остаться в одном классе.
 *
 * <p>В очередь кладётся меньшая половина разреза — на этом и держится оценка O(n log n) вместо
 * O(n²) у наивного алгоритма Мура: каждое состояние попадает в очередь не чаще log n раз.
 * Автомат должен быть полным, иначе отсутствующий переход не отличить от перехода в ловушку.
 */
final class Partition {
    private final Set<Integer> states;
    private final Set<Integer> finals;
    private final Map<Integer, Map<Character, Integer>> moves;

    Partition(Set<Integer> states, Set<Integer> finals, Map<Integer, Map<Character, Integer>> moves) {
        this.states = Set.copyOf(states);
        this.finals = Set.copyOf(finals);
        this.moves = Map.copyOf(moves);
    }

    List<Set<Integer>> classes() {
        List<Set<Integer>> classes = new ArrayList<>();
        Deque<Set<Integer>> pending = new ArrayDeque<>();
        for (Set<Integer> group : List.of(kept(finals::contains), kept(state -> !finals.contains(state)))) {
            if (!group.isEmpty()) {
                classes.add(group);
                pending.add(group);
            }
        }
        while (!pending.isEmpty()) {
            Set<Integer> separator = pending.poll();
            for (char symbol : alphabet()) {
                refine(classes, pending, sources(separator, symbol));
            }
        }
        return classes;
    }

    private void refine(List<Set<Integer>> classes, Deque<Set<Integer>> pending, Set<Integer> sources) {
        for (Set<Integer> group : new ArrayList<>(classes)) {
            Set<Integer> inside = intersection(group, sources);
            Set<Integer> outside = difference(group, sources);
            if (!inside.isEmpty() && !outside.isEmpty()) {
                classes.set(classes.indexOf(group), inside);
                classes.add(outside);
                enqueue(pending, group, inside, outside);
            }
        }
    }

    private void enqueue(Deque<Set<Integer>> pending, Set<Integer> group, Set<Integer> inside, Set<Integer> outside) {
        if (pending.remove(group)) {
            pending.add(inside);
            pending.add(outside);
        } else {
            pending.add(inside.size() <= outside.size() ? inside : outside);
        }
    }

    private Set<Integer> sources(Set<Integer> target, char symbol) {
        Set<Integer> sources = new LinkedHashSet<>();
        for (int state : states) {
            if (target.contains(moves.getOrDefault(state, Map.of()).getOrDefault(symbol, -1))) {
                sources.add(state);
            }
        }
        return sources;
    }

    private Set<Character> alphabet() {
        Set<Character> alphabet = new LinkedHashSet<>();
        moves.values().forEach(symbols -> alphabet.addAll(symbols.keySet()));
        return alphabet;
    }

    private Set<Integer> kept(IntPredicate rule) {
        Set<Integer> group = new LinkedHashSet<>();
        for (int state : states) {
            if (rule.test(state)) {
                group.add(state);
            }
        }
        return group;
    }

    private Set<Integer> intersection(Set<Integer> group, Set<Integer> other) {
        Set<Integer> common = new LinkedHashSet<>(group);
        common.retainAll(other);
        return common;
    }

    private Set<Integer> difference(Set<Integer> group, Set<Integer> other) {
        Set<Integer> rest = new LinkedHashSet<>(group);
        rest.removeAll(other);
        return rest;
    }
}
