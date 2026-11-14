package ru.mifi.practice.vol6.tree.huffman;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;

/** Сжатие и восстановление текста кодом Хаффмана. */
@SuppressWarnings({"PMD.UseUtilityClass", "PMD.LooseCoupling"})
public final class Tree {

    public static Compressed compress(String text) {
        Map<Character, Integer> frequency = new HashMap<>();
        Map<Character, String> codes = new HashMap<>();
        frequency(text, frequency);
        Node root = buildTree(frequency);
        buildCodes(root, "", codes);
        StringBuilder encoded = new StringBuilder();
        for (char character : text.toCharArray()) {
            encoded.append(codes.get(character));
        }
        return new Compressed(root, encoded.toString());
    }

    static void buildCodes(Node node, String code, Map<Character, String> codes) {
        if (node == null) {
            return;
        }
        if (node.left == null && node.right == null) {
            codes.put(node.character, code.isEmpty() ? "0" : code);
            return;
        }
        buildCodes(node.left, code + "0", codes);
        buildCodes(node.right, code + "1", codes);
    }

    static Node buildTree(Map<Character, Integer> frequency) {
        PriorityQueue<Node> min = new PriorityQueue<>();
        frequency.forEach((character, count) -> min.add(new Node(character, count, null, null)));
        while (min.size() > 1) {
            Node left = Objects.requireNonNull(min.poll());
            Node right = Objects.requireNonNull(min.poll());
            Node node = new Node((char) Math.min(left.character, right.character), left.frequency + right.frequency, left, right);
            min.add(node);
        }
        return min.poll();
    }

    private static void frequency(String text, Map<Character, Integer> frequency) {
        frequency.clear();
        for (var character : text.toCharArray()) {
            frequency.put(character, frequency.getOrDefault(character, 0) + 1);
        }
    }

    public record Compressed(Node tree, String text) {
        public String decompress() {
            StringBuilder result = new StringBuilder();
            if (tree == null) {
                return "";
            }
            if (tree.left == null && tree.right == null) {
                return String.valueOf(tree.character).repeat(text.length());
            }
            Node it = tree;
            for (var bit : text.toCharArray()) {
                if (bit == '0') {
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
    @SuppressWarnings("PMD.OverrideBothEqualsAndHashCodeOnComparable")
    record Node(char character, int frequency, Node left, Node right) implements Comparable<Node> {
        @Override
        public int compareTo(Node o) {
            int byFrequency = Integer.compare(this.frequency, o.frequency);
            if (byFrequency != 0) {
                return byFrequency;
            }
            // При равных весах сначала берём лист, затем меньший байт поддерева.
            int byKind = Boolean.compare(left != null, o.left != null);
            return byKind == 0 ? Character.compare(character, o.character) : byKind;
        }
    }
}
