package ru.mifi.practice.voln.codes;

import java.util.Arrays;

/**
 * Закодированный пакет Raptor/LT: содержит список индексов соседей
 * (межуточных символов) и полезную нагрузку — их XOR.
 */
public final class EncodedSymbol {
    private final long id;
    private final int[] neighbors;
    private final byte[] payload;

    /**
     * Создаёт закодированный пакет.
     * @param id идентификатор пакета (произвольный, используется лишь для воспроизводимости)
     * @param neighbors индексы промежуточных символов, вошедших в XOR
     * @param payload байтовая нагрузка, размер равен размеру символа
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

    /**
     * Идентификатор пакета.
     * @return id
     */
    public long id() {
        return id;
    }

    /**
     * Копия массива индексов соседей.
     * @return индексы
     */
    public int[] neighbors() {
        return Arrays.copyOf(neighbors, neighbors.length);
    }

    /**
     * Копия полезной нагрузки.
     * @return байты полезной нагрузки
     */
    public byte[] payload() {
        return Arrays.copyOf(payload, payload.length);
    }

    @Override
    public String toString() {
        return "EncodedSymbol{" +
                "id=" + id +
                ", degree=" + neighbors.length +
                ", size=" + payload.length +
                '}';
    }
}
