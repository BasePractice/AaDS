package ru.mifi.practice.vol6.tree;

import ru.mifi.practice.vol6.tree.paths.LowestCommonAncestorPath;
import ru.mifi.practice.vol6.tree.paths.MergePath;
import ru.mifi.practice.vol6.tree.visitors.Distance;
import ru.mifi.practice.vol6.tree.visitors.EulerPath;
import ru.mifi.practice.vol6.tree.visitors.OnSubTree;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

public abstract class Main {
    public static void main(String[] args) throws IOException {
        Tree<Integer> tree = new ParserText<Integer>().parse(Main.class.getResourceAsStream("/standard.tree"),
            Integer::parseInt, Comparator.comparing(k -> k));
        System.out.print("PRE  : ");
        tree.visit(new Visitor.Stdout<>(), new VisitorStrategy.PreOrder<>());
        System.out.println();
        System.out.print("POST : ");
        tree.visit(new Visitor.Stdout<>(), new VisitorStrategy.PostOrder<>());
        System.out.println();
        System.out.print("IN   : ");
        tree.visit(new Visitor.Stdout<>(), new VisitorStrategy.InOrder<>());
        System.out.println();
        OnSubTree<Integer> on = new OnSubTree<>();
        tree.visit(on, new VisitorStrategy.PreOrder<>());
        System.out.println("TIMED: ");
        System.out.println(on);
        Distance<Integer> distance = new Distance<>();
        tree.visit(distance, new VisitorStrategy.PreOrder<>());
        System.out.print("DIST : ");
        System.out.println(distance.distances());
        MergePath<Integer> merge = new MergePath<>();
        List<Node<Integer>> path = merge.path(tree, 4, 7);
        System.out.print("PATH1: ");
        System.out.println(path);
        LowestCommonAncestorPath<Integer> lcap = new LowestCommonAncestorPath<>();
        path = lcap.path(tree, 4, 7);
        System.out.print("PATH2: ");
        System.out.println(path);
        Diameter<Integer> diameter = new Diameter<>();
        path = diameter.path(tree);
        System.out.print("DIAM : ");
        System.out.println(path);
        EulerPath<Integer> euler = new EulerPath<>();
        System.out.print("EULER: ");
        tree.visit(euler, new VisitorStrategy.PreOrder<>());
        System.out.println(euler);
        Center<Integer> center = new Center<>();
        List<Node<Integer>> centered = center.center(tree, integer -> integer);
        System.out.print("CENTR: ");
        System.out.println(centered);
        Centroid<Integer> centroid = new Centroid<>();
        var c = centroid.centroid(tree);
        System.out.print("CEOID: ");
        System.out.println(c);
        System.out.print("HASH : ");
        System.out.println(tree.hash());
        System.out.println("HEAP : ");
        Heap<Integer> heap = new Heap.Minimum<>(10);
        heap
            .add(20)
            .add(3)
            .add(25)
            .add(5)
            .add(7)
            .add(10)
            .add(15)
            .add(21);
        heap.refresh();
        heap.print();
        for (int i = 0; i < 5; i++) {
            System.out.print("  MIN: ");
            System.out.println(heap.top());
            System.out.print("  DEL: ");
            System.out.println(heap.deleteRoot());
            heap.print();
        }
    }
}
