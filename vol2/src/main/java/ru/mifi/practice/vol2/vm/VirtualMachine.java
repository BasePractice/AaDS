package ru.mifi.practice.vol2.vm;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HexFormat;
import java.util.Optional;


public interface VirtualMachine {

    Value eval(String input, Context context);

    Value eval(Binary input, Context context) throws IOException;

    Binary compile(String input);


    enum Type {
        NUMBER(1) {
            @Override
            void write(ByteArrayOutputStream output, Object value) {
                Number number = (Number) value;
                output.write(code());
                output.write(number.intValue());
            }

            @Override
            Value read(InputStream stream) throws IOException {
                return DefaultValue.of(stream.read());
            }
        }, STRING(2) {
            @Override
            void write(ByteArrayOutputStream output, Object value) throws IOException {
                String str = (String) value;
                output.write(code());
                output.write(str.length());
                output.write(str.getBytes(StandardCharsets.UTF_8));
            }

            @Override
            Value read(InputStream stream) throws IOException {
                int length = stream.read();
                byte[] bytes = new byte[length];
                stream.read(bytes);
                return DefaultValue.of(new String(bytes, StandardCharsets.UTF_8));
            }
        }, BOOL(3) {
            @Override
            void write(ByteArrayOutputStream output, Object value) {
                output.write(code());
                output.write((boolean) value ? 1 : 0);
            }

            @Override
            Value read(InputStream stream) throws IOException {
                return DefaultValue.of(stream.read() == 1);
            }
        };

        private final int code;

        Type(int code) {
            this.code = code;
        }

        static Type of(int code) {
            for (Type type : values()) {
                if (type.code == code) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unknown virtual machine code: " + code);
        }

        int code() {
            return code;
        }

        abstract void write(ByteArrayOutputStream output, Object value) throws IOException;

        abstract Value read(InputStream stream) throws IOException;
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

        byte[] data();

        final class Default implements Binary {
            private final byte[] data;

            private Default(byte[] data) {
                this.data = data;
            }

            @Override
            public String toString() {
                return HexFormat.of().formatHex(data);
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
        private static Value of(boolean value) {
            return new DefaultValue(Type.BOOL, value);
        }

        private static Value of(Number value) {
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
            type.write(output, value);
        }
    }

    final class Default implements VirtualMachine {

        @Override
        public Value eval(String input, Context context) {
            Deque<Value> stack = context.stack();
            processing(input, stack, Op::eval);
            if (stack.isEmpty()) {
                throw new IllegalArgumentException("Stack is empty");
            }
            return stack.pop();
        }

        @Override
        public Value eval(Binary input, Context context) throws IOException {
            Deque<Value> stack = context.stack();
            try (InputStream stream = new ByteArrayInputStream(input.data())) {
                int read = -1;
                while ((read = stream.read()) != -1) {
                    Optional<Op> code = Op.fromCode((byte) read);
                    if (code.isPresent()) {
                        Op op = code.get();
                        for (int i = 0; i < op.args(); i++) {
                            int arg = stream.read();
                            if (arg > 0) {
                                stack.push(Type.of(arg).read(stream));
                            }
                        }
                        op.eval(stack);
                    }
                }
            }
            if (stack.isEmpty()) {
                throw new IllegalArgumentException("Stack is empty");
            }
            return stack.pop();
        }

        @Override
        public Binary compile(String input) {
            Deque<Value> stackInput = new ArrayDeque<>(100);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            processing(input, stackInput, (op, stack) -> {
                output.write(op.code());
                for (int i = 0; i < op.args(); i++) {
                    if (stack.isEmpty()) {
                        output.write(0);
                    } else {
                        Value value = stack.pop();
                        try {
                            value.write(output);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                return true;
            });
            return new Binary.Default(output.toByteArray());
        }

        //CHECKSTYLE:OFF
        private void processing(String input, Deque<Value> stack, Operation operation) {
            String[] parts = input.split("\\s+");
            for (String part : parts) {
                part = part.trim();
                if (part.isEmpty()) {
                    continue;
                }
                if (Character.isDigit(part.charAt(0))) {
                    stack.push(new DefaultValue(Type.NUMBER, Double.valueOf(part)));
                } else {
                    var o = part;
                    Op.fromString(o).map(op -> operation.operation(op, stack))
                        .orElseThrow(() -> new UnsupportedOperationException("Unsupported " + o));
                }
            }
        }
        //CHECKSTYLE:ON

        @SuppressWarnings("PMD.LooseCoupling")
        enum Op implements OpCode {
            PLUS("+", (byte) 1, 2) {
                @Override
                boolean eval(Deque<Value> stack) {
                    stack.push(DefaultValue.of(stack.pop().doubleValue() + stack.pop().doubleValue()));
                    return true;
                }
            },
            MINUS("-", (byte) 2, 2) {
                @Override
                boolean eval(Deque<Value> stack) {
                    stack.push(DefaultValue.of(stack.pop().doubleValue() - stack.pop().doubleValue()));
                    return true;
                }
            },
            MULTIPLY("*", (byte) 3, 2) {
                @Override
                boolean eval(Deque<Value> stack) {
                    stack.push(DefaultValue.of(stack.pop().doubleValue() * stack.pop().doubleValue()));
                    return true;
                }
            },
            DIVIDE("/", (byte) 4, 2) {
                @Override
                boolean eval(Deque<Value> stack) {
                    stack.push(DefaultValue.of(stack.pop().doubleValue() / stack.pop().doubleValue()));
                    return true;
                }
            },
            ABS("abs", (byte) 5, 1) {
                @Override
                boolean eval(Deque<Value> stack) {
                    stack.push(DefaultValue.of(Math.abs(stack.pop().doubleValue())));
                    return true;
                }
            };
            private final String op;
            private final byte code;
            private final int args;

            Op(String op, byte code, int args) {
                this.op = op;
                this.code = code;
                this.args = args;
            }

            static Optional<Op> fromString(String text) {
                for (Op op : values()) {
                    if (op.op.equals(text)) {
                        return Optional.of(op);
                    }
                }
                return Optional.empty();
            }

            static Optional<Op> fromCode(byte code) {
                for (Op op : values()) {
                    if (op.code == code) {
                        return Optional.of(op);
                    }
                }
                return Optional.empty();
            }

            @Override
            public byte code() {
                return code;
            }

            @Override
            public int args() {
                return args;
            }

            abstract boolean eval(Deque<Value> stack);
        }

        @FunctionalInterface
        interface Operation {
            boolean operation(Op op, Deque<Value> stack);
        }
    }
}
