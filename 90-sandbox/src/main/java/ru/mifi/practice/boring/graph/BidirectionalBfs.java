package ru.mifi.practice.boring.graph;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntFunction;

/**
 * Двунаправленный поиск в ширину: две волны идут навстречу — одна от начала, вторая от цели, — и
 * путь готов, когда они встретились. Обычный поиск в ширину на пути длины d просматривает порядка
 * b^d вершин, где b — среднее число соседей; здесь каждая волна проходит половину пути, и вместе
 * они дают 2·b^(d/2). На разветвлённом графе это разница в тысячи раз.
 *
 * <p>Волна от цели идёт по дугам в обратную сторону, поэтому обратные связи строятся заранее. За
 * шаг двигается меньшая из волн — так фронт растёт медленнее.
 *
 * <p>Поиск в ширину считает все дуги одинаковыми, вес здесь не смотрится: он нужен другим
 * алгоритмам пакета. Каждая волна помнит, откуда пришла в вершину, поэтому после встречи путь
 * собирается по двум цепочкам предков — от места встречи в обе стороны.
 */
public final class BidirectionalBfs {

    /** Путь от начала до цели по вершинам; пустой список, если пути нет. */
    public List<Integer> path(Graph graph, int from, int to) {
        if (from == to) {
            return List.of(from);
        }
        List<List<Integer>> incoming = incoming(graph);
        Map<Integer, Integer> forward = new HashMap<>(Map.of(from, from));
        Map<Integer, Integer> backward = new HashMap<>(Map.of(to, to));
        Deque<Integer> ahead = new ArrayDeque<>(List.of(from));
        Deque<Integer> behind = new ArrayDeque<>(List.of(to));
        while (!ahead.isEmpty() && !behind.isEmpty()) {
            int met = ahead.size() <= behind.size()
                ? step(ahead, forward, backward, vertex -> outgoing(graph, vertex))
                : step(behind, backward, forward, incoming::get);
            if (met >= 0) {
                return joined(met, forward, backward);
            }
        }
        return List.of();
    }

    /** Шаг одной волны на весь уровень; если сосед помечен встречной волной — место встречи. */
    private int step(Deque<Integer> wave,
                     Map<Integer, Integer> own,
                     Map<Integer, Integer> other,
                     IntFunction<List<Integer>> neighbours) {
        for (int size = wave.size(); size > 0; size--) {
            int vertex = wave.poll();
            for (int neighbour : neighbours.apply(vertex)) {
                if (own.containsKey(neighbour)) {
                    continue;
                }
                own.put(neighbour, vertex);
                if (other.containsKey(neighbour)) {
                    return neighbour;
                }
                wave.add(neighbour);
            }
        }
        return -1;
    }

    /** Сшивает половинки: цепочку предков до начала разворачивает, цепочку до цели дописывает. */
    private List<Integer> joined(int met, Map<Integer, Integer> forward, Map<Integer, Integer> backward) {
        List<Integer> path = new ArrayList<>();
        int vertex = met;
        while (forward.get(vertex) != vertex) {
            path.add(vertex);
            vertex = forward.get(vertex);
        }
        path.add(vertex);
        Collections.reverse(path);
        int next = backward.get(met);
        while (next != met) {
            path.add(next);
            if (backward.get(next) == next) {
                return path;
            }
            next = backward.get(next);
        }
        return path;
    }

    private List<Integer> outgoing(Graph graph, int vertex) {
        List<Integer> neighbours = new ArrayList<>();
        graph.edges(vertex).forEach(edge -> neighbours.add(edge.to()));
        return neighbours;
    }

    private List<List<Integer>> incoming(Graph graph) {
        List<List<Integer>> incoming = new ArrayList<>();
        for (int vertex = 0; vertex < graph.size(); vertex++) {
            incoming.add(new ArrayList<>());
        }
        graph.edges().forEach(edge -> incoming.get(edge.to()).add(edge.from()));
        return incoming;
    }
}
