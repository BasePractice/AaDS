package ru.mifi.practice.vol8.regexp.visitor;

import ru.mifi.practice.vol8.regexp.Tree;

abstract class AbstractVisitor implements Tree.Visitor {
    @Override
    public void visit(Tree.Char ch) {
        //Nothing
    }

    @Override
    public void start() {
        //Nothing
    }

    @Override
    public void nextRange() {
        //Nothing
    }

    @Override
    public void nextSet() {
        //Nothing
    }

    @Override
    public void nextAnd() {
        //Nothing
    }

    @Override
    public void nextOr() {
        //Nothing
    }

    @Override
    public void any() {
        //Nothing
    }

    @Override
    public void end() {
        //Nothing
    }

    @Override
    public void exit(Tree.Set set) {
        //Nothing
    }

    @Override
    public void exit(Tree.Range range) {
        //Nothing
    }

    @Override
    public void exit(Tree.Group group) {
        //Nothing
    }

    @Override
    public void exit(Tree.Unary unary) {
        //Nothing
    }

    @Override
    public void exit(Tree.Or or) {
        //Nothing
    }

    @Override
    public void exit(Tree.And and) {
        //Nothing
    }

    @Override
    public void enter(Tree.Set set) {
        //Nothing
    }

    @Override
    public void enter(Tree.Range range) {
        //Nothing
    }

    @Override
    public void enter(Tree.Group group) {
        //Nothing
    }

    @Override
    public void enter(Tree.Unary unary) {
        //Nothing
    }

    @Override
    public void enter(Tree.Or or) {
        //Nothing
    }

    @Override
    public void enter(Tree.And and) {
        //Nothing
    }
}
