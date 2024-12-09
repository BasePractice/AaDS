package ru.mifi.practice.vol6.tree.huffman;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;

@SuppressWarnings({"PMD.UseUtilityClass", "PMD.LooseCoupling"})
public final class Tree {

    public static Compressed compress(String text) {
        Map<Character, Integer> frequency = new HashMap<>();
        Map<Character, String> codes = new HashMap<>();
        frequency(text, frequency);
        Node root = buildTree(frequency);
        buildCodes(root, "", codes);
        StringBuilder encoded = new StringBuilder();
        for (char c : text.toCharArray()) {
            encoded.append(codes.get(c));
        }
        return new Compressed(root, encoded.toString());
    }

    static void buildCodes(Node node, String code, Map<Character, String> codes) {
        if (node == null) {
            return;
        }
        if (node.left == null && node.right == null) {
            codes.put(node.character, code);
            return;
        }
        buildCodes(node.left, code + "0", codes);
        buildCodes(node.right, code + "1", codes);
    }

    private static Node buildTree(Map<Character, Integer> frequency) {
        PriorityQueue<Node> min = new PriorityQueue<>();
        frequency.forEach((k, v) -> min.add(new Node(k, v, null, null)));

        while (min.size() > 1) {
            Node left = Objects.requireNonNull(min.poll());
            Node right = Objects.requireNonNull(min.poll());

            Node node = new Node((char) 0, left.frequency + right.frequency, left, right);
            min.add(node);
        }
        return min.poll();
    }

    private static void frequency(String text, Map<Character, Integer> frequency) {
        frequency.clear();
        for (var c : text.toCharArray()) {
            frequency.put(c, frequency.getOrDefault(c, 0) + 1);
        }
    }

    public record Compressed(Node tree, String text) {
        public String decompress() {
            StringBuilder result = new StringBuilder();
            Node it = tree;
            for (var c : text.toCharArray()) {
                if (c == '0') {
                    it = it.left;
                } else {
                    it = it.right;
                }
                Objects.requireNonNull(it);
                if (it.left == null && it.right == null) {
                    result.append(it.character);
                    it = tree;
                }
            }
            return result.toString();
        }
    }

    private record Node(char character, int frequency, Node left, Node right) implements Comparable<Node> {
        @Override
        public int compareTo(Node o) {
            return Integer.compare(this.frequency, o.frequency);
        }
    }
}
