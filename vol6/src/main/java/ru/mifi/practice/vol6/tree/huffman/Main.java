package ru.mifi.practice.vol6.tree.huffman;

import java.util.HashMap;
import java.util.Map;

public abstract class Main {
    public static void main(String[] args) {
        String text = "boobs boom";
        Tree.Compressed compressed = Tree.compress(text);
        System.out.println("Original    : " + text);
        System.out.println("Length      : " + text.length());
        System.out.println("Compressed  : " + compressed.text());
        Map<Character, String> codes = new HashMap<>();
        Tree.buildCodes(compressed.tree(), "", codes);
        System.out.println("Codes       : ");
        for (Map.Entry<Character, String> entry : codes.entrySet()) {
            System.out.println("            : " + entry.getKey() + " = " + entry.getValue());
        }
        System.out.println("Length      : " + compressed.text().length() / 8);
        System.out.println("Decompressed: " + compressed.decompress());
    }
}
