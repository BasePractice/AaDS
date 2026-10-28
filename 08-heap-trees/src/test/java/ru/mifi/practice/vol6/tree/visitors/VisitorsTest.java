package ru.mifi.practice.vol6.tree.visitors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import ru.mifi.practice.vol6.tree.Node;
import ru.mifi.practice.vol6.tree.ParserText;
import ru.mifi.practice.vol6.tree.Tree;
import ru.mifi.practice.vol6.tree.VisitorStrategy;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

/**
 * Проверка посетителей обхода дерева.
 *
 * <p>Дерево во всех проверках одно: корень 1 с потомками 2 и 3, у 2 — потомки 4 и 5. Прямой
 * обход входит в узлы в порядке 1, 2, 4, 5, 3, и от этого порядка зависят все ответы посетителей.
 */
@DisplayName("Посетители обхода дерева")
final class VisitorsTest {

    @DisplayName("Счётчик считает все узлы дерева")
    @Test
    @Timeout(1)
    void countsEveryNode() {
        Count<Integer> count = new Count<>();
        tree().visit(count, new VisitorStrategy.PreOrder<>());
        assertThat("the counter dont count every node", count.count(), is(5));
    }

    @DisplayName("Сброс возвращает счётчик на ноль")
    @Test
    @Timeout(1)
    void clearsTheCounter() {
        Count<Integer> count = new Count<>();
        tree().visit(count, new VisitorStrategy.PreOrder<>());
        count.clear();
        assertThat("clearing dont return the counter to zero", count.count(), is(0));
    }

    @DisplayName("Глубина корня равна нулю")
    @Test
    @Timeout(1)
    void measuresZeroDepthForTheRoot() {
        Tree<Integer> tree = tree();
        Distance<Integer> distance = new Distance<>();
        tree.visit(distance, new VisitorStrategy.PreOrder<>());
        assertThat("the root is not at depth zero", distance.distances().get(tree.find(1)), is(0));
    }

    @DisplayName("Глубина внука равна двум")
    @Test
    @Timeout(1)
    void measuresTwoLevelsToAGrandChild() {
        Tree<Integer> tree = tree();
        Distance<Integer> distance = new Distance<>();
        tree.visit(distance, new VisitorStrategy.PreOrder<>());
        assertThat("a grandchild is not two levels deep", distance.distances().get(tree.find(4)), is(2));
    }

    @DisplayName("Братья лежат на одной глубине")
    @Test
    @Timeout(1)
    void keepsSiblingsAtTheSameDepth() {
        Tree<Integer> tree = tree();
        Distance<Integer> distance = new Distance<>();
        tree.visit(distance, new VisitorStrategy.PreOrder<>());
        assertThat("siblings lie at different depths",
            distance.distances().get(tree.find(2)), is(distance.distances().get(tree.find(3))));
    }

    @DisplayName("Эйлеров обход упоминает каждый узел дважды")
    @Test
    @Timeout(1)
    void mentionsEveryNodeTwice() {
        EulerPath<Integer> euler = new EulerPath<>();
        tree().visit(euler, new VisitorStrategy.PreOrder<>());
        assertThat("the euler tour dont mention every node twice",
            values(euler.path()), contains(1, 2, 4, 4, 5, 5, 2, 3, 3, 1));
    }

    @DisplayName("Сброс очищает эйлеров обход")
    @Test
    @Timeout(1)
    void clearsTheEulerTour() {
        EulerPath<Integer> euler = new EulerPath<>();
        tree().visit(euler, new VisitorStrategy.PreOrder<>());
        euler.clear();
        assertThat("clearing dont empty the euler tour", euler.path().size(), is(0));
    }

    @DisplayName("Корень входит первым и выходит последним")
    @Test
    @Timeout(1)
    void entersTheRootFirstAndLeavesItLast() {
        Tree<Integer> tree = tree();
        OnSubTree<Integer> times = new OnSubTree<>();
        tree.visit(times, new VisitorStrategy.PreOrder<>());
        assertThat("the root dont leave last", times.out(tree.find(1)), is(10));
    }

    /**
     * Времена входа и выхода отвечают на вопрос о вложенности за одно сравнение: узел лежит в
     * поддереве другого тогда и только тогда, когда его отрезок вложен в чужой.
     */
    @DisplayName("Отрезок потомка вложен в отрезок предка")
    @Test
    @Timeout(1)
    void nestsTheDescendantIntervalInTheAncestorOne() {
        Tree<Integer> tree = tree();
        OnSubTree<Integer> times = new OnSubTree<>();
        tree.visit(times, new VisitorStrategy.PreOrder<>());
        assertThat("the descendant interval is not nested in the ancestor one",
            times.in(tree.find(2)) < times.in(tree.find(4)) && times.out(tree.find(4)) < times.out(tree.find(2)),
            is(true));
    }

    @DisplayName("Отрезки братьев не пересекаются")
    @Test
    @Timeout(1)
    void keepsSiblingIntervalsApart() {
        Tree<Integer> tree = tree();
        OnSubTree<Integer> times = new OnSubTree<>();
        tree.visit(times, new VisitorStrategy.PreOrder<>());
        assertThat("the sibling intervals overlap",
            times.out(tree.find(2)) < times.in(tree.find(3)), is(true));
    }

    @DisplayName("Времена входа и выхода дают тот же обход, что и эйлеров")
    @Test
    @Timeout(1)
    void rebuildsTheEulerTourFromTheTimes() {
        OnSubTree<Integer> times = new OnSubTree<>();
        tree().visit(times, new VisitorStrategy.PreOrder<>());
        assertThat("the times dont rebuild the euler tour",
            values(times.times()), contains(1, 2, 4, 4, 5, 5, 2, 3, 3, 1));
    }

    @DisplayName("Рассылающий посетитель доводит события до подписчика")
    @Test
    @Timeout(1)
    void deliversEventsToTheRegisteredVisitor() {
        Sequencer<Integer> sequencer = new Sequencer<>();
        Count<Integer> count = new Count<>();
        sequencer.register(count);
        tree().visit(sequencer, new VisitorStrategy.PreOrder<>());
        assertThat("the sequencer dont deliver events to the registered visitor", count.count(), is(5));
    }

    @DisplayName("Рассылающий посетитель доводит события до всех подписчиков")
    @Test
    @Timeout(1)
    void deliversEventsToEveryRegisteredVisitor() {
        Sequencer<Integer> sequencer = new Sequencer<>();
        Count<Integer> first = new Count<>();
        Count<Integer> second = new Count<>();
        sequencer.register(first);
        sequencer.register(second);
        tree().visit(sequencer, new VisitorStrategy.PreOrder<>());
        assertThat("the sequencer dont deliver events to every registered visitor",
            second.count(), is(first.count()));
    }

    @DisplayName("Отписанный посетитель событий не получает")
    @Test
    @Timeout(1)
    void keepsEventsAwayFromTheUnregisteredVisitor() {
        Sequencer<Integer> sequencer = new Sequencer<>();
        Count<Integer> count = new Count<>();
        sequencer.register(count);
        sequencer.unregister(count);
        tree().visit(sequencer, new VisitorStrategy.PreOrder<>());
        assertThat("the unregistered visitor still gets events", count.count(), is(0));
    }

    private static List<Integer> values(List<Node<Integer>> nodes) {
        List<Integer> values = new ArrayList<>();
        nodes.forEach(node -> values.add(node.value()));
        return values;
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
