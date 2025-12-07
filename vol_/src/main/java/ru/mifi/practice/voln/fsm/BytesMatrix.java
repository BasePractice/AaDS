package ru.mifi.practice.voln.fsm;

import lombok.NonNull;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.BitSet;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public interface BytesMatrix {

    static BytesMatrix defaults(int rows, int cols) {
        return new BytesTorusMatrix(rows, cols);
    }

    static BytesMatrix random(int rows, int cols) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        BytesTorusMatrix matrix = new BytesTorusMatrix(rows, cols);
        for (int row = 0; row < matrix.rows(); row++) {
            for (int col = 0; col < matrix.cols(); col++) {
                boolean nexted = random.nextBoolean();
                matrix.set(row, col, (byte) (nexted ? 1 : 0));
            }
        }
        return matrix;
    }

    int rows();

    int cols();

    byte get(int row, int col);

    void set(int row, int col, byte value);

    void readAt(int row, byte[] data);

    BytesMatrix copy();

    void writeAt(int row, byte[] bytes);

    interface Formatter {
        void write(BytesMatrix matrix, Writer writer) throws IOException;

        Optional<BytesMatrix> read(Reader reader) throws IOException;
    }

    record BytesTorusBits(int rows, int cols, BitSet bitSet) implements BytesMatrix {
        public BytesTorusBits(int rows, int cols) {
            this(rows, cols, new BitSet(rows * cols));
        }

        @Override
        public byte get(int row, int col) {
            row = row % rows;
            col = col % cols;
            return (byte) (bitSet.get(row * cols + col) ? 1 : 0);
        }

        @Override
        public void set(int row, int col, byte value) {
            row = row % rows;
            col = col % cols;
            bitSet.set(row * cols + col, value);
        }

        @Override
        public void readAt(int row, byte[] data) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public BytesTorusBits copy() {
            return new BytesTorusBits(rows, cols, (BitSet) bitSet.clone());
        }

        @Override
        public void writeAt(int row, byte[] bytes) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    record BytesTorusMatrix(int rows, int cols, byte[] data) implements BytesMatrix {
        public BytesTorusMatrix(int rows, int cols) {
            this(rows, cols, new byte[rows * cols]);
        }

        @Override
        public byte get(int row, int col) {
            if (col < 0) {
                col = Math.abs(cols - col);
            }
            if (row < 0) {
                row = Math.abs(rows - row);
            }
            row = row % rows;
            col = col % cols;
            return data[row * cols + col];
        }

        @Override
        public void set(int row, int col, byte value) {
            if (col < 0) {
                col = Math.abs(cols - col);
            }
            if (row < 0) {
                row = Math.abs(rows - row);
            }
            row = row % rows;
            col = col % cols;
            data[row * cols + col] = value;
        }

        @Override
        public void readAt(int row, byte[] bytes) {
            if (bytes.length < cols) {
                throw new IllegalArgumentException();
            }
            row = row % rows;
            System.arraycopy(data, row * cols, bytes, 0, cols);
        }

        @Override
        public BytesMatrix copy() {
            BytesTorusMatrix matrix = new BytesTorusMatrix(rows, cols);
            System.arraycopy(data, 0, matrix.data, 0, data.length);
            return matrix;
        }

        @Override
        public void writeAt(int row, byte[] bytes) {
            if (bytes.length < cols) {
                throw new IllegalArgumentException();
            }
            row = row % rows;
            System.arraycopy(bytes, 0, data, row * cols, bytes.length);
        }

        @NonNull
        @Override
        public String toString() {
            return BytesMatrixFormatter.hexString(data);
        }
    }
}
