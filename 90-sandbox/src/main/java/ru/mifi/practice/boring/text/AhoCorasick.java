package ru.mifi.practice.boring.text;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Поиск множества образцов за один проход — алгоритм Ахо — Корасика. Образцы складываются в бор:
 * общие начала образцов лежат общим путём. Затем по бору строится автомат: у каждого узла
 * появляется суффиксная ссылка на узел, отвечающий самому длинному собственному суффиксу
 * пройденной строки, который тоже есть в боре.
 *
 * <p>Это та же префикс-функция Кнута — Морриса — Пратта, только сразу для всего набора: при
 * несовпадении автомат не возвращается по тексту, а сползает по суффиксной ссылке. Поэтому текст
 * читается один раз, сколько бы образцов ни искали, — в этом всё отличие от Рабина — Карпа,
 * которому нужен отдельный проход на каждый образец.
 *
 * <p>К узлу приписаны образцы, кончающиеся в нём, плюс образцы его суффиксной ссылки: иначе
 * образец «he» потерялся бы внутри «she».
 */
public final class AhoCorasick implements Search {

    @Override
    public Map<String, List<Integer>> positions(String text, List<String> patterns) {
        Node root = automaton(patterns);
        Map<String, List<Integer>> found = new LinkedHashMap<>();
        patterns.forEach(pattern -> found.put(pattern, new ArrayList<>()));
        Node current = root;
        for (int at = 0; at < text.length(); at++) {
            current = next(root, current, text.charAt(at));
            for (String output : current.outputs) {
                found.get(output).add(at - output.length() + 1);
            }
        }
        return found;
    }

    private Node automaton(List<String> patterns) {
        Node root = new Node(true);
        for (String pattern : patterns) {
            grow(root, pattern);
        }
        link(root);
        return root;
    }

    private void grow(Node root, String pattern) {
        if (pattern.isEmpty()) {
            throw new IllegalArgumentException("Пустой образец совпадает с любой позицией текста");
        }
        Node current = root;
        for (char symbol : pattern.toCharArray()) {
            if (!current.moves.containsKey(symbol)) {
                current.moves.put(symbol, new Node(false));
            }
            current = current.moves.get(symbol);
        }
        current.outputs.add(pattern);
    }

    /** Обход бора в ширину: суффиксная ссылка узла строится по уже готовой ссылке родителя. */
    private void link(Node root) {
        Deque<Node> pending = new ArrayDeque<>();
        root.fail = root;
        for (Node child : root.moves.values()) {
            child.fail = root;
            pending.add(child);
        }
        while (!pending.isEmpty()) {
            Node node = pending.poll();
            node.moves.forEach((symbol, child) -> {
                child.fail = next(root, node.fail, symbol);
                child.outputs.addAll(child.fail.outputs);
                pending.add(child);
            });
        }
    }

    private Node next(Node root, Node from, char symbol) {
        Node current = from;
        while (!current.root && !current.moves.containsKey(symbol)) {
            current = current.fail;
        }
        return current.moves.getOrDefault(symbol, root);
    }

    /** Узел бора: переходы по символам, суффиксная ссылка и образцы, кончающиеся здесь. */
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
