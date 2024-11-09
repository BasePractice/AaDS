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
        NUMBER(1) {
            @Override
            public void write(ByteArrayOutputStream output, Object value) {
                Number number = (Number) value;
                output.write(number.intValue());
            }

            @Override
            public Value read(InputStream stream) throws IOException {
                return DefaultValue.of(stream.read());
            }
        }, STRING(2) {
            @Override
            public void write(ByteArrayOutputStream output, Object value) throws IOException {
                String str = (String) value;
                output.write(str.length());
                output.write(str.getBytes(StandardCharsets.UTF_8));
            }

            @Override
            public Value read(InputStream stream) throws IOException {
                int length = stream.read();
                byte[] bytes = new byte[length];
                int read = stream.read(bytes);
                if (read != length) {
                    throw new EOFException();
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
                this.data = HexFormat.of().parseHex(text);
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
            throw new IllegalStateException();
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
            return new DefaultValue(Type.NUMBER, null);
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
