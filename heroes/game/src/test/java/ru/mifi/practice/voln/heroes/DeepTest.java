package ru.mifi.practice.voln.heroes;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

/** Проверка глубокой тактики: выбор хода перебором и то, что отсечение его не портит. */
@DisplayName("Глубокая тактика")
final class DeepTest {

    @DisplayName("Соседнюю цель перебор бьёт")
    @Test
    @Timeout(5)
    void attacksTheNeighbourTarget() {
        BattleMap map = new BattleMap();
        map.addLeft(5, 5, stack(Unit.Type.WALKER, 100, 100));
        map.addRight(5, 6, stack(Unit.Type.WALKER, 10, 10));
        assertThat("the search leaves a target standing next to it alone",
            new Tactics.Deep().decide(map).kind(), is(Tactics.Kind.ATTACK));
    }

    @DisplayName("Из двух целей перебор выбирает добиваемую")
    @Test
    @Timeout(5)
    void picksTheTargetItCanFinish() {
        BattleMap map = new BattleMap();
        map.addLeft(5, 5, stack(Unit.Type.WALKER, 100, 100));
        map.addRight(4, 5, stack(Unit.Type.WALKER, 10, 500));
        map.addRight(5, 6, stack(Unit.Type.WALKER, 10, 10));
        assertThat("the search prefers scratching the fresh troop to finishing off the weak one",
            new Tactics.Deep().decide(map).column(), is(6));
    }

    @DisplayName("Далёкую цель перебор не атакует, а сближается")
    @Test
    @Timeout(5)
    void approachesTheDistantTarget() {
        BattleMap map = new BattleMap();
        map.addLeft(5, 0, stack(Unit.Type.WALKER, 100, 100));
        map.addRight(5, 14, stack(Unit.Type.WALKER, 10, 10));
        assertThat("the search stands still while the enemy is out of reach",
            new Tactics.Deep().decide(map).kind(), is(Tactics.Kind.MOVE));
    }

    @DisplayName("На пустом поле перебор пропускает ход")
    @Test
    @Timeout(5)
    void skipsTheTurnOnAnEmptyField() {
        assertThat("the search finds a move on a field without a single troop",
            new Tactics.Deep().decide(new BattleMap()).kind(), is(Tactics.Kind.SKIP));
    }

    @DisplayName("Стрелок предпочитает выстрел подходу")
    @Test
    @Timeout(5)
    void prefersTheShotToTheApproach() {
        BattleMap map = new BattleMap();
        map.addLeft(5, 5, stack(Unit.Type.SHOOTER, 100, 100));
        map.addRight(5, 9, stack(Unit.Type.WALKER, 10, 500));
        assertThat("the shooter walks up to the enemy instead of shooting from where it stands",
            new Tactics.Deep().decide(map).kind(), is(Tactics.Kind.SHOOT));
    }

    @DisplayName("Под ответный удар перебор не подставляется")
    @Test
    @Timeout(5)
    void avoidsTheDeadlyTrade() {
        assertThat("the search takes the kill that costs it the whole troop next half-move",
            new Tactics.Deep().decide(trap()).kind(), is(not(Tactics.Kind.ATTACK)));
    }

    @DisplayName("Жадная тактика на подставу и попадается")
    @Test
    @Timeout(5)
    void showsWhatTheGreedyOneMisses() {
        assertThat("the greedy tactics sees a half-move ahead, though it looks only at its own strike",
            new Tactics.Greedy().decide(trap()).kind(), is(Tactics.Kind.ATTACK));
    }

    /** Сверка идёт при неограниченной ширине: усечение — отдельная от отсечения вольность. */
    @DisplayName("Отсечение не теряет лучшую ветку")
    @Test
    @Timeout(30)
    void keepsTheBestBranch() {
        BattleMap map = crowd();
        Position position = map.snapshot();
        Tactics.Decision chosen = new Tactics.Deep(3, Integer.MAX_VALUE).decide(map);
        int best = candidates(map, position).stream()
            .mapToInt(decision -> reference(position.answer(decision), position.leftTurn(), 2))
            .max().orElseThrow();
        assertThat("the cut off branches take away the best move the plain search finds",
            reference(position.answer(chosen), position.leftTurn(), 2), is(best));
    }

    @DisplayName("Ширина перебора отсекает лишние ветки")
    @Test
    @Timeout(30)
    void readsNoMoreBranchesThanAllowed() {
        BattleMap map = new BattleMap();
        map.fillRandomly(20260814);
        Position position = map.snapshot();
        assertThat("the crowded field offers so few replies that the width has nothing to cut",
            position.replies().size() > 12, is(true));
    }

    @DisplayName("Перебор на полном поле укладывается в отведённое время")
    @Test
    @Timeout(10)
    void thinksWithinTheTime() {
        BattleMap map = new BattleMap();
        map.fillRandomly(20260814);
        assertThat("the search on a full field takes longer than a move may take, or finds nothing to do",
            new Tactics.Deep().decide(map).kind(), is(not(Tactics.Kind.SKIP)));
    }

    /**
     * Бой до конца, а не один ход: решение, которого поле боя не примет, оставило бы очередь
     * стоять на месте, и бой не кончился бы никогда.
     */
    @DisplayName("Бой перебора против жадной тактики доигрывается до победы")
    @Test
    @Timeout(60)
    void playsTheBattleToTheEnd() {
        BattleMap map = new BattleMap();
        map.fillRandomly(20260814);
        Tactics left = new Tactics.Deep();
        Tactics right = new Tactics.Greedy();
        for (int step = 0; step < 5000 && map.outcome() == BattleMap.Outcome.NONE; step++) {
            if (map.isAnimating()) {
                map.endAction();
                continue;
            }
            Tactics tactics = map.isLeftTurn() ? left : right;
            new Battle.Local(map, tactics).apply(tactics.decide(map));
        }
        assertThat("the battle between the two tactics never comes to an end",
            map.outcome(), is(not(BattleMap.Outcome.NONE)));
    }

    /**
     * Подстава: слабый противник стоит так, что добить его можно, только встав на единственную
     * клетку рядом с сильным, — и тот следующим полуходом сносит подошедшего целиком. Сильный
     * зажат камнями и неподвижен, поэтому отряд, оставшийся на месте, ему недосягаем: уклониться
     * от размена и вправду выгоднее, чем разменяться.
     */
    private static BattleMap trap() {
        BattleMap map = new BattleMap();
        map.addObstacle(3, 8);
        map.addObstacle(4, 7);
        map.addObstacle(4, 9);
        Unit.Stack ours = new Unit.Stack(Unit.Type.WALKER);
        ours.add(new Unit(10, 0, 500, 1));
        map.addLeft(5, 7, ours);
        Unit.Stack bait = new Unit.Stack(Unit.Type.WALKER);
        bait.add(new Unit(1, 0, 5, 1));
        map.addRight(5, 9, bait);
        Unit.Stack killer = new Unit.Stack(Unit.Type.WALKER);
        for (int fighter = 0; fighter < 3; fighter++) {
            killer.add(new Unit(500, 0, 100, 0));
        }
        map.addRight(4, 8, killer);
        return map;
    }

    /** Тесное поле: обе стороны стоят вплотную, поэтому перебору есть что считать. */
    private static BattleMap crowd() {
        BattleMap map = new BattleMap();
        map.addLeft(4, 4, stack(Unit.Type.WALKER, 30, 120));
        map.addLeft(6, 4, stack(Unit.Type.SHOOTER, 40, 90));
        map.addRight(4, 6, stack(Unit.Type.WALKER, 25, 110));
        map.addRight(6, 6, stack(Unit.Type.WALKER, 35, 60));
        return map;
    }

    /** Ходы, которые примет живое поле боя: перебор в корне считает только их. */
    private static List<Tactics.Decision> candidates(BattleMap map, Position position) {
        int[] coord = map.getStackCoord(map.getTurnQueue().peekFirst());
        return position.moves().stream().filter(decision -> switch (decision.kind()) {
            case ATTACK -> !map.attackPath(coord[0], coord[1], decision.row(), decision.column()).isEmpty();
            case SHOOT -> map.shot(coord[0], coord[1], decision.row(), decision.column()) > 0;
            case MOVE -> !map.movePath(coord[0], coord[1], decision.row(), decision.column()).isEmpty();
            default -> true;
        }).toList();
    }

    /** Перебор без отсечения: эталон, с которым сверяется отсекающий. */
    private static int reference(Position position, boolean ours, int left) {
        if (left <= 0 || position.over() || position.active().isEmpty()) {
            return position.score(ours);
        }
        boolean gaining = position.leftTurn() == ours;
        int best = gaining ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        for (Position.Reply reply : position.replies()) {
            int value = reference(position.answer(reply), ours, left - 1);
            best = gaining ? Math.max(best, value) : Math.min(best, value);
        }
        return best;
    }

    private static Unit.Stack stack(Unit.Type type, int attack, int health) {
        Unit.Stack stack = new Unit.Stack(type);
        stack.add(new Unit(attack, 0, health, 4));
        return stack;
    }
}
