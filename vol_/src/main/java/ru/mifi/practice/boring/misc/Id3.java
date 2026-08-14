package ru.mifi.practice.boring.misc;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Алгоритм ID3: строит дерево решений по таблице примеров. В каждом узле выбирается признак, по
 * которому лучше всего делить, — тот, что сильнее уменьшает неопределённость ответа.
 *
 * <p>Неопределённость меряется энтропией Шеннона: если в выборке все ответы одинаковы, энтропия
 * нулевая, если ответы поровну — она максимальна. Прирост информации от признака — это энтропия
 * до деления минус средневзвешенная энтропия частей после. Признак с наибольшим приростом и
 * становится вопросом в узле.
 *
 * <p>Дальше рекурсия: для каждого значения выбранного признака строится поддерево по оставшимся
 * примерам и оставшимся признакам. Останов — когда все ответы в выборке совпали или признаки
 * кончились; во втором случае берётся самый частый ответ.
 *
 * <p>ID3 жаден и не оглядывается назад: выбрав признак, он его не пересматривает. Поэтому дерево
 * не обязательно наименьшее — зато строится быстро и читается человеком, что для дерева решений
 * часто важнее точности.
 */
public final class Id3 {
    private final List<String[]> rows;
    private final String[] columns;
    private final int target;

    public Id3(List<String[]> rows, String[] columns, int target) {
        this.rows = List.copyOf(rows);
        this.columns = columns.clone();
        this.target = target;
    }

    /** Дерево решений по всей таблице. */
    public Node tree() {
        Set<Integer> features = new LinkedHashSet<>();
        for (int column = 0; column < columns.length; column++) {
            if (column != target) {
                features.add(column);
            }
        }
        return grown(rows, features);
    }

    /** Ответ дерева для набора значений признаков. */
    public String answer(Node tree, Map<String, String> values) {
        Node current = tree;
        while (!current.leaf) {
            Node next = current.branches.get(values.get(current.label));
            if (next == null) {
                return current.fallback;
            }
            current = next;
        }
        return current.label;
    }

    private Node grown(List<String[]> examples, Set<Integer> features) {
        Set<String> answers = answers(examples);
        if (answers.size() == 1) {
            return new Node(answers.iterator().next());
        }
        if (features.isEmpty() || examples.isEmpty()) {
            return new Node(frequent(examples));
        }
        int best = best(examples, features);
        Map<String, Node> branches = new LinkedHashMap<>();
        Set<Integer> rest = new LinkedHashSet<>(features);
        rest.remove(best);
        for (String value : values(examples, best)) {
            branches.put(value, grown(filtered(examples, best, value), rest));
        }
        return new Node(columns[best], branches, frequent(examples));
    }

    /** Признак с наибольшим приростом информации. */
    private int best(List<String[]> examples, Set<Integer> features) {
        int best = features.iterator().next();
        double bestGain = -1;
        for (int feature : features) {
            double gain = entropy(examples) - divided(examples, feature);
            if (gain > bestGain) {
                bestGain = gain;
                best = feature;
            }
        }
        return best;
    }

    /** Средневзвешенная энтропия частей, на которые признак делит выборку. */
    private double divided(List<String[]> examples, int feature) {
        double divided = 0;
        for (String value : values(examples, feature)) {
            List<String[]> part = filtered(examples, feature, value);
            divided += (double) part.size() / examples.size() * entropy(part);
        }
        return divided;
    }

    /** Энтропия Шеннона по ответам выборки. */
    private double entropy(List<String[]> examples) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        examples.forEach(row -> counts.merge(row[target], 1, Integer::sum));
        double entropy = 0;
        for (int count : counts.values()) {
            double share = (double) count / examples.size();
            entropy -= share * Math.log(share) / Math.log(2);
        }
        return entropy;
    }

    private List<String[]> filtered(List<String[]> examples, int feature, String value) {
        List<String[]> filtered = new ArrayList<>();
        for (String[] row : examples) {
            if (row[feature].equals(value)) {
                filtered.add(row);
            }
        }
        return filtered;
    }

    private Set<String> values(List<String[]> examples, int feature) {
        Set<String> values = new LinkedHashSet<>();
        examples.forEach(row -> values.add(row[feature]));
        return values;
    }

    private Set<String> answers(List<String[]> examples) {
        Set<String> answers = new LinkedHashSet<>();
        examples.forEach(row -> answers.add(row[target]));
        return answers;
    }

    private String frequent(List<String[]> examples) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        examples.forEach(row -> counts.merge(row[target], 1, Integer::sum));
        String frequent = "";
        int best = 0;
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            if (entry.getValue() > best) {
                best = entry.getValue();
                frequent = entry.getKey();
            }
        }
        return frequent;
    }

    /** Узел дерева: либо готовый ответ в листе, либо вопрос о признаке с ветвями по значениям. */
    public static final class Node {
        private final boolean leaf;
        private final String label;
        private final Map<String, Node> branches;
        private final String fallback;

        Node(String answer) {
            this(true, answer, Map.of(), answer);
        }

        Node(String feature, Map<String, Node> branches, String fallback) {
            this(false, feature, branches, fallback);
        }

        private Node(boolean leaf, String label, Map<String, Node> branches, String fallback) {
            this.leaf = leaf;
            this.label = label;
            this.branches = Map.copyOf(branches);
            this.fallback = fallback;
        }

        public int size() {
            int size = 1;
            for (Node branch : branches.values()) {
                size += branch.size();
            }
            return size;
        }

        @Override
        public String toString() {
            return leaf ? label : label + branches;
        }
    }
}
