package ru.mifi.practice.vol8.regexp;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public interface Tree {

    Node root();

    void visit(Visitor visitor);

    enum Operator {
        STAR {
            @Override
            public String toString() {
                return "*";
            }
        },
        PLUS {
            @Override
            public String toString() {
                return "+";
            }
        },
        QUESTION {
            @Override
            public String toString() {
                return "?";
            }
        }
    }

    interface Node {
        default void visit(Visitor visitor) {
            //Nothing
        }

        default boolean isEmpty() {
            return false;
        }

        String toText();
    }

    interface Visitor {
        void visit(Char ch);

        void enter(And and);

        void enter(Or or);

        void enter(Unary unary);

        void enter(Group group);

        void enter(Range range);

        void enter(Set set);

        void exit(And and);

        void exit(Or or);

        void exit(Unary unary);

        void exit(Group group);

        void exit(Range range);

        void exit(Set set);

        void start();

        void end();

        void any();
    }

    record Empty() implements Node {
        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public String toText() {
            return "";
        }
    }

    record Char(char ch) implements Node {
        @Override
        public void visit(Visitor visitor) {
            visitor.visit(this);
        }

        @Override
        public String toText() {
            return "" + ch;
        }

        @Override
        public String toString() {
            return "" + ch;
        }
    }

    record Escape(char ch) implements Node {
        @Override
        public String toString() {
            return "\\" + ch;
        }

        @Override
        public String toText() {
            return "\\" + ch;
        }
    }

    record And(Node left, Node right) implements Node {
        @Override
        public void visit(Visitor visitor) {
            visitor.enter(this);
            left.visit(visitor);
            right.visit(visitor);
            visitor.exit(this);
        }

        @Override
        public String toText() {
            return left.toText() + "," + right.toText();
        }

        @Override
        public String toString() {
            return left + "" + right;
        }
    }

    record Or(List<Node> nodes) implements Node {
        private Or(Node node) {
            this(new ArrayList<>(List.of(node)));
        }

        private Or add(Or or) {
            this.nodes.addAll(or.nodes);
            return this;
        }

        public Or add(Node next) {
            this.nodes.add(next);
            return this;
        }

        @Override
        public void visit(Visitor visitor) {
            visitor.enter(this);
            nodes.forEach(node -> node.visit(visitor));
            visitor.exit(this);
        }

        @Override
        public String toText() {
            return nodes.stream().map(Node::toText).collect(Collectors.joining("|"));
        }

        @Override
        public String toString() {
            return nodes.stream().map(Node::toString).collect(Collectors.joining("|"));
        }
    }

    record Unary(Operator operator, Node node) implements Node {
        @Override
        public String toString() {
            return node + operator.toString();
        }

        @Override
        public void visit(Visitor visitor) {
            visitor.enter(this);
            node.visit(visitor);
            visitor.exit(this);
        }

        @Override
        public String toText() {
            return switch (operator) {
                case STAR -> "{" + node.toText() + "}";
                case PLUS -> node.toText() + ",{" + node.toText() + "}";
                case QUESTION -> "[" + node.toText() + "]";
            };
        }
    }

    record Group(Node node) implements Node {
        @Override
        public String toString() {
            return "(" + node + ")";
        }

        @Override
        public void visit(Visitor visitor) {
            visitor.enter(this);
            node.visit(visitor);
            visitor.exit(this);
        }

        @Override
        public String toText() {
            return node.toText();
        }
    }

    record Range(Node start, Node end) implements Node {
        @Override
        public String toString() {
            return start.toString() + "-" + end.toString();
        }

        @Override
        public void visit(Visitor visitor) {
            visitor.enter(this);
            start.visit(visitor);
            end.visit(visitor);
            visitor.exit(this);
        }

        @Override
        public String toText() {
            return start.toText() + "-" + end.toText();
        }
    }

    record Set(boolean positive, List<Node> nodes) implements Node {
        @Override
        public String toString() {
            return "[" + (positive ? "" : "^") + nodes.stream().map(Node::toString).collect(Collectors.joining()) + "]";
        }

        @Override
        public void visit(Visitor visitor) {
            visitor.enter(this);
            nodes.forEach(node -> node.visit(visitor));
            visitor.exit(this);
        }

        @Override
        public String toText() {
            return "(" + nodes.stream().map(Node::toText).collect(Collectors.joining("|")) + ")";
        }
    }

    record Any() implements Node {
        @Override
        public String toString() {
            return ".";
        }

        @Override
        public void visit(Visitor visitor) {
            visitor.any();
        }

        @Override
        public String toText() {
            return ".";
        }
    }

    final class Default implements Tree {
        private final Node root;

        public Default(String text) {
            this.root = new Parser(text).parse();
        }

        @Override
        public Node root() {
            return root;
        }

        @Override
        public void visit(Visitor visitor) {
            visitor.start();
            root.visit(visitor);
            visitor.end();
        }
    }

    final class Parser {
        private final char[] chars;
        private char current;
        private int index = 0;

        private Parser(String text) {
            this.chars = text.trim().toCharArray();
            this.current = this.chars[0];
        }

        private Node parse() {
            Node simple = parseSimple();
            if (simple.isEmpty()) {
                return simple;
            }
            if (peekChar() == '|') {
                next();
                Node next = parse();
                if (next instanceof Or or) {
                    return new Or(simple).add(or);
                }
                return new Or(simple).add(next);
            }
            return simple;
        }

        private Node parseSimple() {
            Node basic = parseBasic();
            if (basic.isEmpty()) {
                return basic;
            }
            next();
            if (eof()) {
                return basic;
            }
            Node next = parseSimple();
            if (next.isEmpty()) {
                return basic;
            }
            return new And(basic, next);
        }

        private Node parseBasic() {
            Node elementary = parseElementary();
            if (elementary.isEmpty()) {
                return elementary;
            }
            next();
            if (peekChar() == '*') {
                return new Unary(Operator.STAR, elementary);
            } else if (peekChar() == '+') {
                return new Unary(Operator.PLUS, elementary);
            } else if (peekChar() == '?') {
                return new Unary(Operator.QUESTION, elementary);
            }
            prev();
            return elementary;
        }

        private Node parseElementary() {
            if (peekChar() == '.') {
                next();
                return new Any();
            } else if (peekChar() == '[') {
                return parseSet();
            } else if (peekChar() == '(') {
                return parseGroup();
            } else if (peekChar() == ')') {
                return new Empty();
            } else if (peekChar() == ']') {
                return new Empty();
            } else if (peekChar() == '|') {
                return new Empty();
            }
            return parseChar();
        }

        private Node parseGroup() {
            if (peekChar() == '(') {
                next();
                Node element = parse();
                expect(')');
                return new Group(element);
            }
            return new Empty();
        }

        private Node parseSet() {
            if (peekChar() == '[') {
                next();
                boolean positive = true;
                if (peekChar() == '^') {
                    positive = false;
                    next();
                }
                return parseSet(positive);
            }
            return new Empty();
        }

        private Node parseSet(boolean positive) {
            Set set = new Set(positive, new ArrayList<>());
            while (!eof() && peekChar() != ']') {
                set.nodes.add(parseSetElement());
            }
            return set;
        }

        private Node parseSetElement() {
            Node start = parseChar();
            next();
            if (peekChar() == '-') {
                next();
                return new Range(start, parseChar());
            }
            return start;
        }

        private Node parseChar() {
            char c = peekChar();
            if (c == 0) {
                return new Empty();
            } else if (c == '\\') {
                next();
                return new Escape(peekChar());
            }
            return new Char(c);
        }

        private void next() {
            index++;
            if (eof()) {
                current = 0;
            } else {
                current = chars[index];
            }
        }

        private void prev() {
            index--;
            current = chars[index];
        }

        private void expect(char c) {
            if (eof() || peekChar() != c) {
                throw new IllegalStateException("Unexpected character '" + c + "' but '" + peekChar() + "'");
            }
        }

        private char peekChar() {
            if (eof()) {
                return 0;
            }
            return current;
        }

        private boolean eof() {
            return index >= chars.length;
        }

        @Override
        public String toString() {
            return "Char: " + current + ", Index:" + index;
        }
    }
}
