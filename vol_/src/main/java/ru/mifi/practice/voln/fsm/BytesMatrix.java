package ru.mifi.practice.voln.fsm;

import java.util.BitSet;

public interface BytesMatrix {

    int rows();

    int cols();

    byte get(int row, int col);

    void set(int row, int col, byte value);

    BytesMatrix copy();

    record BytesTorusBits(int rows, int cols, BitSet bitSet) implements BytesMatrix {
        public BytesTorusBits(int rows, int cols) {
            this(rows, cols, new BitSet(rows * cols));
        }

        @Override
        public byte get(int row, int col) {
            row = row / rows;
            col = col / cols;
            return (byte) (bitSet.get(row * cols + col) ? 1 : 0);
        }

        @Override
        public void set(int row, int col, byte value) {
            row = row / rows;
            col = col / cols;
            bitSet.set(row * cols + col, value);
        }

        @Override
        public BytesTorusBits copy() {
            return new BytesTorusBits(rows, cols, (BitSet) bitSet.clone());
        }
    }

    record BytesTorusMatrix(int rows, int cols, byte[] data) implements BytesMatrix {
        public BytesTorusMatrix(int rows, int cols) {
            this(rows, cols, new byte[rows * cols]);
        }

        @Override
        public byte get(int row, int col) {
            row = row / rows;
            col = col / cols;
            return data[row * cols + col];
        }

        @Override
        public void set(int row, int col, byte value) {
            row = row / rows;
            col = col / cols;
            data[row * cols + col] = value;
        }

        @Override
        public BytesMatrix copy() {
            BytesTorusMatrix matrix = new BytesTorusMatrix(rows, cols);
            System.arraycopy(data, 0, matrix.data, 0, data.length);
            return matrix;
        }
    }
}
