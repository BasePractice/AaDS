package ru.mifi.practice.vol8.automaton;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Недетерминированный конечный автомат: из состояния по символу ведёт сколько угодно переходов,
 * а пустые переходы проходятся без символа вовсе. Слово принимается, если хотя бы один путь
 * доводит до допускающего состояния, поэтому проверка идёт множеством текущих состояний сразу.
 *
 * <p>Автомат неизменяем: переходы добавляются методами with и epsilon, каждый из которых
 * возвращает новый автомат.
 */
//TODO: Построить НКА по дереву регулярного выражения из пакета regexp конструкцией Томпсона — сейчас автомат задаётся переходами вручную
public final class Nfa implements Automaton {
    private final int start;
    private final Set<Integer> finals;
    private final Map<Integer, Map<Character, Set<Integer>>> moves;
    private final Map<Integer, Set<Integer>> epsilons;

    public Nfa(int start, Set<Integer> finals) {
        this(start, finals, Map.of(), Map.of());
    }

    private Nfa(int start,
                Set<Integer> finals,
                Map<Integer, Map<Character, Set<Integer>>> moves,
                Map<Integer, Set<Integer>> epsilons) {
        this.start = start;
        this.finals = Set.copyOf(finals);
        this.moves = Map.copyOf(moves);
        this.epsilons = Map.copyOf(epsilons);
    }

    public Nfa with(int from, char symbol, int to) {
        Map<Integer, Map<Character, Set<Integer>>> copy = new LinkedHashMap<>();
        moves.forEach((state, symbols) -> copy.put(state, new LinkedHashMap<>(symbols)));
        copy.computeIfAbsent(from, state -> new LinkedHashMap<>())
            .merge(symbol, Set.of(to), this::united);
        return new Nfa(start, finals, copy, epsilons);
    }

    public Nfa epsilon(int from, int to) {
        Map<Integer, Set<Integer>> copy = new LinkedHashMap<>(epsilons);
        copy.merge(from, Set.of(to), this::united);
        return new Nfa(start, finals, moves, copy);
    }

    @Override
    public boolean accepts(String input) {
        Set<Integer> current = closure(Set.of(start));
        for (char symbol : input.toCharArray()) {
            current = closure(moved(current, symbol));
            if (current.isEmpty()) {
                return false;
            }
        }
        return current.stream().anyMatch(finals::contains);
    }

    /**
     * Детерминизация подмножествами: состояние нового автомата — множество состояний старого,
     * замкнутое по пустым переходам. Множеств не больше 2^n, но на практике их считанные
     * единицы, потому что недостижимые сочетания просто не появляются в очереди.
     */
    public Dfa determined() {
        Map<Set<Integer>, Integer> names = new LinkedHashMap<>();
        Map<Integer, Map<Character, Integer>> table = new LinkedHashMap<>();
        Set<Integer> accepting = new LinkedHashSet<>();
        Deque<Set<Integer>> pending = new ArrayDeque<>();
        Set<Integer> first = closure(Set.of(start));
        name(first, names, accepting);
        pending.add(first);
        while (!pending.isEmpty()) {
            Set<Integer> group = pending.poll();
            for (char symbol : alphabet(group)) {
                Set<Integer> next = closure(moved(group, symbol));
                if (!names.containsKey(next)) {
                    name(next, names, accepting);
                    pending.add(next);
                }
                table.computeIfAbsent(names.get(group), state -> new LinkedHashMap<>())
                    .put(symbol, names.get(next));
            }
        }
        return assembled(table, accepting);
    }

    private void name(Set<Integer> group, Map<Set<Integer>, Integer> names, Set<Integer> accepting) {
        int name = names.size();
        names.put(group, name);
        if (group.stream().anyMatch(finals::contains)) {
            accepting.add(name);
        }
    }

    private Dfa assembled(Map<Integer, Map<Character, Integer>> table, Set<Integer> accepting) {
        Dfa dfa = new Dfa(0, accepting);
        for (Map.Entry<Integer, Map<Character, Integer>> row : table.entrySet()) {
            for (Map.Entry<Character, Integer> move : row.getValue().entrySet()) {
                dfa = dfa.with(row.getKey(), move.getKey(), move.getValue());
            }
        }
        return dfa;
    }

    private Set<Character> alphabet(Set<Integer> group) {
        Set<Character> alphabet = new LinkedHashSet<>();
        for (int state : group) {
            alphabet.addAll(moves.getOrDefault(state, Map.of()).keySet());
        }
        return alphabet;
    }

    private Set<Integer> moved(Set<Integer> group, char symbol) {
        Set<Integer> next = new LinkedHashSet<>();
        for (int state : group) {
            next.addAll(moves.getOrDefault(state, Map.of()).getOrDefault(symbol, Set.of()));
        }
        return next;
    }

    private Set<Integer> closure(Set<Integer> group) {
        Set<Integer> closed = new LinkedHashSet<>(group);
        Deque<Integer> pending = new ArrayDeque<>(group);
        while (!pending.isEmpty()) {
            for (int next : epsilons.getOrDefault(pending.poll(), Set.of())) {
                if (closed.add(next)) {
                    pending.add(next);
                }
            }
        }
        return closed;
    }

    private Set<Integer> united(Set<Integer> left, Set<Integer> right) {
        Set<Integer> union = new LinkedHashSet<>(left);
        union.addAll(right);
        return union;
    }

    @Override
    public String toString() {
        return "НКА: старт " + start + ", допускающие " + new HashSet<>(finals)
            + ", переходы " + moves + ", пустые переходы " + epsilons;
    }
}
