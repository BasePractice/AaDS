package ru.mifi.practice.vol6.tree;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

/**
 * Проверка порядков обхода дерева.
 *
 * <p>Дерево одно: корень 1 с потомками 2 и 3, у 2 — потомки 4 и 5. Три классических порядка
 * различаются только тем, когда узел попадает между своими потомками, и это здесь и проверяется.
 */
@DisplayName("Порядок обхода дерева")
final class VisitorStrategyTest {

    @DisplayName("Прямой обход берёт узел раньше потомков")
    @Test
    @Timeout(1)
    void takesTheNodeBeforeItsChildren() {
        assertThat("the pre order walk dont take the node before its children",
            entered(new VisitorStrategy.PreOrder<>()), contains(1, 2, 4, 5, 3));
    }

    @DisplayName("Симметричный обход берёт узел между потомками")
    @Test
    @Timeout(1)
    void takesTheNodeBetweenItsChildren() {
        assertThat("the in order walk dont take the node between its children",
            entered(new VisitorStrategy.InOrder<>()), contains(4, 2, 5, 1, 3));
    }

    @DisplayName("Обратный обход берёт узел после потомков")
    @Test
    @Timeout(1)
    void takesTheNodeAfterItsChildren() {
        assertThat("the post order walk dont take the node after its children",
            entered(new VisitorStrategy.PostOrder<>()), contains(4, 5, 2, 3, 1));
    }

    @DisplayName("Помеченный заранее узел обходом пропускается")
    @Test
    @Timeout(1)
    void skipsAPreMarkedNode() {
        Tree<Integer> tree = tree();
        VisitorStrategy.AlreadyVisited<Integer> strategy =
            new VisitorStrategy.AlreadyVisited<>(new VisitorStrategy.PreOrder<>());
        strategy.put(tree.find(2));
        List<Integer> order = new ArrayList<>();
        tree.visit(recorder(order), strategy);
        assertThat("a pre marked node is still walked", order, contains(1, 3));
    }

    @DisplayName("Повторный обход не повторяет уже посещённые узлы")
    @Test
    @Timeout(1)
    void walksEveryNodeAtMostOnce() {
        Tree<Integer> tree = tree();
        VisitorStrategy.AlreadyVisited<Integer> strategy =
            new VisitorStrategy.AlreadyVisited<>(new VisitorStrategy.PreOrder<>());
        List<Integer> order = new ArrayList<>();
        tree.visit(recorder(order), strategy);
        tree.visit(recorder(order), strategy);
        assertThat("a repeated walk visits the same nodes again", order.size(), is(5));
    }

    private static List<Integer> entered(VisitorStrategy<Integer> strategy) {
        List<Integer> order = new ArrayList<>();
        tree().visit(recorder(order), strategy);
        return order;
    }

    private static Visitor<Integer> recorder(List<Integer> order) {
        return new Visitor<>() {
            @Override
            public void enterNode(Node<Integer> node) {
                order.add(node.value());
            }

            @Override
            public void exitNode(Node<Integer> node) {
                //отмечать выход здесь незачем
            }

            @Override
            public void empty() {
                //пустое дерево проверяется отдельно
            }
        };
    }

    private static Tree<Integer> tree() {
        try {
            return new ParserText<Integer>()
                .parse("1:{2,3}\n2:{4,5}\n", Integer::valueOf, Comparator.naturalOrder());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
