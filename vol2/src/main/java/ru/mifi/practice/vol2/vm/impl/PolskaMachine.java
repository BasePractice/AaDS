package ru.mifi.practice.vol2.vm.impl;

import ru.mifi.practice.vol2.vm.VirtualMachine;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

public final class PolskaMachine implements VirtualMachine {

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
            int read;
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
                } else {
                    throw new IllegalArgumentException("Unknown op: " + read);
                }
            }
        }
        if (stack.isEmpty()) {
            return DefaultValue.none();
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
                try {
                    if (stack.isEmpty()) {
                        output.write(Type.NONE.code());
                    } else {
                        Value value = stack.pop();
                        value.write(output);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return true;
        });
        return Binary.of(output.toByteArray());
    }

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
