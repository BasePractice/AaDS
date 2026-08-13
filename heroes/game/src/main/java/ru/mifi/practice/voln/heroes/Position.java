package ru.mifi.practice.voln.heroes;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Снимок позиции: где стоят отряды, чей ход и кто из них в этом ходу уже отходил.
 *
 * <p>Позиция в себе ничего не меняет: ход отдаёт новую позицию, а прежняя остаётся какой была,
 * поэтому перебору вариантов не нужен откат — он просто забывает ненужную ветку. Поле боя такого
 * не умеет: оно рисует, ведёт журнал и бросает кости, а перебору нужны тишина и повторяемость.
 *
 * <p>Отсюда и второе отличие: в снимке удар считается верхней границей разброса, поэтому одна и
 * та же позиция оценивается всегда одинаково.
 */
record Position(Field terrain, List<Troop> troops, boolean leftTurn) {
    private static final int CASUALTY_WEIGHT = 100;
    private static final int[] NEIGHBOUR_ROWS = {0, 0, 1, -1};
    private static final int[] NEIGHBOUR_COLUMNS = {1, -1, 0, 0};

    Position(Field terrain, List<Troop> troops, boolean leftTurn) {
        this.terrain = terrain;
        this.troops = List.copyOf(troops);
        this.leftTurn = leftTurn;
    }

    /** Отряд, чей сейчас ход, или пусто, если ходить этой стороне некем. */
    Optional<Troop> active() {
        return troops.stream().filter(troop -> troop.left() == leftTurn && !troop.acted()).findFirst();
    }

    /** Кончился ли бой: одна из сторон исчезла с поля. */
    boolean over() {
        return troops.stream().noneMatch(Troop::left) || troops.stream().allMatch(Troop::left);
    }

    /**
     * Оценка позиции глазами стороны: свои бойцы и их здоровье за вычетом того же у противника.
     * Гибель бойца весит на два порядка больше царапины, поэтому размен здоровья на жизни перебор
     * считает выигрышем.
     */
    int score(boolean left) {
        return material(left) - material(!left);
    }

    /**
     * Осмысленные ходы отряда, чья очередь: выстрел и удар по каждому достижимому противнику,
     * шаг к ближайшему, если не достаётся никто, и пропуск.
     *
     * <p>Перебирать все клетки поля незачем: ход, не меняющий ни здоровья, ни расстояний, ветку
     * перебора только удлиняет. Достижимость берётся из одного обхода поля на позицию — противник
     * достаётся, если хоть одна свободная клетка рядом с ним отстоит не дальше скорости.
     */
    List<Tactics.Decision> moves() {
        Optional<Troop> active = active();
        if (active.isEmpty()) {
            return List.of();
        }
        Troop troop = active.get();
        Field field = field();
        int[][] distances = field.distances(troop.cell().row(), troop.cell().column(), troop.flying());
        List<Tactics.Decision> moves = new ArrayList<>();
        for (Troop enemy : troops) {
            if (enemy.left() == leftTurn) {
                continue;
            }
            if (volley(troop, enemy, field) > 0) {
                moves.add(enemy.cell().decision(Tactics.Kind.SHOOT));
            }
            if (melee(troop, enemy, distances).isPresent()) {
                moves.add(enemy.cell().decision(Tactics.Kind.ATTACK));
            }
        }
        approach(troop, field, distances).ifPresent(moves::add);
        moves.add(new Tactics.Decision(Tactics.Kind.SKIP, -1, -1));
        return moves;
    }

    /**
     * Позиция после хода. Удар ложится сразу вместе с ответом цели, а когда сторона отходила
     * всеми отрядами, очередь переходит к противнику и все встают в неё заново.
     */
    Position after(Tactics.Decision decision) {
        Optional<Troop> active = active();
        if (active.isEmpty()) {
            return this;
        }
        Troop troop = active.get();
        List<Troop> next = switch (decision.kind()) {
            case ATTACK -> attacked(troop, decision);
            case SHOOT -> shot(troop, decision);
            case MOVE -> moved(troop, decision);
            default -> replaced(troop.done());
        };
        return turned(next);
    }

    /** Сила выстрела по противнику или ноль, если стрелять нельзя. */
    private int volley(Troop troop, Troop enemy, Field field) {
        Cell from = troop.cell();
        Cell to = enemy.cell();
        if (adjoined(troop, field) || !field.visible(from.row(), from.column(), to.row(), to.column())) {
            return 0;
        }
        return troop.squad().maximumShot(from.away(to));
    }

    /** Стоит ли рядом с отрядом противник: вплотную стрелку не размахнуться. */
    private boolean adjoined(Troop troop, Field field) {
        boolean[][] enemies = new boolean[Constants.ROWS][Constants.COLS];
        troops.stream().filter(other -> other.left() != troop.left())
            .forEach(other -> enemies[other.cell().row()][other.cell().column()] = true);
        return field.adjoins(troop.cell().row(), troop.cell().column(), enemies);
    }

    /**
     * Клетка, с которой отряд достанет противника, или пусто, если не достанет вовсе: ближайшая
     * свободная клетка рядом с целью, до которой хватает скорости.
     */
    private Optional<Cell> melee(Troop troop, Troop enemy, int[][] distances) {
        return around(enemy, distances, troop.squad().speed() + 1);
    }

    /** Шаг на всю доступную скорость к ближайшему противнику. */
    private Optional<Tactics.Decision> approach(Troop troop, Field field, int[][] distances) {
        Optional<Cell> aim = Optional.empty();
        int best = Integer.MAX_VALUE;
        for (Troop enemy : troops) {
            if (enemy.left() == leftTurn) {
                continue;
            }
            Optional<Cell> cell = around(enemy, distances, best);
            if (cell.isPresent()) {
                best = cell.get().at(distances);
                aim = cell;
            }
        }
        if (aim.isEmpty()) {
            return Optional.empty();
        }
        Cell cell = aim.get();
        List<int[]> path = field.trace(distances, troop.cell().row(), troop.cell().column(),
            cell.row(), cell.column());
        int step = Math.min(troop.squad().speed(), path.size() - 1);
        if (step <= 0) {
            return Optional.empty();
        }
        return Optional.of(new Cell(path.get(step)[0], path.get(step)[1]).decision(Tactics.Kind.MOVE));
    }

    /** Ближайшая к отряду свободная клетка рядом с противником, если она ближе заданного предела. */
    private Optional<Cell> around(Troop enemy, int[][] distances, int limit) {
        Optional<Cell> found = Optional.empty();
        int best = limit;
        for (int i = 0; i < NEIGHBOUR_ROWS.length; i++) {
            int row = enemy.cell().row() + NEIGHBOUR_ROWS[i];
            int column = enemy.cell().column() + NEIGHBOUR_COLUMNS[i];
            if (Field.isInside(row, column) && distances[row][column] < best) {
                best = distances[row][column];
                found = Optional.of(new Cell(row, column));
            }
        }
        return found;
    }

    /** Отряды после удара вплотную: подход к цели, урон и ответ уцелевших. */
    private List<Troop> attacked(Troop troop, Tactics.Decision decision) {
        Optional<Troop> aim = at(decision.row(), decision.column());
        if (aim.isEmpty()) {
            return replaced(troop.done());
        }
        Troop enemy = aim.get();
        Optional<Cell> cell = melee(troop, enemy,
            field().distances(troop.cell().row(), troop.cell().column(), troop.flying()));
        Troop striker = cell.map(troop::moved).orElse(troop).done();
        Unit.Squad hit = enemy.squad().damaged(striker.squad().maximumMelee());
        if (hit.isEmpty()) {
            return without(enemy, replaced(striker));
        }
        Troop beaten = enemy.struck(hit).countered();
        if (enemy.counterAttacked()) {
            return replaced(striker, enemy.struck(hit));
        }
        Troop answered = striker.struck(striker.squad().damaged(hit.maximumMelee()));
        if (answered.squad().isEmpty()) {
            return without(troop, replaced(beaten));
        }
        return replaced(answered, beaten);
    }

    /** Отряды после выстрела: цель теряет бойцов, а стрелок — стрелу, и на удар издали не отвечает. */
    private List<Troop> shot(Troop troop, Tactics.Decision decision) {
        Optional<Troop> aim = at(decision.row(), decision.column());
        if (aim.isEmpty()) {
            return replaced(troop.done());
        }
        Troop enemy = aim.get();
        int force = volley(troop, enemy, field());
        if (force <= 0) {
            return replaced(troop.done());
        }
        Troop striker = troop.struck(troop.squad().spent()).done();
        Unit.Squad hit = enemy.squad().damaged(force);
        if (hit.isEmpty()) {
            return without(enemy, replaced(striker));
        }
        return replaced(striker, enemy.struck(hit));
    }

    /** Отряды после перемещения: занятая клетка ход отменяет, но очередь всё равно уходит дальше. */
    private List<Troop> moved(Troop troop, Tactics.Decision decision) {
        if (at(decision.row(), decision.column()).isPresent()) {
            return replaced(troop.done());
        }
        return replaced(troop.moved(new Cell(decision.row(), decision.column())).done());
    }

    private Optional<Troop> at(int row, int column) {
        return troops.stream().filter(troop -> troop.cell().row() == row && troop.cell().column() == column)
            .findFirst();
    }

    /** Поле, где один отряд заменён на себя же в новом состоянии. */
    private List<Troop> replaced(Troop next) {
        return troops.stream().map(troop -> troop.id() == next.id() ? next : troop).toList();
    }

    /** Поле, где заменены двое: ударивший и его цель. */
    private List<Troop> replaced(Troop next, Troop beaten) {
        return troops.stream()
            .map(troop -> troop.id() == next.id() ? next : troop)
            .map(troop -> troop.id() == beaten.id() ? beaten : troop)
            .toList();
    }

    /** Поле без павшего отряда. */
    private List<Troop> without(Troop fallen, List<Troop> rest) {
        return rest.stream().filter(troop -> troop.id() != fallen.id()).toList();
    }

    /** Позиция после хода: пока стороне есть кем ходить, очередь остаётся у неё. */
    private Position turned(List<Troop> next) {
        Position position = new Position(terrain, next, leftTurn);
        if (position.over() || position.active().isPresent()) {
            return position;
        }
        return new Position(terrain, ready(next), !leftTurn);
    }

    /** Все встают в очередь заново — так же, как поле боя перестраивает её на смене хода. */
    private List<Troop> ready(List<Troop> next) {
        return next.stream()
            .sorted(Comparator.comparingInt((Troop troop) -> troop.cell().row())
                .thenComparingInt(troop -> troop.cell().column()))
            .map(Troop::rested)
            .toList();
    }

    private int material(boolean left) {
        return troops.stream().filter(troop -> troop.left() == left)
            .mapToInt(troop -> troop.squad().size() * CASUALTY_WEIGHT + troop.squad().totalHealth()).sum();
    }

    /** Поле с расставленными на нём отрядами: занятая клетка непроходима. */
    private Field field() {
        boolean[][] occupied = new boolean[Constants.ROWS][Constants.COLS];
        troops.forEach(troop -> occupied[troop.cell().row()][troop.cell().column()] = true);
        return terrain.with(occupied);
    }

    /** Клетка поля. */
    record Cell(int row, int column) {

        /** Насколько клетка далека: шаг наискось стоит столько же, сколько прямой. */
        int away(Cell other) {
            return Field.distance(row, column, other.row(), other.column());
        }

        /** Что размечено на этой клетке обходом поля. */
        int at(int[][] distances) {
            return distances[row][column];
        }

        /** Решение тактики об этой клетке. */
        Tactics.Decision decision(Tactics.Kind kind) {
            return new Tactics.Decision(kind, row, column);
        }
    }

    /**
     * Отряд в снимке позиции: свой номер, клетка, сторона, бойцы и то, что отряд успел сделать
     * в этом ходу. Номер нужен, чтобы отличить отряд от точно такого же соседнего: в снимке
     * отряды сравниваются по значению, а не по ссылке.
     */
    record Troop(long id, Cell cell, boolean left, Unit.Squad squad, boolean acted, boolean counterAttacked) {

        /** Летает ли отряд: летун проходит над камнями. */
        boolean flying() {
            return squad.type() == Unit.Type.FLYER;
        }

        Troop moved(Cell next) {
            return new Troop(id, next, left, squad, acted, counterAttacked);
        }

        Troop struck(Unit.Squad next) {
            return new Troop(id, cell, left, next, acted, counterAttacked);
        }

        /** Отряд, который в этом ходу уже отходил. */
        Troop done() {
            return new Troop(id, cell, left, squad, true, counterAttacked);
        }

        /** Отряд, который на удар уже ответил. */
        Troop countered() {
            return new Troop(id, cell, left, squad, acted, true);
        }

        /** Отряд на новом ходу: ходить снова можно, и ответить снова тоже. */
        Troop rested() {
            return new Troop(id, cell, left, squad, false, false);
        }
    }
}
