package ru.mifi.practice.voln.fsm;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Проверка клеточного автомата «Жизнь» на трёх канонических фигурах.
 *
 * <p>Поле тороидальное, поэтому размер берётся с запасом: на тесном поле фигура видит саму себя
 * через край и правила выглядят нарушенными, хотя нарушены не они, а условия проверки.
 */
@DisplayName("Игра «Жизнь»")
final class GoLTest {

    @DisplayName("Одинокая клетка погибает")
    @Test
    @Timeout(1)
    void killsALonelyCell() {
        BytesMatrix matrix = BytesMatrix.defaults(8, 8);
        matrix.set(3, 3, (byte) 1);
        assertThat("a lonely cell survives", alive(ticked(matrix)), is(0));
    }

    @DisplayName("Квадрат остаётся неподвижным")
    @Test
    @Timeout(1)
    void keepsABlockStill() {
        BytesMatrix matrix = BytesMatrix.defaults(8, 8);
        matrix.set(3, 3, (byte) 1);
        matrix.set(3, 4, (byte) 1);
        matrix.set(4, 3, (byte) 1);
        matrix.set(4, 4, (byte) 1);
        assertThat("the block dont stay still", cells(ticked(matrix)), is(List.of("3,3", "3,4", "4,3", "4,4")));
    }

    @DisplayName("Горизонтальный мигалка становится вертикальным")
    @Test
    @Timeout(1)
    void turnsTheBlinker() {
        BytesMatrix matrix = BytesMatrix.defaults(8, 8);
        matrix.set(3, 2, (byte) 1);
        matrix.set(3, 3, (byte) 1);
        matrix.set(3, 4, (byte) 1);
        assertThat("the blinker dont turn", cells(ticked(matrix)), is(List.of("2,3", "3,3", "4,3")));
    }

    @DisplayName("Мигалка возвращается к себе за два шага")
    @Test
    @Timeout(1)
    void returnsTheBlinkerInTwoTicks() {
        BytesMatrix matrix = BytesMatrix.defaults(8, 8);
        matrix.set(3, 2, (byte) 1);
        matrix.set(3, 3, (byte) 1);
        matrix.set(3, 4, (byte) 1);
        GoL game = new GoL(matrix);
        List<String> seen = new ArrayList<>();
        game.tick(null);
        game.tick((tick, next) -> seen.addAll(cells(next)));
        assertThat("the blinker dont return to itself in two ticks", seen, is(List.of("3,2", "3,3", "3,4")));
    }

    @DisplayName("Наблюдателю приходит номер шага")
    @Test
    @Timeout(1)
    void tellsTheObserverTheTickNumber() {
        GoL game = new GoL(BytesMatrix.defaults(8, 8));
        List<Integer> ticks = new ArrayList<>();
        game.tick((tick, matrix) -> ticks.add(tick));
        game.tick((tick, matrix) -> ticks.add(tick));
        assertThat("the observer dont get the tick number", ticks, is(List.of(1, 2)));
    }

    private static BytesMatrix ticked(BytesMatrix matrix) {
        List<BytesMatrix> next = new ArrayList<>();
        new GoL(matrix).tick((tick, result) -> next.add(result));
        return next.get(0);
    }

    private static int alive(BytesMatrix matrix) {
        return cells(matrix).size();
    }

    private static List<String> cells(BytesMatrix matrix) {
        List<String> alive = new ArrayList<>();
        for (int row = 0; row < matrix.rows(); row++) {
            for (int col = 0; col < matrix.cols(); col++) {
                if (matrix.get(row, col) == 1) {
                    alive.add(row + "," + col);
                }
            }
        }
        return alive;
    }
}
