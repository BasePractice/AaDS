package ru.mifi.practice.voln.heroes;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

/** Проверка снимка позиции: ход отдаёт новую позицию, а прежняя остаётся какой была. */
@DisplayName("Снимок позиции")
final class PositionTest {

    @DisplayName("Снимок поля боя повторяет расстановку")
    @Test
    @Timeout(1)
    void repeatsTheBattleField() {
        BattleMap map = new BattleMap();
        map.addLeft(5, 5, stack(Unit.Type.WALKER, 100, 100));
        map.addRight(5, 6, stack(Unit.Type.WALKER, 10, 10));
        assertThat("the snapshot holds a number of troops other than the field does",
            map.snapshot().troops().size(), is(2));
    }

    @DisplayName("Снимок начинает с того, чья очередь")
    @Test
    @Timeout(1)
    void startsWithTheOneInTurn() {
        BattleMap map = new BattleMap();
        map.addLeft(5, 5, stack(Unit.Type.WALKER, 100, 100));
        map.addRight(5, 6, stack(Unit.Type.WALKER, 10, 10));
        assertThat("the snapshot lets somebody else move first",
            map.snapshot().active().orElseThrow().left(), is(true));
    }

    @DisplayName("Удар добивает слабую цель")
    @Test
    @Timeout(1)
    void finishesOffTheWeakTarget() {
        Position position = position(
            troop(1, 5, 5, true, Unit.Type.WALKER, 100, 100),
            troop(2, 5, 6, false, Unit.Type.WALKER, 10, 10));
        assertThat("the target survives a strike far beyond its health",
            position.after(new Tactics.Decision(Tactics.Kind.ATTACK, 5, 6)).troops().size(), is(1));
    }

    @DisplayName("Уцелевшая цель отвечает ударом")
    @Test
    @Timeout(1)
    void strikesBack() {
        Position position = position(
            troop(1, 5, 5, true, Unit.Type.WALKER, 10, 500),
            troop(2, 5, 6, false, Unit.Type.WALKER, 100, 500));
        assertThat("the target that survived the strike leaves it unanswered",
            position.after(new Tactics.Decision(Tactics.Kind.ATTACK, 5, 6))
                .troops().get(0).squad().totalHealth() < 500, is(true));
    }

    @DisplayName("Ответивший в этом ходу отряд помечен")
    @Test
    @Timeout(1)
    void remembersTheAnswer() {
        Position position = position(
            troop(1, 5, 5, true, Unit.Type.WALKER, 10, 500),
            troop(2, 7, 5, true, Unit.Type.WALKER, 10, 500),
            troop(3, 5, 6, false, Unit.Type.WALKER, 100, 500));
        Position after = position.after(new Tactics.Decision(Tactics.Kind.ATTACK, 5, 6));
        assertThat("the target that answered the strike is ready to answer it once more",
            after.troops().get(2).counterAttacked(), is(true));
    }

    @DisplayName("Новый ход возвращает право на ответ")
    @Test
    @Timeout(1)
    void givesTheAnswerBackOnTheNewTurn() {
        Position position = position(
            troop(1, 5, 5, true, Unit.Type.WALKER, 10, 500),
            troop(2, 5, 6, false, Unit.Type.WALKER, 100, 500));
        Position after = position.after(new Tactics.Decision(Tactics.Kind.ATTACK, 5, 6));
        assertThat("the troop that answered stays unable to answer on the turn of its own",
            after.troops().stream().anyMatch(Position.Troop::counterAttacked), is(false));
    }

    @DisplayName("Выстрел тратит стрелу")
    @Test
    @Timeout(1)
    void spendsTheArrow() {
        Position position = position(
            troop(1, 5, 5, true, Unit.Type.SHOOTER, 100, 100),
            troop(2, 5, 10, false, Unit.Type.WALKER, 10, 500));
        assertThat("the shot leaves the quiver as full as it was",
            position.after(new Tactics.Decision(Tactics.Kind.SHOOT, 5, 10))
                .troops().get(0).squad().shots(), is(4));
    }

    @DisplayName("На выстрел цель не отвечает")
    @Test
    @Timeout(1)
    void leavesTheShotUnanswered() {
        Position position = position(
            troop(1, 5, 5, true, Unit.Type.SHOOTER, 100, 100),
            troop(2, 5, 10, false, Unit.Type.WALKER, 10, 500));
        assertThat("the target hit from afar strikes back, though it has nobody to strike",
            position.after(new Tactics.Decision(Tactics.Kind.SHOOT, 5, 10))
                .troops().get(0).squad().totalHealth(), is(100));
    }

    @DisplayName("Отходившая сторона отдаёт очередь")
    @Test
    @Timeout(1)
    void handsTheTurnOver() {
        Position position = position(
            troop(1, 5, 5, true, Unit.Type.WALKER, 10, 500),
            troop(2, 5, 10, false, Unit.Type.WALKER, 10, 500));
        assertThat("the side that moved with all of its troops keeps the turn",
            position.after(new Tactics.Decision(Tactics.Kind.SKIP, -1, -1)).leftTurn(), is(false));
    }

    @DisplayName("Ход оставляет прежнюю позицию нетронутой")
    @Test
    @Timeout(1)
    void keepsTheFormerPositionWhole() {
        Position position = position(
            troop(1, 5, 5, true, Unit.Type.WALKER, 100, 100),
            troop(2, 5, 6, false, Unit.Type.WALKER, 10, 10));
        position.after(new Tactics.Decision(Tactics.Kind.ATTACK, 5, 6));
        assertThat("a move counted in the mind wipes out the position it was counted on",
            position.troops().size(), is(2));
    }

    @DisplayName("Оценка выше у стороны с большим войском")
    @Test
    @Timeout(1)
    void favoursTheStrongerSide() {
        Position position = position(
            troop(1, 5, 5, true, Unit.Type.WALKER, 10, 500),
            troop(2, 5, 10, false, Unit.Type.WALKER, 10, 100));
        assertThat("the side with the bigger army is valued no higher than the smaller one",
            position.score(true) > position.score(false), is(true));
    }

    @DisplayName("Соседний противник попадает в список ходов")
    @Test
    @Timeout(1)
    void offersTheStrikeAtTheNeighbour() {
        Position position = position(
            troop(1, 5, 5, true, Unit.Type.WALKER, 100, 100),
            troop(2, 5, 6, false, Unit.Type.WALKER, 10, 10));
        assertThat("the enemy standing next to the troop is offered no strike",
            position.moves(), hasItem(new Tactics.Decision(Tactics.Kind.ATTACK, 5, 6)));
    }

    @DisplayName("Недостижимый противник зовёт сближаться")
    @Test
    @Timeout(1)
    void offersTheApproach() {
        Position position = position(
            troop(1, 5, 0, true, Unit.Type.WALKER, 10, 100),
            troop(2, 5, 14, false, Unit.Type.WALKER, 10, 100));
        assertThat("the troop with nobody within reach is offered no step forward",
            position.moves().get(0).kind(), is(Tactics.Kind.MOVE));
    }

    @DisplayName("Проигранный бой перебор считает конченым")
    @Test
    @Timeout(1)
    void countsTheLostBattleOver() {
        Position position = position(
            troop(1, 5, 5, true, Unit.Type.WALKER, 100, 100),
            troop(2, 5, 6, false, Unit.Type.WALKER, 10, 10));
        assertThat("the battle with one side wiped out goes on",
            position.after(new Tactics.Decision(Tactics.Kind.ATTACK, 5, 6)).over(), is(true));
    }

    private static Position position(Position.Troop... troops) {
        boolean[][] empty = new boolean[Constants.ROWS][Constants.COLS];
        return new Position(new Field(empty, empty), List.of(troops), true);
    }

    private static Position.Troop troop(long id, int row, int column, boolean left,
                                        Unit.Type type, int power, int health) {
        Unit.Squad squad = new Unit.Squad(type, List.of(new Unit.Fighter(power, 0, health, 5)), 5);
        return new Position.Troop(id, new Position.Cell(row, column), left, squad, false, false);
    }

    private static Unit.Stack stack(Unit.Type type, int attack, int health) {
        Unit.Stack stack = new Unit.Stack(type);
        stack.add(new Unit(attack, 0, health, 5));
        return stack;
    }
}
