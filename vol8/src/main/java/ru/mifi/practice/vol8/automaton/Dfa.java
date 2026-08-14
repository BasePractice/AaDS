package ru.mifi.practice.vol8.automaton;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Детерминированный конечный автомат: из состояния по символу ведёт не более одного перехода,
 * поэтому слово читается за один проход без возвратов и копий множеств.
 *
 * <p>Автомат неизменяем: переход добавляется методом with, который возвращает новый автомат.
 */
public final class Dfa implements Automaton {
    private final int start;
    private final Set<Integer> finals;
    private final Map<Integer, Map<Character, Integer>> moves;

    public Dfa(int start, Set<Integer> finals) {
        this(start, finals, Map.of());
    }

    private Dfa(int start, Set<Integer> finals, Map<Integer, Map<Character, Integer>> moves) {
        this.start = start;
        this.finals = Set.copyOf(finals);
        this.moves = Map.copyOf(moves);
    }

    public Dfa with(int from, char symbol, int to) {
        Map<Integer, Map<Character, Integer>> copy = new LinkedHashMap<>();
        moves.forEach((state, symbols) -> copy.put(state, new LinkedHashMap<>(symbols)));
        copy.computeIfAbsent(from, state -> new LinkedHashMap<>()).put(symbol, to);
        return new Dfa(start, finals, copy);
    }

    @Override
    public boolean accepts(String input) {
        int current = start;
        for (char symbol : input.toCharArray()) {
            Integer next = moves.getOrDefault(current, Map.of()).get(symbol);
            if (next == null) {
                return false;
            }
            current = next;
        }
        return finals.contains(current);
    }

    public int size() {
        return states().size();
    }

    /**
     * Минимальный автомат того же языка: недостижимые состояния выбрасываются, отсутствующие
     * переходы сводятся в ловушку, а неразличимые состояния склеиваются разбиением Хопкрофта.
     * Ответ полон — ловушка остаётся состоянием, как и требует определение минимального ДКА.
     */
    public Dfa minimal() {
        Dfa total = reachable().total();
        List<Set<Integer>> classes = new Partition(total.states(), total.finals, total.moves).classes();
        Map<Integer, Integer> names = total.names(classes);
        Dfa minimal = new Dfa(names.get(total.start), total.accepting(classes, names));
        for (Set<Integer> group : classes) {
            int state = group.iterator().next();
            for (Map.Entry<Character, Integer> move : total.moves.getOrDefault(state, Map.of()).entrySet()) {
                minimal = minimal.with(names.get(state), move.getKey(), names.get(move.getValue()));
            }
        }
        return minimal;
    }

    private Map<Integer, Integer> names(List<Set<Integer>> classes) {
        Map<Integer, Integer> names = new LinkedHashMap<>();
        for (Set<Integer> group : classes) {
            for (int state : group) {
                names.put(state, classes.indexOf(group));
            }
        }
        return names;
    }

    private Set<Integer> accepting(List<Set<Integer>> classes, Map<Integer, Integer> names) {
        Set<Integer> accepting = new LinkedHashSet<>();
        for (Set<Integer> group : classes) {
            if (group.stream().anyMatch(finals::contains)) {
                accepting.add(names.get(group.iterator().next()));
            }
        }
        return accepting;
    }

    private Dfa reachable() {
        Set<Integer> reached = new LinkedHashSet<>(Set.of(start));
        Deque<Integer> pending = new ArrayDeque<>(reached);
        while (!pending.isEmpty()) {
            for (int next : moves.getOrDefault(pending.poll(), Map.of()).values()) {
                if (reached.add(next)) {
                    pending.add(next);
                }
            }
        }
        Map<Integer, Map<Character, Integer>> kept = new LinkedHashMap<>();
        reached.stream().filter(moves::containsKey).forEach(state -> kept.put(state, moves.get(state)));
        Set<Integer> accepting = new LinkedHashSet<>(finals);
        accepting.retainAll(reached);
        return new Dfa(start, accepting, kept);
    }

    private Dfa total() {
        if (complete()) {
            return this;
        }
        Set<Character> alphabet = alphabet();
        Set<Integer> states = states();
        int trap = states.stream().mapToInt(Integer::intValue).max().orElse(0) + 1;
        Dfa total = trapping(trap, alphabet);
        for (int state : states) {
            for (char symbol : alphabet) {
                if (!moves.getOrDefault(state, Map.of()).containsKey(symbol)) {
                    total = total.with(state, symbol, trap);
                }
            }
        }
        return total;
    }

    private boolean complete() {
        return states().stream()
            .allMatch(state -> moves.getOrDefault(state, Map.of()).keySet().containsAll(alphabet()));
    }

    private Dfa trapping(int trap, Set<Character> alphabet) {
        Dfa trapped = this;
        for (char symbol : alphabet) {
            trapped = trapped.with(trap, symbol, trap);
        }
        return trapped;
    }

    private Set<Character> alphabet() {
        Set<Character> alphabet = new LinkedHashSet<>();
        moves.values().forEach(symbols -> alphabet.addAll(symbols.keySet()));
        return alphabet;
    }

    private Set<Integer> states() {
        Set<Integer> states = new LinkedHashSet<>(Set.of(start));
        states.addAll(finals);
        moves.forEach((state, symbols) -> {
            states.add(state);
            states.addAll(symbols.values());
        });
        return states;
    }

    @Override
    public String toString() {
        return "ДКА: старт " + start + ", допускающие " + finals + ", переходы " + moves;
    }
}
