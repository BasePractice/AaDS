package ru.mifi.practice.vol2.vm;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HexFormat;
import java.util.Locale;


/** Стековая виртуальная машина вычисления и компиляции выражений в байт-код. */
public interface VirtualMachine {

    Value eval(String input, Context context);

    Value eval(Binary input, Context context) throws IOException;

    Binary compile(String input);


    enum Type {
        NONE(0) {
            @Override
            public void write(ByteArrayOutputStream output, Object value) {
                //None
            }

            @Override
            public Value read(InputStream stream) {
                return DefaultValue.none();
            }
        },
        /**
         * Число хранится восемью байтами в формате IEEE-754, потому что машина считает в double.
         * Раньше писался один байт от intValue: значения больше 255, отрицательные и дробные
         * искажались при компиляции.
         */
        NUMBER(1) {
            @Override
            public void write(ByteArrayOutputStream output, Object value) {
                writeLong(output, Double.doubleToLongBits(((Number) value).doubleValue()));
            }

            @Override
            public Value read(InputStream stream) throws IOException {
                return DefaultValue.of(Double.longBitsToDouble(readLong(stream)));
            }
        },
        /**
         * Строка хранится длиной в байтах и телом в UTF-8. Раньше длина писалась в символах, и
         * кириллица, занимающая два байта на символ, ломала чтение.
         */
        STRING(2) {
            @Override
            public void write(ByteArrayOutputStream output, Object value) throws IOException {
                byte[] bytes = ((String) value).getBytes(StandardCharsets.UTF_8);
                writeInt(output, bytes.length);
                output.write(bytes);
            }

            @Override
            public Value read(InputStream stream) throws IOException {
                int length = readInt(stream);
                byte[] bytes = stream.readNBytes(length);
                if (bytes.length != length) {
                    throw new EOFException("Строка оборвалась: прочитано " + bytes.length + " байт из " + length);
                }
                return DefaultValue.of(new String(bytes, StandardCharsets.UTF_8));
            }
        }, BOOL(3) {
            @Override
            public void write(ByteArrayOutputStream output, Object value) {
                output.write((boolean) value ? 1 : 0);
            }

            @Override
            public Value read(InputStream stream) throws IOException {
                return DefaultValue.of(stream.read() == 1);
            }
        };

        private final int code;

        Type(int code) {
            this.code = code;
        }

        static void writeLong(ByteArrayOutputStream output, long value) {
            for (int shift = Long.SIZE - Byte.SIZE; shift >= 0; shift -= Byte.SIZE) {
                output.write((int) (value >>> shift));
            }
        }

        static long readLong(InputStream stream) throws IOException {
            long value = 0;
            for (int i = 0; i < Long.BYTES; i++) {
                value = value << Byte.SIZE | nextByte(stream);
            }
            return value;
        }

        static void writeInt(ByteArrayOutputStream output, int value) {
            for (int shift = Integer.SIZE - Byte.SIZE; shift >= 0; shift -= Byte.SIZE) {
                output.write(value >>> shift);
            }
        }

        static int readInt(InputStream stream) throws IOException {
            int value = 0;
            for (int i = 0; i < Integer.BYTES; i++) {
                value = value << Byte.SIZE | nextByte(stream);
            }
            return value;
        }

        private static int nextByte(InputStream stream) throws IOException {
            int read = stream.read();
            if (read < 0) {
                throw new EOFException("Байт-код оборвался посреди значения");
            }
            return read;
        }

        public static Type of(int code) {
            for (Type type : values()) {
                if (type.code == code) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unknown virtual machine code: " + code);
        }

        public int code() {
            return code;
        }

        public abstract void write(ByteArrayOutputStream output, Object value) throws IOException;

        public abstract Value read(InputStream stream) throws IOException;
    }

    interface OpCode {
        byte code();

        int args();
    }

    interface Context {

        static Context newContext() {
            return new Default(new ArrayDeque<>(100));
        }

        Deque<Value> stack();

        record Default(Deque<Value> stack) implements Context {
        }
    }

    interface Binary {

        static Binary of(byte[] data) {
            return new Default(data);
        }

        byte[] data();

        final class Default implements Binary {
            private final byte[] data;

            private Default(byte[] data) {
                this.data = data;
            }

            private Default(String text) {
                this(HexFormat.of().parseHex(text));
            }

            @Override
            public String toString() {
                return HexFormat.of().formatHex(data).toUpperCase(Locale.ROOT);
            }

            @Override
            public byte[] data() {
                return data;
            }
        }
    }

    interface Value {

        Type type();

        Object value();

        default double doubleValue() {
            if (type() == Type.NUMBER) {
                return ((Number) value()).doubleValue();
            }
            throw new IllegalStateException("Значение не является числом: " + type());
        }

        void write(ByteArrayOutputStream output) throws IOException;
    }

    record DefaultValue(Type type, Object value) implements Value {
        public static Value of(boolean value) {
            return new DefaultValue(Type.BOOL, value);
        }

        public static Value of(Number value) {
            return new DefaultValue(Type.NUMBER, value);
        }

        public static Value of(String text) {
            return new DefaultValue(Type.STRING, text);
        }

        public static Value none() {
            return new DefaultValue(Type.NONE, null);
        }

        @Override
        public void write(ByteArrayOutputStream output) throws IOException {
            output.write(type().code());
            type.write(output, value);
        }

        @Override
        public String toString() {
            return type + "(" + (value() == null ? "" : value()) + ")";
        }
    }

}
