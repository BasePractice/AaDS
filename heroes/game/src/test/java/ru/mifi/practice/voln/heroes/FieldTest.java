package ru.mifi.practice.voln.heroes;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/** Проверка геометрии поля: проходимость, путь, видимость и расстояние. */
@DisplayName("Геометрия поля")
final class FieldTest {

    @DisplayName("Пеший обходит камень")
    @Test
    @Timeout(1)
    void walksAroundTheStone() {
        boolean[][] stones = new boolean[Constants.ROWS][Constants.COLS];
        stones[0][1] = true;
        Field field = new Field(stones, new boolean[Constants.ROWS][Constants.COLS]);
        assertThat("the walker goes through the stone instead of around it",
            field.distances(0, 0, false)[0][2], is(4));
    }

    @DisplayName("Летун проходит над камнем")
    @Test
    @Timeout(1)
    void fliesOverTheStone() {
        boolean[][] stones = new boolean[Constants.ROWS][Constants.COLS];
        stones[0][1] = true;
        Field field = new Field(stones, new boolean[Constants.ROWS][Constants.COLS]);
        assertThat("the flyer goes around the stone instead of over it",
            field.distances(0, 0, true)[0][2], is(2));
    }

    @DisplayName("Занятая клетка непроходима")
    @Test
    @Timeout(1)
    void keepsTheOccupiedCellImpassable() {
        boolean[][] troops = new boolean[Constants.ROWS][Constants.COLS];
        troops[0][1] = true;
        Field field = new Field(new boolean[Constants.ROWS][Constants.COLS], troops);
        assertThat("the path goes through a cell held by somebody else",
            field.distances(0, 0, true)[0][2], is(4));
    }

    @DisplayName("До клетки цели путь проложен")
    @Test
    @Timeout(1)
    void reachesTheCellOfTheTarget() {
        boolean[][] troops = new boolean[Constants.ROWS][Constants.COLS];
        troops[0][2] = true;
        Field field = new Field(new boolean[Constants.ROWS][Constants.COLS], troops);
        assertThat("the target cell stays unreachable, so nobody can be attacked",
            field.path(0, 0, 0, 2, false).size(), is(3));
    }

    @DisplayName("Пути через отгороженное поле нет")
    @Test
    @Timeout(1)
    void findsNoPathBehindTheWall() {
        boolean[][] stones = new boolean[Constants.ROWS][Constants.COLS];
        for (int row = 0; row < Constants.ROWS; row++) {
            stones[row][1] = true;
        }
        Field field = new Field(stones, new boolean[Constants.ROWS][Constants.COLS]);
        assertThat("a path is found where a wall of stones cuts the field in two",
            field.path(0, 0, 0, 2, false).isEmpty(), is(true));
    }

    @DisplayName("Стрела не видит цель за камнем")
    @Test
    @Timeout(1)
    void losesTheTargetBehindTheStone() {
        boolean[][] stones = new boolean[Constants.ROWS][Constants.COLS];
        stones[0][1] = true;
        Field field = new Field(stones, new boolean[Constants.ROWS][Constants.COLS]);
        assertThat("the arrow flies through the stone standing on its way",
            field.visible(0, 0, 0, 2), is(false));
    }

    @DisplayName("Отряд не заслоняет цель от стрелы")
    @Test
    @Timeout(1)
    void shootsOverTheTroopOnTheWay() {
        boolean[][] troops = new boolean[Constants.ROWS][Constants.COLS];
        troops[0][1] = true;
        Field field = new Field(new boolean[Constants.ROWS][Constants.COLS], troops);
        assertThat("a troop on the way hides the target, though only stones do that",
            field.visible(0, 0, 0, 2), is(true));
    }

    @DisplayName("Шаг наискось стоит столько же, сколько прямой")
    @Test
    @Timeout(1)
    void countsTheDiagonalStepAsAStraightOne() {
        assertThat("the diagonal step costs more than the straight one",
            Field.distance(0, 0, 3, 3), is(3));
    }

    @DisplayName("Клетка за краем поля наружу не пускает")
    @Test
    @Timeout(1)
    void keepsTheCellOutsideTheField() {
        assertThat("a cell beyond the edge counts as a cell of the field",
            Field.isInside(Constants.ROWS, 0), is(false));
    }

    @DisplayName("Соседство с чужой клеткой видно")
    @Test
    @Timeout(1)
    void seesTheNeighbourCell() {
        boolean[][] cells = new boolean[Constants.ROWS][Constants.COLS];
        cells[5][6] = true;
        Field field = new Field(new boolean[Constants.ROWS][Constants.COLS], cells);
        assertThat("the neighbour standing next to the cell goes unnoticed",
            field.adjoins(5, 5, cells), is(true));
    }

    @DisplayName("Копия камней от чужих правок не зависит")
    @Test
    @Timeout(1)
    void keepsTheStonesOfItsOwn() {
        boolean[][] stones = new boolean[Constants.ROWS][Constants.COLS];
        Field field = new Field(stones, new boolean[Constants.ROWS][Constants.COLS]);
        stones[0][1] = true;
        assertThat("a stone put after the field was built changes what the field sees",
            field.isStone(0, 1), is(false));
    }
}
