package ru.mifi.practice.boring.graph;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Максимальный поток по Диницу")
final class DinicTest {

    @DisplayName("Поток по цепочке равен самой узкой дуге")
    @Test
    @Timeout(1)
    void limitsTheFlowByTheNarrowestEdge() {
        Graph graph = new Graph(3).with(0, 1, 10).with(1, 2, 4);
        assertThat("the flow is not limited by the narrowest edge", new Dinic().flow(graph, 0, 2), is(4));
    }

    @DisplayName("Два независимых пути складываются")
    @Test
    @Timeout(1)
    void addsUpTheIndependentPaths() {
        Graph graph = new Graph(4)
            .with(0, 1, 3).with(1, 3, 3)
            .with(0, 2, 5).with(2, 3, 5);
        assertThat("the independent paths dont add up", new Dinic().flow(graph, 0, 3), is(8));
    }

    @DisplayName("Обратная дуга даёт отменить неудачное решение")
    @Test
    @Timeout(1)
    void undoesTheWrongChoiceByTheBackArc() {
        Graph graph = new Graph(4)
            .with(0, 1, 1).with(0, 2, 1)
            .with(1, 2, 1).with(1, 3, 1).with(2, 3, 1);
        assertThat("the wrong first path cannot be undone", new Dinic().flow(graph, 0, 3), is(2));
    }

    @DisplayName("Без пути из истока в сток потока нет")
    @Test
    @Timeout(1)
    void findsNoFlowWithoutAPath() {
        assertThat("a flow is found where there is no path",
            new Dinic().flow(new Graph(4).with(0, 1, 5).with(2, 3, 5), 0, 3), is(0));
    }

    @DisplayName("Сток, совпадающий с истоком, потока не даёт")
    @Test
    @Timeout(1)
    void findsNoFlowIntoItself() {
        assertThat("a flow into itself is found",
            new Dinic().flow(new Graph(2).with(0, 1, 5), 0, 0), is(0));
    }

    @DisplayName("Классическая сеть даёт известный ответ")
    @Test
    @Timeout(1)
    void solvesTheClassicNetwork() {
        Graph graph = new Graph(6)
            .with(0, 1, 16).with(0, 2, 13)
            .with(1, 2, 10).with(1, 3, 12)
            .with(2, 1, 4).with(2, 4, 14)
            .with(3, 2, 9).with(3, 5, 20)
            .with(4, 3, 7).with(4, 5, 4);
        assertThat("the classic network gives an unexpected flow", new Dinic().flow(graph, 0, 5), is(23));
    }
}
