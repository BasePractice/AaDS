package ru.mifi.practice.vol8.regexp.visitor;

import ru.mifi.practice.vol8.regexp.Tree;

public final class UTextVisitor implements Tree.Visitor {
    private final StringBuilder buffer = new StringBuilder();
    private Tree.Node lastNode;
    @Override
    public void visit(Tree.Char ch) {
        if (!buffer.isEmpty() && buffer.charAt(buffer.length() - 1) != '|') {
            buffer.append(',');
        }
        buffer.append(ch);
    }

    @Override
    public void enter(Tree.And and) {
        //Nothing
    }

    @Override
    public void exit(Tree.And and) {
        lastNode = and;
    }

    @Override
    public void enter(Tree.Or or) {

    }

    @Override
    public void exit(Tree.Or or) {
        lastNode = or;
    }

    @Override
    public void enter(Tree.Unary unary) {

    }

    @Override
    public void exit(Tree.Unary unary) {
        switch (unary.operator()) {
            case STAR -> {
                buffer.append('[').append(lastNode).append(']');
            }
            case PLUS -> {
                buffer.append(lastNode).append('[').append(lastNode).append(']');
            }
            case QUESTION -> {
                buffer.append('{').append(lastNode).append('}');
            }
        }
        lastNode = null;
    }

    @Override
    public void enter(Tree.Group group) {
        buffer.append('(');
    }

    @Override
    public void exit(Tree.Group group) {
        buffer.append(')');
    }

    @Override
    public void enter(Tree.Range range) {

    }

    @Override
    public void exit(Tree.Range range) {

    }

    @Override
    public void enter(Tree.Set set) {
        buffer.append('[');
    }

    @Override
    public void exit(Tree.Set set) {
        buffer.append(']');
    }

    @Override
    public void start() {
        buffer.setLength(0);
        buffer.append("@startebnf").append("\n").append("pattern = ");
    }

    @Override
    public void end() {
        buffer.append(";").append("\n");
        buffer.append("@endebnf").append("\n");
    }

    @Override
    public void any() {

    }

    @Override
    public String toString() {
        return buffer.toString();
    }
}
