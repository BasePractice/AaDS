package ru.mifi.practice.vol6.tree.huffman;

public abstract class Main {
    public static void main(String[] args) {
        String text = "Hello World";
        Tree.Compressed compressed = Tree.compress(text);
        System.out.println("Original    : " + text);
        System.out.println("Length      : " + text.length());
        System.out.println("Compressed  : " + compressed.text());
        System.out.println("Length      : " + compressed.text().length() / 8);
        System.out.println("Decompressed: " + compressed.decompress());
    }
}
