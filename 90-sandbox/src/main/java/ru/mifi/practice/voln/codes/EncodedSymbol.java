package ru.mifi.practice.voln.codes;

import java.util.Arrays;

/**
 * Закодированный пакет Raptor/LT: содержит список индексов соседей
 * (межуточных символов) и полезную нагрузку — их XOR.
 */
public record EncodedSymbol(long id, int[] neighbors, byte[] payload) {
    /**
     * Создаёт закодированный пакет.
     *
     * @param id        идентификатор пакета (произвольный, используется лишь для воспроизводимости)
     * @param neighbors индексы промежуточных символов, вошедших в XOR
     * @param payload   байтовая нагрузка, размер равен размеру символа
     */
    public EncodedSymbol(long id, int[] neighbors, byte[] payload) {
        if (neighbors == null || neighbors.length == 0) {
            throw new IllegalArgumentException("Список соседей пуст");
        }
        if (payload == null || payload.length == 0) {
            throw new IllegalArgumentException("Пустая полезная нагрузка");
        }
        this.id = id;
        this.neighbors = Arrays.copyOf(neighbors, neighbors.length);
        this.payload = Arrays.copyOf(payload, payload.length);
    }
}
