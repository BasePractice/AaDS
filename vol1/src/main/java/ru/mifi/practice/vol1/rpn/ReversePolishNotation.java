package ru.mifi.practice.vol1.rpn;

import java.util.Optional;
import java.util.Stack;

public interface ReversePolishNotation {
    Number evaluate(String expression);

    @SuppressWarnings("PMD.LooseCoupling")
    final class Default implements ReversePolishNotation {
        @Override
        public Number evaluate(String expression) {
            String[] parts = expression.split("\\s+");
            Stack<Number> stack = new Stack<>();
            for (String part : parts) {
                part = part.trim();
                if (part.isEmpty()) {
                    continue;
                }
                if (Character.isDigit(part.charAt(0))) {
                    stack.push(Integer.parseInt(part));
                } else {
                    var o = part;
                    Op.fromString(o).map(op -> op.eval(stack))
                        .orElseThrow(() -> new UnsupportedOperationException("Unsupported " + o));
                }
            }

            if (stack.isEmpty()) {
                throw new IllegalArgumentException("Stack is empty");
            }
            return stack.pop();
        }

        @SuppressWarnings("PMD.LooseCoupling")
        enum Op {
            PLUS("+") {
                @Override
                boolean eval(Stack<Number> stack) {
                    stack.push(stack.pop().doubleValue() + stack.pop().doubleValue());
                    return true;
                }
            },
            MINUS("-") {
                @Override
                boolean eval(Stack<Number> stack) {
                    stack.push(stack.pop().doubleValue() - stack.pop().doubleValue());
                    return true;
                }
            },
            MULTIPLY("*") {
                @Override
                boolean eval(Stack<Number> stack) {
                    stack.push(stack.pop().doubleValue() * stack.pop().doubleValue());
                    return true;
                }
            },
            DIVIDE("/") {
                @Override
                boolean eval(Stack<Number> stack) {
                    stack.push(stack.pop().doubleValue() / stack.pop().doubleValue());
                    return true;
                }
            },
            ABS("abs") {
                @Override
                boolean eval(Stack<Number> stack) {
                    stack.push(Math.abs(stack.pop().doubleValue()));
                    return true;
                }
            };
            private final String op;

            Op(String op) {
                this.op = op;
            }

            static Optional<Op> fromString(String text) {
                for (Op op : values()) {
                    if (op.op.equals(text)) {
                        return Optional.of(op);
                    }
                }
                return Optional.empty();
            }

            abstract boolean eval(Stack<Number> stack);
        }
    }
}
