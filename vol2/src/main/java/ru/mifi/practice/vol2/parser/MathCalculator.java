package ru.mifi.practice.vol2.parser;

import java.util.Optional;

/**
 * Может ли разборщик обработать все правильные математические выражения?
 *
 * @link <a href="https://github.com/BasePractice/c_fsm/tree/master/math_lang">Реализация на Си</a>
 */
public interface MathCalculator {
    /*
    <expr>    : <term> + <expr>
                <term>
    <term>    : <factor> * <factor>
                <factor>
    <factor>  : <number>
                | '(' <expr> ')'
    <number>  : \d+
     */

    Number evaluate(String expression);

    final class Simple implements MathCalculator {

        @Override
        public Number evaluate(String expression) {
            return new Parser(expression).parse().evaluate();
        }

        enum TokenType {
            NUMBER, LBR, RBR, PLUS, MUL, EOF
        }

        private interface Expression {

            static Expression of(Number number) {
                return new NumberExp(number);
            }

            static Expression of(Expression expression) {
                return new ExpressionExp(expression);
            }

            static Expression of(Expression left, Expression right, char operator) {
                return new OperationExp(left, right, operator);
            }

            Number evaluate();

            final class NumberExp implements Expression {
                private final Number number;

                private NumberExp(Number number) {
                    this.number = number;
                }

                @Override
                public Number evaluate() {
                    return number;
                }

                @Override
                public String toString() {
                    return number.toString();
                }
            }

            final class ExpressionExp implements Expression {
                private final Expression expression;

                private ExpressionExp(Expression expression) {
                    this.expression = expression;
                }

                @Override
                public Number evaluate() {
                    return expression.evaluate();
                }

                @Override
                public String toString() {
                    return "(" + expression + ")";
                }
            }

            final class OperationExp implements Expression {
                private final Expression left;
                private final Expression right;
                private final char operator;

                private OperationExp(Expression left, Expression right, char operator) {
                    this.left = left;
                    this.right = right;
                    this.operator = operator;
                }

                @Override
                public Number evaluate() {
                    return switch (operator) {
                        case '+' -> left.evaluate().doubleValue() + right.evaluate().doubleValue();
                        case '*' -> left.evaluate().doubleValue() * right.evaluate().doubleValue();
                        default -> throw new ArithmeticException();
                    };
                }

                @Override
                public String toString() {
                    return left + " " + operator + " " + right;
                }
            }
        }

        private static final class Parser {
            private final Lexer lexer;

            public Parser(String expression) {
                lexer = new Lexer(expression);
            }

            private Expression parse() {
                Expression expression = parseExpr();
                if (!lexer.eof()) {
                    throw new IllegalArgumentException("Выражение разобрано не полностью");
                }
                return expression;
            }

            private Expression parseExpr() {
                Expression left = parseTerm();
                Optional<Token> nexted = lexer.nextToken();
                while (nexted.isPresent() && nexted.get().type == TokenType.PLUS) {
                    left = Expression.of(left, parseTerm(), '+');
                    nexted = lexer.nextToken();
                }
                if (nexted.isPresent()) {
                    lexer.revert();
                }
                return left;
            }

            private Expression parseTerm() {
                Expression left = parseFactor();
                Optional<Token> nexted = lexer.nextToken();
                while (nexted.isPresent() && nexted.get().type == TokenType.MUL) {
                    left = Expression.of(left, parseFactor(), '*');
                    nexted = lexer.nextToken();
                }
                if (nexted.isPresent()) {
                    lexer.revert();
                }
                return left;
            }

            private Expression parseFactor() {
                Optional<Token> nexted = lexer.nextToken();
                if (nexted.isPresent()) {
                    Token token = nexted.get();
                    if (token.type == TokenType.NUMBER) {
                        return Expression.of((Number) token.value);
                    } else if (token.type == TokenType.LBR) {
                        Expression expression = parseExpr();
                        nexted = lexer.nextToken();
                        if (nexted.isPresent()) {
                            token = nexted.get();
                            if (token.type == TokenType.RBR) {
                                return Expression.of(expression);
                            }
                        }
                        throw new IllegalArgumentException("Не найдена закрывающая скобка");
                    } else {
                        throw new IllegalArgumentException("Ожидалось число или скобка, получено " + token.type);
                    }
                }
                throw new IllegalArgumentException("Выражение оборвалось на месте множителя");
            }
        }

        private static final class Lexer {
            private final char[] chars;
            private int index;
            private int previous;

            private Lexer(String expression) {
                this.chars = expression.toCharArray();
            }

            private boolean eof() {
                skipWhitespace();
                return index >= chars.length;
            }

            private Optional<Token> nextToken() {
                skipWhitespace();
                previous = index;
                if (index < chars.length) {
                    char c = chars[index];
                    if (Character.isDigit(c)) {
                        return Optional.of(new Token(TokenType.NUMBER, parseNumber()));
                    } else if (c == '(') {
                        ++index;
                        return Optional.of(new Token(TokenType.LBR, c));
                    } else if (c == ')') {
                        ++index;
                        return Optional.of(new Token(TokenType.RBR, c));
                    } else if (c == '+') {
                        ++index;
                        return Optional.of(new Token(TokenType.PLUS, c));
                    } else if (c == '*') {
                        ++index;
                        return Optional.of(new Token(TokenType.MUL, c));
                    } else {
                        return Optional.empty();
                    }
                }
                return Optional.empty();
            }

            private Number parseNumber() {
                StringBuilder sb = new StringBuilder();
                while (index < chars.length) {
                    char c = chars[index];
                    if (Character.isDigit(c)) {
                        sb.append(c);
                    } else {
                        break;
                    }
                    ++index;
                }
                return Long.parseLong(sb.toString());
            }

            private void skipWhitespace() {
                while (index < chars.length) {
                    char c = chars[index];
                    if (!Character.isWhitespace(c)) {
                        break;
                    }
                    ++index;
                }
            }

            private void revert() {
                index = previous;
            }
        }

        record Token(TokenType type, Object value) {

        }
    }
}
