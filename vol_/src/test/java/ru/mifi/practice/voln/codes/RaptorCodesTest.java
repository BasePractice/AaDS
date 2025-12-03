package ru.mifi.practice.voln.codes;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Raptor Codes: базовые сценарии и граничные случаи")
class RaptorCodesTest {

    @Test
    @DisplayName("Базовый сценарий: строка кодируется и восстанавливается")
    void basicEncodeDecode() {
        byte[] data = "Hello, Raptor!".getBytes(StandardCharsets.UTF_8);
        RaptorConfiguration cfg = RaptorConfiguration.defaults(64, 42L);
        RaptorEncoder enc = RaptorEncoder.fromData(data, cfg);

        // Сгенерируем небольшую избыточность
        int total = enc.totalIntermediates() + 12;
        List<EncodedSymbol> packets = new ArrayList<>();
        for (int i = 0; i < total; i++) {
            packets.add(enc.nextSymbol(i));
        }

        RaptorDecoder dec = RaptorDecoder.create(cfg, enc.k(), enc.originalLength());
        for (EncodedSymbol p : packets) {
            dec.addSymbol(p);
        }
        byte[] decoded = dec.decode();
        assertArrayEquals(data, decoded, "Сообщение должно восстановиться без искажений");
    }

    @Test
    @DisplayName("Потеря части пакетов: декодер должен восстановить")
    void packetLoss() {
        byte[] data = new byte[10_000];
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) (i * 31 + 7);
        }
        RaptorConfiguration cfg = RaptorConfiguration.defaults(256, 2025L);
        RaptorEncoder enc = RaptorEncoder.fromData(data, cfg);

        int needed = enc.totalIntermediates();
        int total = needed + needed / 3; // 33% избыточности
        List<EncodedSymbol> packets = new ArrayList<>();
        for (int i = 0; i < total; i++) {
            // Симулируем потери: пропускаем каждый 5-й пакет
            if (i % 5 == 0) {
                continue;
            }
            packets.add(enc.nextSymbol(i));
        }

        RaptorDecoder dec = RaptorDecoder.create(cfg, enc.k(), enc.originalLength());
        for (EncodedSymbol p : packets) {
            dec.addSymbol(p);
        }
        byte[] decoded = dec.decode();
        assertArrayEquals(data, decoded);
    }

    @Test
    @DisplayName("Пустой ввод: должен вернуться пустой массив")
    void emptyInput() {
        RaptorConfiguration cfg = RaptorConfiguration.defaults(64, 1L);
        RaptorEncoder enc = RaptorEncoder.fromData(new byte[0], cfg);
        assertEquals(0, enc.originalLength());

        List<EncodedSymbol> packets = new ArrayList<>();
        for (int i = 0; i < enc.totalIntermediates() + 3; i++) {
            packets.add(enc.nextSymbol(i));
        }
        RaptorDecoder dec = RaptorDecoder.create(cfg, enc.k(), enc.originalLength());
        for (EncodedSymbol p : packets) {
            dec.addSymbol(p);
        }
        byte[] decoded = dec.decode();
        assertEquals(0, decoded.length);
    }

    @Test
    @DisplayName("Один байт и размер символа больше длины")
    void singleByte() {
        RaptorConfiguration cfg = RaptorConfiguration.defaults(128, 99L);
        byte[] src = new byte[]{123};
        RaptorEncoder enc = RaptorEncoder.fromData(src, cfg);
        List<EncodedSymbol> packets = new ArrayList<>();
        for (int i = 0; i < enc.totalIntermediates() + 5; i++) {
            packets.add(enc.nextSymbol(i));
        }
        RaptorDecoder dec = RaptorDecoder.create(cfg, enc.k(), enc.originalLength());
        packets.forEach(dec::addSymbol);
        byte[] decoded = dec.decode();
        assertArrayEquals(src, decoded);
    }

    @Test
    @DisplayName("Некорректный пакет: индекс вне диапазона вызывает исключение")
    void invalidNeighborIndex() {
        RaptorConfiguration cfg = RaptorConfiguration.defaults(16, 3L);
        RaptorEncoder enc = RaptorEncoder.fromData("abc".getBytes(StandardCharsets.UTF_8), cfg);
        EncodedSymbol ok = enc.nextSymbol(1);
        // Сконструируем неверный пакет вручную
        int[] neigh = new int[]{enc.totalIntermediates() + 1};
        EncodedSymbol bad = new EncodedSymbol(999, neigh, ok.payload());
        RaptorDecoder dec = RaptorDecoder.create(cfg, enc.k(), enc.originalLength());
        assertDoesNotThrow(() -> dec.addSymbol(ok));
        assertThrows(IllegalArgumentException.class, () -> dec.addSymbol(bad));
    }

    @Test
    @DisplayName("Добавление дубликатов пакетов не мешает декодированию")
    void duplicates() {
        byte[] data = "duplicates".getBytes(StandardCharsets.UTF_8);
        RaptorConfiguration cfg = RaptorConfiguration.defaults(64, 555L);
        RaptorEncoder enc = RaptorEncoder.fromData(data, cfg);
        List<EncodedSymbol> pkts = new ArrayList<>();
        for (int i = 0; i < enc.totalIntermediates() + 4; i++) {
            EncodedSymbol p = enc.nextSymbol(i);
            pkts.add(p);
            if (i % 3 == 0) {
                pkts.add(p); // дубликат
            }
        }
        RaptorDecoder dec = RaptorDecoder.create(cfg, enc.k(), enc.originalLength());
        pkts.forEach(dec::addSymbol);
        byte[] decoded = dec.decode();
        assertArrayEquals(data, decoded);
    }
}
