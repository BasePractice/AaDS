package ru.mifi.practice.vol5.tree;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public interface PathTree<K, V> {

    void add(K[] key, V value);

    Optional<V> match(K[] key);

    static <V> PathTree<String, V> create() {
        String skipElement = "{}";
        return new SearchTree<>(
            element -> element.startsWith("{") && element.endsWith("}") ? skipElement : element, skipElement);
    }

    @FunctionalInterface
    interface Transformer<K> {
        K transform(K element);
    }

    final class SearchTree<K, V> implements PathTree<K, V> {
        private final Map<K, Node<K, V>> nodes = new HashMap<>();
        private final Transformer<K> transformer;
        private final K skip;

        SearchTree(Transformer<K> transformer, K skip) {
            this.transformer = transformer;
            this.skip = skip;
        }

        private Optional<Node<K, V>> search(K[] elements) {
            K element = elements[0];
            Node<K, V> node = nodes.get(element);
            if (node == null) {
                return Optional.empty();
            }
            return search(node, elements, 1);
        }

        private Optional<Node<K, V>> search(Node<K, V> parent, K[] elements, int index) {
            if (index >= elements.length) {
                return Optional.of(parent);
            }
            K element = elements[index];
            Node<K, V> node = parent.nodes.get(element);
            if (node == null) {
                node = parent.nodes.get(skip);
                if (node != null) {
                    return search(node, elements, index + 1);
                }
                return Optional.empty();
            }
            return search(node, elements, index + 1);
        }

        @Override
        public void add(K[] elements, V value) {
            int index = 0;
            K element = transformer.transform(elements[index]);
            Node<K, V> node = nodes.computeIfAbsent(element, e -> new Node<>(null, e, value));
            add(node, elements, index + 1, value);
        }

        private void add(Node<K, V> parent, K[] elements, int index, V value) {
            if (index >= elements.length) {
                return;
            }
            K element = transformer.transform(elements[index]);
            Map<K, Node<K, V>> nodes = parent.nodes;
            Node<K, V> node = nodes.computeIfAbsent(element, e -> new Node<>(parent, e, value));
            if (index + 1 < elements.length) {
                add(node, elements, index + 1, value);
            }
        }

        @Override
        public Optional<V> match(K[] key) {
            return search(key).map(Node::value);
        }
    }

    final class Node<K, V> {
        private final K key;
        private final V value;
        private final Node<K, V> parent;
        private final Map<K, Node<K, V>> nodes;

        private Node(Node<K, V> parent, K key, V value) {
            this.key = key;
            this.parent = parent;
            this.value = value;
            this.nodes = new HashMap<>();
        }

        private V value() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Node<?, ?> node = (Node<?, ?>) o;
            return Objects.equals(key, node.key);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key);
        }

        @Override
        public String toString() {
            return parent == null ? String.valueOf(key) : parent + "/" + key;
        }
    }
}
