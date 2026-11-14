package ru.mifi.practice.vol6.tree.huffman;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Учебный файл Хаффмана")
final class HuffmanFileTest {
    @TempDir
    Path directory;

    @Test
    void restoresOrdinaryFilesByteForByte() throws IOException {
        byte[][] examples = {
            {},
            {42, 42, 42},
            {0, -1, 42, 0, -128},
            "BOOBS BOOM\nабракадабра\n".getBytes(StandardCharsets.UTF_8)
        };
        for (int index = 0; index < examples.length; index++) {
            Path source = Files.write(directory.resolve("original-" + index), examples[index]);
            Path packed = directory.resolve("packed-" + index);
            Path restored = directory.resolve("restored-" + index);
            HuffmanFile.compress(source, packed);
            HuffmanFile.decompress(packed, restored);
            assertArrayEquals(examples[index], Files.readAllBytes(restored));
        }
    }

    @Test
    void rejectsTruncatedPayloadBeforeCreatingOutput() throws IOException {
        Path source = Files.writeString(directory.resolve("original"), "BOOBS BOOM");
        Path packed = directory.resolve("packed");
        Path restored = directory.resolve("restored");
        HuffmanFile.compress(source, packed);
        byte[] data = Files.readAllBytes(packed);
        Files.write(packed, Arrays.copyOf(data, data.length - 1));
        assertThrows(IOException.class, () -> HuffmanFile.decompress(packed, restored));
        assertFalse(Files.exists(restored));
    }
}
