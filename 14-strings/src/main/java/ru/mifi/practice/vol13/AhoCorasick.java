package ru.mifi.practice.vol13;

import ru.mifi.practice.commons.Counter;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Поиск многих образцов сразу — алгоритм Ахо — Корасик. Образцы складываются в бор, и по нему
 * строится автомат: у каждого узла есть суффиксная ссылка на самый длинный собственный суффикс,
 * который тоже является префиксом какого-нибудь образца. Это та же префикс-функция Кнута —
 * Морриса — Пратта, только не для одного образца, а для всего набора сразу.
 *
 * <p>Текст читается один раз слева направо: на несовпадении автомат уходит по суффиксной ссылке,
 * а не возвращается по тексту. Поэтому поиск стоит O(n + сумма длин образцов + число вхождений),
 * а не n прогонов Кнута — Морриса — Пратта по разу на образец.
 *
 * <p>Счётчик отмечает прочитанные символы текста: их ровно столько, сколько в тексте, сколько бы
 * образцов ни искали.
 */
public final class AhoCorasick {
    private final Node root;
    private final List<String> patterns;

    public AhoCorasick(List<String> patterns) {
        if (patterns.stream().anyMatch(String::isEmpty)) {
            throw new IllegalArgumentException("Пустой образец совпадает с любой позицией текста");
        }
        this.patterns = List.copyOf(patterns);
        this.root = new Node(true);
        this.patterns.forEach(this::grow);
        link();
    }

    public Map<String, List<Integer>> all(String text, Counter counter) {
        Map<String, List<Integer>> found = new LinkedHashMap<>();
        patterns.forEach(pattern -> found.put(pattern, new ArrayList<>()));
        Node current = root;
        for (int i = 0; i < text.length(); i++) {
            counter.increment();
            current = next(current, text.charAt(i));
            for (String output : current.outputs) {
                found.get(output).add(i - output.length() + 1);
            }
        }
        return found;
    }

    private void grow(String pattern) {
        Node current = root;
        for (char symbol : pattern.toCharArray()) {
            if (!current.moves.containsKey(symbol)) {
                current.moves.put(symbol, new Node(false));
            }
            current = current.moves.get(symbol);
        }
        current.outputs.add(pattern);
    }

    private void link() {
        Deque<Node> pending = new ArrayDeque<>();
        root.fail = root;
        for (Node child : root.moves.values()) {
            child.fail = root;
            pending.add(child);
        }
        while (!pending.isEmpty()) {
            Node node = pending.poll();
            node.moves.forEach((symbol, child) -> {
                child.fail = next(node.fail, symbol);
                child.outputs.addAll(child.fail.outputs);
                pending.add(child);
            });
        }
    }

    private Node next(Node from, char symbol) {
        Node current = from;
        while (!current.root && !current.moves.containsKey(symbol)) {
            current = current.fail;
        }
        return current.moves.getOrDefault(symbol, root);
    }

    /** Узел бора: переходы по символам, суффиксная ссылка и образцы, кончающиеся в этом узле. */
    private static final class Node {
        private final boolean root;
        private final Map<Character, Node> moves = new HashMap<>();
        private final List<String> outputs = new ArrayList<>();
        private Node fail;

        Node(boolean root) {
            this.root = root;
        }
    }
}
