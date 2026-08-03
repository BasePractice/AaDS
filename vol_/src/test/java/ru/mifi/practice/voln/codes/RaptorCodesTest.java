package ru.mifi.practice.voln.codes;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Raptor Codes: базовые сценарии и граничные случаи")
final class RaptorCodesTest {

    @Test
    @Timeout(5)
    @DisplayName("Строка кодируется и восстанавливается")
    void basicEncodeDecode() {
        byte[] data = "Hello, Raptor!".getBytes(StandardCharsets.UTF_8);
        RaptorConfiguration cfg = RaptorConfiguration.defaults(64, 42L);
        RaptorEncoder enc = RaptorEncoder.fromData(data, cfg);
        int total = enc.totalIntermediates() + 12;
        List<EncodedSymbol> packets = new ArrayList<>();
        for (int i = 0; i < total; i++) {
            packets.add(enc.nextSymbol(i));
        }
        RaptorDecoder dec = RaptorDecoder.create(cfg, enc.k(), enc.originalLength());
        for (EncodedSymbol p : packets) {
            dec.addSymbol(p);
        }
        assertThat("decoded message doesnt match the original", dec.decode(), is(data));
    }

    @Test
    @Timeout(5)
    @DisplayName("Потеря части пакетов: декодер восстанавливает")
    void packetLoss() {
        byte[] data = new byte[10_000];
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) (i * 31 + 7);
        }
        RaptorConfiguration cfg = RaptorConfiguration.defaults(256, 2025L);
        RaptorEncoder enc = RaptorEncoder.fromData(data, cfg);
        int needed = enc.totalIntermediates();
        int total = needed + needed / 3;
        List<EncodedSymbol> packets = new ArrayList<>();
        for (int i = 0; i < total; i++) {
            if (i % 5 == 0) {
                continue;
            }
            packets.add(enc.nextSymbol(i));
        }
        RaptorDecoder dec = RaptorDecoder.create(cfg, enc.k(), enc.originalLength());
        for (EncodedSymbol p : packets) {
            dec.addSymbol(p);
        }
        assertThat("decoded data doesnt match the original after packet loss", dec.decode(), is(data));
    }

    @Test
    @Timeout(5)
    @DisplayName("Пустой ввод: исходная длина равна нулю")
    void emptyInputOriginalLengthZero() {
        RaptorConfiguration cfg = RaptorConfiguration.defaults(64, 1L);
        RaptorEncoder enc = RaptorEncoder.fromData(new byte[0], cfg);
        assertThat("original length of empty input is not zero", enc.originalLength(), is(0));
    }

    @Test
    @Timeout(5)
    @DisplayName("Пустой ввод: декодируется пустой массив")
    void emptyInputDecodesToEmpty() {
        RaptorConfiguration cfg = RaptorConfiguration.defaults(64, 1L);
        RaptorEncoder enc = RaptorEncoder.fromData(new byte[0], cfg);
        List<EncodedSymbol> packets = new ArrayList<>();
        for (int i = 0; i < enc.totalIntermediates() + 3; i++) {
            packets.add(enc.nextSymbol(i));
        }
        RaptorDecoder dec = RaptorDecoder.create(cfg, enc.k(), enc.originalLength());
        for (EncodedSymbol p : packets) {
            dec.addSymbol(p);
        }
        assertThat("empty input doesnt decode to empty array", dec.decode().length, is(0));
    }

    @Test
    @Timeout(5)
    @DisplayName("Один байт восстанавливается")
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
        assertThat("single byte doesnt survive encode and decode", dec.decode(), is(src));
    }

    @Test
    @Timeout(5)
    @DisplayName("Корректный пакет принимается декодером")
    void validSymbolAccepted() {
        RaptorConfiguration cfg = RaptorConfiguration.defaults(16, 3L);
        RaptorEncoder enc = RaptorEncoder.fromData("abc".getBytes(StandardCharsets.UTF_8), cfg);
        EncodedSymbol ok = enc.nextSymbol(1);
        RaptorDecoder dec = RaptorDecoder.create(cfg, enc.k(), enc.originalLength());
        assertDoesNotThrow(() -> dec.addSymbol(ok), "valid symbol is wrongly rejected");
    }

    @Test
    @Timeout(5)
    @DisplayName("Пакет с индексом вне диапазона отвергается")
    void invalidNeighborIndexRejected() {
        RaptorConfiguration cfg = RaptorConfiguration.defaults(16, 3L);
        RaptorEncoder enc = RaptorEncoder.fromData("abc".getBytes(StandardCharsets.UTF_8), cfg);
        EncodedSymbol ok = enc.nextSymbol(1);
        int[] neigh = new int[]{enc.totalIntermediates() + 1};
        EncodedSymbol bad = new EncodedSymbol(999, neigh, ok.payload());
        RaptorDecoder dec = RaptorDecoder.create(cfg, enc.k(), enc.originalLength());
        assertThrows(IllegalArgumentException.class, () -> dec.addSymbol(bad),
            "symbol with out of range neighbor index is not rejected");
    }

    @Test
    @Timeout(5)
    @DisplayName("Дубликаты пакетов не мешают декодированию")
    void duplicates() {
        byte[] data = "duplicates".getBytes(StandardCharsets.UTF_8);
        RaptorConfiguration cfg = RaptorConfiguration.defaults(64, 555L);
        RaptorEncoder enc = RaptorEncoder.fromData(data, cfg);
        List<EncodedSymbol> pkts = new ArrayList<>();
        for (int i = 0; i < enc.totalIntermediates() + 4; i++) {
            EncodedSymbol p = enc.nextSymbol(i);
            pkts.add(p);
            if (i % 3 == 0) {
                pkts.add(p);
            }
        }
        RaptorDecoder dec = RaptorDecoder.create(cfg, enc.k(), enc.originalLength());
        pkts.forEach(dec::addSymbol);
        assertThat("duplicate packets break decoding", dec.decode(), is(data));
    }
}
