package ru.mifi.practice.vol6.tree.huffman;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/** Учебный формат HUF1: таблица частот байтов и упакованные биты. */
public final class HuffmanFile {
    private static final int MAGIC = 0x48554631;
    private static final int MAX_SOURCE_BYTES = 65_536;
    private static final int ALPHABET_SIZE = 256;
    private static final int MAX_PACKED_BYTES = 6 + ALPHABET_SIZE * 5 + MAX_SOURCE_BYTES;

    private HuffmanFile() {
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            throw new IllegalArgumentException("Формат запуска: compress|decompress вход выход");
        }
        Path source = Path.of(args[1]);
        Path target = Path.of(args[2]);
        switch (args[0]) {
            case "compress" -> compress(source, target);
            case "decompress" -> decompress(source, target);
            default -> throw new IllegalArgumentException("Неизвестная команда: " + args[0]);
        }
        System.out.printf("%d → %d байт%n", Files.size(source), Files.size(target));
    }

    public static void compress(Path source, Path target) throws IOException {
        byte[] input = readLimited(source, MAX_SOURCE_BYTES);
        Map<Character, Integer> frequencies = new TreeMap<>();
        for (byte value : input) {
            frequencies.merge((char) Byte.toUnsignedInt(value), 1, Integer::sum);
        }
        Map<Character, String> codes = new HashMap<>();
        Tree.buildCodes(Tree.buildTree(frequencies), "", codes);

        ByteArrayOutputStream packed = new ByteArrayOutputStream();
        try (DataOutputStream output = new DataOutputStream(packed)) {
            output.writeInt(MAGIC);
            output.writeShort(frequencies.size());
            for (Map.Entry<Character, Integer> entry : frequencies.entrySet()) {
                output.writeByte(entry.getKey());
                output.writeInt(entry.getValue());
            }
            writeBits(input, codes, output);
        }
        Files.write(target, packed.toByteArray(), StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
    }

    private static void writeBits(byte[] input, Map<Character, String> codes, DataOutputStream output)
        throws IOException {
        int current = 0;
        int used = 0;
        for (byte value : input) {
            String code = codes.get((char) Byte.toUnsignedInt(value));
            for (int index = 0; index < code.length(); index++) {
                current = (current << 1) | (code.charAt(index) - '0');
                used++;
                if (used == Byte.SIZE) {
                    output.writeByte(current);
                    current = 0;
                    used = 0;
                }
            }
        }
        if (used > 0) {
            output.writeByte(current << (Byte.SIZE - used));
        }
    }

    public static void decompress(Path source, Path target) throws IOException {
        byte[] packed = readLimited(source, MAX_PACKED_BYTES);
        byte[] restored;
        try (DataInputStream input = new DataInputStream(new ByteArrayInputStream(packed))) {
            Map<Character, Integer> frequencies = readHeader(input);
            Tree.Node root = Tree.buildTree(frequencies);
            int length = root == null ? 0 : root.frequency();
            restored = readBits(input, root, length);
            for (byte value : restored) {
                frequencies.merge((char) Byte.toUnsignedInt(value), -1, Integer::sum);
            }
            if (frequencies.values().stream().anyMatch(count -> count != 0)) {
                throw new IOException("Данные не совпадают с таблицей частот");
            }
        }
        Files.write(target, restored, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
    }

    private static Map<Character, Integer> readHeader(DataInputStream input) throws IOException {
        if (input.readInt() != MAGIC) {
            throw new IOException("Ожидается файл HUF1");
        }
        int count = input.readUnsignedShort();
        if (count > ALPHABET_SIZE) {
            throw new IOException("Слишком много символов в таблице частот");
        }
        Map<Character, Integer> frequencies = new HashMap<>();
        int total = 0;
        for (int index = 0; index < count; index++) {
            char symbol = (char) input.readUnsignedByte();
            int frequency = input.readInt();
            if (frequency <= 0 || frequency > MAX_SOURCE_BYTES - total) {
                throw new IOException("Некорректная частота или превышен лимит распаковки");
            }
            if (frequencies.putIfAbsent(symbol, frequency) != null) {
                throw new IOException("Повторяющийся символ в таблице частот");
            }
            total += frequency;
        }
        return frequencies;
    }

    private static byte[] readBits(DataInputStream input, Tree.Node root, int length) throws IOException {
        byte[] restored = new byte[length];
        int current = 0;
        int remaining = 0;
        for (int index = 0; index < length; index++) {
            Tree.Node node = root;
            do {
                if (remaining == 0) {
                    current = input.readUnsignedByte();
                    remaining = Byte.SIZE;
                }
                remaining--;
                int bit = (current >>> remaining) & 1;
                node = followBit(node, bit);
            } while (node.left() != null);
            restored[index] = (byte) node.character();
        }
        int paddingMask = (1 << remaining) - 1;
        if ((current & paddingMask) != 0 || input.read() != -1) {
            throw new IOException("Лишние данные после последнего символа");
        }
        return restored;
    }

    private static Tree.Node followBit(Tree.Node node, int bit) throws IOException {
        if (node.left() == null) {
            if (bit != 0) {
                throw new IOException("Единственному символу соответствует бит 0");
            }
            return node;
        }
        return bit == 0 ? node.left() : node.right();
    }

    private static byte[] readLimited(Path source, int limit) throws IOException {
        try (InputStream input = Files.newInputStream(source)) {
            byte[] bytes = input.readNBytes(limit + 1);
            if (bytes.length > limit) {
                throw new IOException("Размер файла превышает " + limit + " байт");
            }
            return bytes;
        }
    }
}
