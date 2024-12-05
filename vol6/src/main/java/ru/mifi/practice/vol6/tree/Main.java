package ru.mifi.practice.vol6.tree;

import ru.mifi.practice.vol6.tree.visitors.OnSubTree;

import java.io.IOException;
import java.util.Comparator;

public abstract class Main {
    public static void main(String[] args) throws IOException {
        Tree<Integer> tree = new ParserText<Integer>().parse(Main.class.getResourceAsStream("/standard.tree"),
            Integer::parseInt, Comparator.comparing(k -> k));
        System.out.print("PRE : ");
        tree.visit(new Visitor.Stdout<>(), new VisitorStrategy.PreOrder<>());
        System.out.println();
        System.out.print("POST: ");
        tree.visit(new Visitor.Stdout<>(), new VisitorStrategy.PostOrder<>());
        System.out.println();
        System.out.print("IN  : ");
        tree.visit(new Visitor.Stdout<>(), new VisitorStrategy.InOrder<>());
        System.out.println();
        OnSubTree<Integer> on = new OnSubTree<>();
        tree.visit(on, new VisitorStrategy.PreOrder<>());
        System.out.println(on);
        System.out.println(tree);
    }
}
