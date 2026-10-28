package ru.mifi.practice.vol8.regexp.tree;

import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/** Синтаксическое дерево регулярного выражения. */
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
    }

    /**
     * Обходчик дерева. Каждый метод по умолчанию ничего не делает, поэтому реализация
     * описывает только интересные ей узлы, не перечисляя два десятка пустых заглушек.
     */
    interface Visitor {
        default void visit(Char ch) {
            //Nothing
        }

        default void visit(Escape escape) {
            //Nothing
        }

        default void enter(And and) {
            //Nothing
        }

        default void enter(Or or) {
            //Nothing
        }

        default void enter(Unary unary) {
            //Nothing
        }

        default void enter(Group group) {
            //Nothing
        }

        default void enter(Range range) {
            //Nothing
        }

        default void enter(Set set) {
            //Nothing
        }

        default void exit(And and) {
            //Nothing
        }

        default void exit(Or or) {
            //Nothing
        }

        default void exit(Unary unary) {
            //Nothing
        }

        default void exit(Group group) {
            //Nothing
        }

        default void exit(Range range) {
            //Nothing
        }

        default void exit(Set set) {
            //Nothing
        }

        default void start() {
            //Nothing
        }

        default void end() {
            //Nothing
        }

        default void any() {
            //Nothing
        }

        default void nextOr() {
            //Nothing
        }

        default void nextAnd() {
            //Nothing
        }

        default void nextSet() {
            //Nothing
        }

        default void nextRange() {
            //Nothing
        }
    }

    record Empty() implements Node {
        @Override
        public boolean isEmpty() {
            return true;
        }
    }

    record Char(char ch) implements Node {
        @Override
        public void visit(Visitor visitor) {
            visitor.visit(this);
        }

        @NonNull
        @Override
        public String toString() {
            return "" + ch;
        }
    }

    /**
     * Экранированный символ. Раньше узел не переопределял visit и потому молча не посещался:
     * обходчик его просто не видел, и «\.» не порождало ни состояний, ни вывода.
     */
    record Escape(char ch) implements Node {
        @NonNull
        @Override
        public String toString() {
            return "\\" + ch;
        }

        @Override
        public void visit(Visitor visitor) {
            visitor.visit(this);
        }
    }

    record And(Node left, Node right) implements Node {
        @Override
        public void visit(Visitor visitor) {
            visitor.enter(this);
            left.visit(visitor);
            visitor.nextAnd();
            right.visit(visitor);
            visitor.exit(this);
        }

        @NonNull
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
            nodes.forEach(node -> {
                visitor.nextOr();
                node.visit(visitor);
            });
            visitor.exit(this);
        }

        @NonNull
        @Override
        public String toString() {
            return nodes.stream().map(Node::toString).collect(Collectors.joining("|"));
        }
    }

    record Unary(Operator operator, Node node) implements Node {
        @NonNull
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
    }

    record Group(Node node) implements Node {
        @NonNull
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
    }

    record Range(Node start, Node end) implements Node {
        @NonNull
        @Override
        public String toString() {
            return start.toString() + "-" + end.toString();
        }

        @Override
        public void visit(Visitor visitor) {
            visitor.enter(this);
            start.visit(visitor);
            visitor.nextRange();
            end.visit(visitor);
            visitor.exit(this);
        }
    }

    record Set(boolean positive, List<Node> nodes) implements Node {
        @NonNull
        @Override
        public String toString() {
            return "[" + (positive ? "" : "^") + nodes.stream().map(Node::toString).collect(Collectors.joining()) + "]";
        }

        @Override
        public void visit(Visitor visitor) {
            visitor.enter(this);
            nodes.forEach(node -> {
                visitor.nextSet();
                node.visit(visitor);
            });
            visitor.exit(this);
        }
    }

    record Any() implements Node {
        @NonNull
        @Override
        public String toString() {
            return ".";
        }

        @Override
        public void visit(Visitor visitor) {
            visitor.any();
        }
    }

    record Default(Node root) implements Tree {
        public Default(String root) {
            this(new Parser(root).parse());
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
            expect(']');
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
            char symbol = peekChar();
            if (symbol == 0) {
                return new Empty();
            } else if (symbol == '\\') {
                next();
                return new Escape(peekChar());
            }
            return new Char(symbol);
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

        private void expect(char symbol) {
            if (eof() || peekChar() != symbol) {
                throw new IllegalStateException("Unexpected character '" + symbol + "' but '" + peekChar() + "'");
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
