package ru.mifi.practice.boring.graph;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@DisplayName("Мосты и точки сочленения")
final class LowLinkTest {

    @DisplayName("Ребро между двумя циклами — мост")
    @Test
    @Timeout(1)
    void findsTheBridgeBetweenTwoCycles() {
        assertThat("the bridge between the cycles is missed", new LowLink().bridges(dumbbell()), hasSize(1));
    }

    @DisplayName("Ребро цикла мостом не является")
    @Test
    @Timeout(1)
    void findsNoBridgeInACycle() {
        assertThat("an edge of the cycle passes for a bridge",
            new LowLink().bridges(new Graph(3).both(0, 1, 1).both(1, 2, 1).both(2, 0, 1)), hasSize(0));
    }

    @DisplayName("В цепочке мостами являются все рёбра")
    @Test
    @Timeout(1)
    void findsEveryEdgeOfAChainAsABridge() {
        assertThat("an edge of the chain is not a bridge",
            new LowLink().bridges(new Graph(3).both(0, 1, 1).both(1, 2, 1)), hasSize(2));
    }

    @DisplayName("Вершина между двумя циклами — точка сочленения")
    @Test
    @Timeout(1)
    void findsTheArticulationBetweenTwoCycles() {
        assertThat("the articulation points are wrong",
            new LowLink().articulations(dumbbell()), is(Set.of(2, 3)));
    }

    @DisplayName("Середина цепочки — точка сочленения, а концы — нет")
    @Test
    @Timeout(1)
    void findsTheMiddleOfTheChain() {
        assertThat("the middle of the chain is not an articulation point",
            new LowLink().articulations(new Graph(3).both(0, 1, 1).both(1, 2, 1)), is(Set.of(1)));
    }

    @DisplayName("В цикле точек сочленения нет")
    @Test
    @Timeout(1)
    void findsNoArticulationInACycle() {
        assertThat("a vertex of the cycle passes for an articulation point",
            new LowLink().articulations(new Graph(3).both(0, 1, 1).both(1, 2, 1).both(2, 0, 1)), hasSize(0));
    }

    @DisplayName("Кратное ребро мостом не является")
    @Test
    @Timeout(1)
    void findsNoBridgeInADoubledEdge() {
        assertThat("a doubled edge passes for a bridge",
            new LowLink().bridges(new Graph(2).both(0, 1, 1).both(0, 1, 1)), hasSize(0));
    }

    /** Гантель: два треугольника, соединённые одним ребром, — на нём и держится связность. */
    private static Graph dumbbell() {
        return new Graph(6)
            .both(0, 1, 1).both(1, 2, 1).both(2, 0, 1)
            .both(2, 3, 1)
            .both(3, 4, 1).both(4, 5, 1).both(5, 3, 1);
    }
}
