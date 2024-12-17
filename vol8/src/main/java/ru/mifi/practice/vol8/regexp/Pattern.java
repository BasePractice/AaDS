package ru.mifi.practice.vol8.regexp;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public final class Pattern {
    private final Element root;

    private Pattern(Element root) {
        this.root = root;
    }

    @Override
    public String toString() {
        return root.toString().replaceAll("\\s+", "");
    }

    private static String toString(List<Element> elements) {
        StringBuilder result = new StringBuilder();
        for (Element element : elements) {
            result.append(element.toString());
        }
        return result.toString();
    }

    public static Pattern compile(String text) {
        return new Pattern(new Parser(new Lexer(text)).parse());
    }

    public interface Element {

        record Any() implements Element {
            @Override
            public String toString() {
                return ".";
            }
        }

        record Symbol(char symbol) implements Element {
            @Override
            public String toString() {
                return String.valueOf(symbol);
            }
        }

        record Star(Element element) implements Element {
            @Override
            public String toString() {
                return element.toString() + "*";
            }
        }

        record Question(Element element) implements Element {
            @Override
            public String toString() {
                return element.toString() + "?";
            }
        }

        record Plus(Element element) implements Element {
            @Override
            public String toString() {
                return element.toString() + "+";
            }
        }
    }

    public interface Sequence extends Element {

        default Sequence addCharacter(char character) {
            return add(new Symbol(character));
        }

        Sequence add(Element element);

        void addAny();

        class Or extends Simple {
            @Override
            public String toString() {
                StringBuilder result = new StringBuilder();
                Element[] el = elements.toArray(new Element[0]);
                for (int i = 0; i < el.length; i++) {
                    if (i > 0) {
                        result.append(" | ");
                    }
                    result.append(el[i].toString());
                }
                return result.toString();
            }
        }

        class Group extends Simple {
            private char append = 0;

            @Override
            public String toString() {
                return "(" + Pattern.toString(elements) + ")" + (append != 0 ? append : "");
            }

            @Override
            public Sequence add(Element element) {
                if (elements.isEmpty()) {
                    elements.add(element);
                } else {
                    Element last = elements.getLast();
                    if (last instanceof Or or) {
                        or.add(element);
                    } else {
                        elements.add(element);
                    }
                }
                return this;
            }

            public void or() {
                Element last = elements.removeLast();
                if (elements.isEmpty()) {
                    Or or = new Or();
                    or.add(last);
                    last = or;
                } else {
                    Element next = elements.removeLast();
                    if (next instanceof Or or) {
                        or.add(last);
                        last = or;
                    } else {
                        throw new UnsupportedOperationException("unexpected element: " + next);
                    }
                }
                elements.add(last);
            }
        }

        class ClassCharacters extends Simple {
            @Override
            public Sequence add(Element element) {
                if (element instanceof Symbol) {
                    super.add(element);
                } else {
                    throw new UnsupportedOperationException();
                }
                return this;
            }

            @Override
            public String toString() {
                return "[" + Pattern.toString(elements) + "]";
            }
        }

        @SuppressWarnings("PMD.LooseCoupling")
        class Simple implements Sequence {
            protected final LinkedList<Element> elements = new LinkedList<>();

            @Override
            public Sequence add(Element element) {
                elements.add(element);
                return this;
            }

            @Override
            public void addAny() {
                elements.add(new Any());
            }

            @Override
            public String toString() {
                return Pattern.toString(elements);
            }

            public Sequence applyQuestion() {
                elements.add(new Element.Question(elements.removeLast()));
                return this;
            }

            public Sequence applyStar() {
                elements.add(new Element.Star(elements.removeLast()));
                return this;
            }

            public Sequence applyPlus() {
                elements.add(new Element.Plus(elements.removeLast()));
                return this;
            }
        }

        class Unary extends Simple {
            public Unary(Element element) {
                elements.add(element);
            }

            @Override
            public Sequence add(Element element) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void addAny() {
                throw new UnsupportedOperationException();
            }
        }

        class Plus extends Unary {
            Plus(Element element) {
                super(element);
            }

            @Override
            public String toString() {
                return Pattern.toString(elements) + "+";
            }
        }

        class Star extends Unary {
            Star(Element element) {
                super(element);
            }

            @Override
            public String toString() {
                return Pattern.toString(elements) + "*";
            }
        }

        class Question extends Unary {
            public Question(Element element) {
                super(element);
            }

            @Override
            public String toString() {
                return Pattern.toString(elements) + "?";
            }
        }
    }

    private record Parser(Lexer lexer) {

        private Element parse() {
            Deque<Sequence> stack = new ArrayDeque<>();
            stack.push(new Sequence.Simple());
            while (!lexer.eof()) {
                Optional<Token> optional = lexer.token();
                if (optional.isEmpty()) {
                    break;
                }
                lexer.next();
                Token token = optional.get();
                switch (token.type) {
                    case SYMBOL, QUOTED_SYMBOL -> {
                        Sequence sequence = stack.peek();
                        if (sequence instanceof Sequence.Unary) {
                            stack.push(new Sequence.Simple().addCharacter(token.ch));
                        } else {
                            sequence.addCharacter(token.ch);
                        }
                    }
                    case GROUP_OPEN -> {
                        stack.push(new Sequence.Group());
                    }
                    case CLASS_CLOSE, GROUP_CLOSE -> {
                        Sequence sequence = stack.pop();
                        stack.peek().add(sequence);
                    }
                    case CLASS_OPEN -> {
                        stack.push(new Sequence.ClassCharacters());
                    }
                    case QUESTION -> {
                        stack.push(applyQuestion(stack.pop()));
                    }
                    case PLUS -> {
                        stack.push(applyPlus(stack.pop()));
                    }
                    case STAR -> {
                        stack.push(applyStar(stack.pop()));
                    }
                    case ANY -> {
                        stack.peek().addAny();
                    }
                    case OR -> {
                        Sequence element = stack.pop();
                        if (stack.isEmpty()) {
                            Sequence.Or or = new Sequence.Or();
                            or.add(element);
                            stack.push(or);
                        } else if (element instanceof Sequence.Group group) {
                            group.or();
                            stack.push(group);
                        } else {
                            Sequence sequence = stack.pop();
                            if (sequence instanceof Sequence.Or or) {
                                or.add(element);
                                stack.push(or);
                            } else if (sequence instanceof Sequence.Group group) {
                                group.add(element);
                                stack.push(group);
                            } else {
                                Sequence.Or or = new Sequence.Or();
                                Sequence temp = new Sequence.Simple();
                                temp.add(sequence);
                                temp.add(element);
                                or.add(temp);
                                stack.push(or);
                            }
                        }
                        stack.push(new Sequence.Simple());
                    }
                    case EOS -> {
                    }
                    default -> {

                    }
                }
            }

            Sequence sequence = stack.pop();
            if (stack.isEmpty()) {
                return sequence;
            }
            Deque<Sequence> tail = new ArrayDeque<>();
            stack.push(sequence);
            while (!stack.isEmpty()) {
                Sequence pop = stack.pop();
                if (stack.isEmpty()) {
                    Sequence temp = new Sequence.Simple();
                    while (!tail.isEmpty()) {
                        temp.add(tail.removeFirst());
                    }
                    pop.add(temp);
                    return pop;
                } else {
                    tail.push(pop);
                }
            }
            throw new UnsupportedOperationException();
        }

        private static Sequence applyStar(Sequence sequence) {
            if (sequence instanceof Sequence.Group || sequence instanceof Sequence.ClassCharacters) {
                return new Sequence.Star(sequence);
            }
            return ((Sequence.Simple) sequence).applyStar();
        }

        private static Sequence applyPlus(Sequence sequence) {
            if (sequence instanceof Sequence.Group || sequence instanceof Sequence.ClassCharacters) {
                return new Sequence.Plus(sequence);
            }
            return ((Sequence.Simple) sequence).applyPlus();
        }

        private static Sequence applyQuestion(Sequence sequence) {
            if (sequence instanceof Sequence.Group || sequence instanceof Sequence.ClassCharacters) {
                return new Sequence.Question(sequence);
            }
            return ((Sequence.Simple) sequence).applyQuestion();
        }
    }

    private static final class Lexer {
        private final Token[] tokens;
        private int it;

        private Lexer(String input) {
            char[] chars = input.toCharArray();
            List<Token> tokens = new ArrayList<>();
            for (int it = 0; it < chars.length; it++) {
                char ch = chars[it];
                switch (ch) {
                    case '(': {
                        tokens.add(new Token(ch, it, TokenType.GROUP_OPEN));
                        break;
                    }
                    case ')': {
                        tokens.add(new Token(ch, it, TokenType.GROUP_CLOSE));
                        break;
                    }
                    case '[': {
                        tokens.add(new Token(ch, it, TokenType.CLASS_OPEN));
                        break;
                    }
                    case ']': {
                        tokens.add(new Token(ch, it, TokenType.CLASS_CLOSE));
                        break;
                    }
                    case '\\': {
                        if (it + 1 < chars.length) {
                            ++it;
                            ch = chars[it];
                            tokens.add(new Token(ch, it, TokenType.QUOTED_SYMBOL));
                        }
                        break;
                    }
                    case '+': {
                        tokens.add(new Token(ch, it, TokenType.PLUS));
                        break;
                    }
                    case '?': {
                        tokens.add(new Token(ch, it, TokenType.QUESTION));
                        break;
                    }
                    case '*': {
                        tokens.add(new Token(ch, it, TokenType.STAR));
                        break;
                    }
                    case '.': {
                        tokens.add(new Token(ch, it, TokenType.ANY));
                        break;
                    }
                    case '|': {
                        tokens.add(new Token(ch, it, TokenType.OR));
                        break;
                    }
                    case '$': {
                        tokens.add(new Token(ch, it, TokenType.EOS));
                        break;
                    }
                    default: {
                        tokens.add(new Token(ch, it, TokenType.SYMBOL));
                    }
                }
            }
            this.tokens = tokens.toArray(new Token[0]);
            this.it = 0;
        }

        private boolean eof() {
            return it >= tokens.length;
        }

        private Optional<Token> token() {
            return Optional.ofNullable(tokens[it]);
        }

        private boolean next() {
            if (it < tokens.length) {
                ++it;
            }
            return eof();
        }
    }

    record Token(char ch, int pos, TokenType type) {
    }

    enum TokenType {
        EOL,
        SYMBOL,
        /**
         * \\, \(, \)
         */
        QUOTED_SYMBOL,
        /**
         * (
         */
        GROUP_OPEN,
        /**
         * )
         */
        GROUP_CLOSE,
        /**
         * [
         */
        CLASS_OPEN,
        /**
         * ]
         */
        CLASS_CLOSE,
        QUESTION,
        PLUS,
        STAR,
        ANY,
        OR,
        EOS
    }
}
