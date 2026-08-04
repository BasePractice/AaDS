package ru.mifi.practice.voln.heroes;

import lombok.Getter;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.NavigableMap;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;

/** Поле боя: расстановка отрядов, очередь ходов, перемещение и бой. */
public final class BattleMap {
    private static final int ROWS = 11;
    private static final int COLS = 15;
    private static final int MIN_STACKS = 5;
    private static final int RANDOM_STACKS = 3;
    private static final int MAX_COL_INDEX = 14;
    private static final int MIN_UNITS = 1;
    private static final int RANDOM_UNITS = 10;
    private static final int MIN_SPEED = 3;
    private static final int RANDOM_SPEED = 5;
    private static final int MIN_ATTACK = 10;
    private static final int RANDOM_ATTACK = 20;
    private static final int MIN_DEFENSE = 5;
    private static final int RANDOM_DEFENSE = 10;
    private static final int MIN_HEALTH = 50;
    private static final int RANDOM_HEALTH = 50;
    private static final int DIRECTIONS_COUNT = 4;
    private static final int OBSTACLES_COUNT_BASE = 5;
    private static final int OBSTACLES_COUNT_RANDOM = 5;
    private static final int OBSTACLES_COL_MIN = 5;
    private static final int OBSTACLES_COL_RANDOM = 5;

    private final AtomicLong idCount = new AtomicLong(0);
    private final NavigableMap<Long, StackKey> left = new TreeMap<>();
    private final NavigableMap<Long, StackKey> right = new TreeMap<>();
    private final Long[][] map = new Long[ROWS][COLS];
    private final boolean[][] obstacles = new boolean[ROWS][COLS];
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    @Getter
    private final Deque<Long> turnQueue = new ArrayDeque<>();
    @Getter
    private boolean leftTurn = true;
    @Getter
    private boolean animating;
    private boolean engaged;
    private int[] pendingAttack;
    private int[] pendingShot;

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void addLeft(int row, int col, Unit.Stack stack) {
        if (map[row][col] != null) {
            throw new IllegalArgumentException("клетка уже занята");
        }
        long id = idCount.getAndIncrement();
        left.put(id, new StackKey(id, stack));
        map[row][col] = id;
        engage();
        fillTurnQueue();
        support.firePropertyChange("map", null, null);
    }

    public void addRight(int row, int col, Unit.Stack stack) {
        if (map[row][col] != null) {
            throw new IllegalArgumentException("клетка уже занята");
        }
        long id = idCount.getAndIncrement();
        right.put(id, new StackKey(id, stack));
        map[row][col] = id;
        engage();
        fillTurnQueue();
        support.firePropertyChange("map", null, null);
    }

    /**
     * Исход боя: побеждает сторона, у которой на поле остались отряды. Пока обе стороны стоят —
     * или пока хотя бы одна ещё не вышла на поле — исхода нет.
     */
    public Outcome outcome() {
        if (!engaged || left.isEmpty() == right.isEmpty()) {
            return Outcome.NONE;
        }
        return right.isEmpty() ? Outcome.LEFT : Outcome.RIGHT;
    }

    private void engage() {
        engaged = engaged || !left.isEmpty() && !right.isEmpty();
    }

    public void fillRandomly() {
        fill(new Random());
    }

    /**
     * Расстановка по зерну генератора. Сетевой бой раздаёт обоим игрокам одно зерно, и поле
     * получается одинаковым, хотя карта строится на каждой машине независимо.
     */
    public void fillRandomly(long seed) {
        fill(new Random(seed));
    }

    private void fill(Random random) {
        generateObstacles(random);
        int stackCount = MIN_STACKS + random.nextInt(RANDOM_STACKS);
        for (int i = 0; i < stackCount; i++) {
            int row;
            do {
                row = random.nextInt(ROWS);
            } while (map[row][0] != null);
            addLeft(row, 0, createRandomStack(random));
            do {
                row = random.nextInt(ROWS);
            } while (map[row][MAX_COL_INDEX] != null);
            addRight(row, MAX_COL_INDEX, createRandomStack(random));
        }
        fillTurnQueue();
    }

    public void fillTurnQueue() {
        turnQueue.clear();
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                Long id = map[r][c];
                if (id != null) {
                    if (leftTurn && left.containsKey(id)) {
                        turnQueue.add(id);
                    } else if (!leftTurn && right.containsKey(id)) {
                        turnQueue.add(id);
                    }
                }
            }
        }
    }

    public void waitTurn() {
        if (turnQueue.isEmpty() || animating) {
            return;
        }
        Long id = turnQueue.pollFirst();
        turnQueue.addLast(id);
        support.firePropertyChange("map", null, null);
    }

    public void skipTurn() {
        if (turnQueue.isEmpty() || animating) {
            return;
        }
        Long id = turnQueue.pollFirst();
        Unit.Stack stack = getStackById(id);
        if (stack != null) {
            stack.setActed(true);
            String message = String.format("%s %s пропустил ход",
                leftTurn ? "Левый" : "Правый", stack.getType().getName());
            support.firePropertyChange("log", null, message);
        }
        checkTurnEnd();
        support.firePropertyChange("map", null, null);
    }

    public Unit.Stack getStackById(Long id) {
        if (id == null) {
            return null;
        }
        StackKey key = left.get(id);
        if (key == null) {
            key = right.get(id);
        }
        return key != null ? key.stack : null;
    }

    public int[] getStackCoord(Long id) {
        if (id == null) {
            return new int[0];
        }
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (id.equals(map[r][c])) {
                    return new int[]{r, c};
                }
            }
        }
        return new int[0];
    }

    private Unit.Stack createRandomStack(Random random) {
        Unit.Type[] types = Unit.Type.values();
        Unit.Type type = types[random.nextInt(types.length)];
        Unit.Stack stack = new Unit.Stack(type);
        int count = MIN_UNITS + random.nextInt(RANDOM_UNITS);
        int speed = MIN_SPEED + random.nextInt(RANDOM_SPEED);
        for (int i = 0; i < count; i++) {
            stack.add(new Unit(MIN_ATTACK + random.nextInt(RANDOM_ATTACK),
                MIN_DEFENSE + random.nextInt(RANDOM_DEFENSE),
                MIN_HEALTH + random.nextInt(RANDOM_HEALTH), speed));
        }
        return stack;
    }

    public Unit.Stack getStack(int row, int col) {
        Long id = map[row][col];
        if (id == null) {
            return null;
        }
        StackKey key = left.get(id);
        if (key == null) {
            key = right.get(id);
        }
        return key != null ? key.stack : null;
    }

    public boolean isLeft(int row, int col) {
        Long id = map[row][col];
        return id != null && left.containsKey(id);
    }

    public boolean isObstacle(int row, int col) {
        return obstacles[row][col];
    }

    /**
     * Путь перемещения на клетку или пустой список, если правила такой ход запрещают. Запрос
     * отделён от команды: тактика перебирает варианты, не двигая при этом ни одного отряда.
     */
    public List<int[]> movePath(int sourceRow, int sourceColumn, int targetRow, int targetColumn) {
        Unit.Stack stack = getStack(sourceRow, sourceColumn);
        if (animating || stack == null || stack.hasActed() || isLeft(sourceRow, sourceColumn) != leftTurn
            || outcome() != Outcome.NONE) {
            return List.of();
        }
        if (getStack(targetRow, targetColumn) != null || isObstacle(targetRow, targetColumn)) {
            return List.of();
        }
        List<int[]> path = getPath(sourceRow, sourceColumn, targetRow, targetColumn, stack.getType() == Unit.Type.FLYER);
        if (path.isEmpty() || path.size() - 1 > stack.speed()) {
            return List.of();
        }
        return path;
    }

    /**
     * Путь атаки: подход вплотную к цели и удар последним шагом. Пустой список означает, что
     * цель недостижима — соседние с ней клетки заняты, закрыты препятствием или слишком далеко.
     */
    public List<int[]> attackPath(int sourceRow, int sourceColumn, int targetRow, int targetColumn) {
        if (!ready(sourceRow, sourceColumn, targetRow, targetColumn)) {
            return List.of();
        }
        Unit.Stack stack = getStack(sourceRow, sourceColumn);
        boolean flying = stack.getType() == Unit.Type.FLYER;
        List<int[]> path = getPath(sourceRow, sourceColumn, targetRow, targetColumn, flying);
        if (path.isEmpty()) {
            return List.of();
        }
        int[] melee = path.get(path.size() - 2);
        if (isBlocked(melee[0], melee[1], sourceRow, sourceColumn)) {
            path = detour(sourceRow, sourceColumn, targetRow, targetColumn, flying, stack.speed());
        }
        if (path.isEmpty() || path.size() - 2 > stack.speed()) {
            return List.of();
        }
        return path;
    }

    /** Может ли отряд взяться за цель: ход его, он ещё не ходил, а на клетке цели стоит противник. */
    private boolean ready(int sourceRow, int sourceColumn, int targetRow, int targetColumn) {
        Unit.Stack stack = getStack(sourceRow, sourceColumn);
        return !animating && outcome() == Outcome.NONE && stack != null && !stack.hasActed()
            && getStack(targetRow, targetColumn) != null
            && isLeft(sourceRow, sourceColumn) == leftTurn && isLeft(targetRow, targetColumn) != leftTurn;
    }

    /**
     * Сила выстрела по цели: чем дальше цель, тем слабее удар, а ноль означает, что стрелять
     * нельзя. Стреляют только стрелки, только по противнику в пределах дальности и только пока
     * рядом никто не стоит: в ближнем бою луку размаха нет.
     */
    public int shot(int sourceRow, int sourceColumn, int targetRow, int targetColumn) {
        if (!ready(sourceRow, sourceColumn, targetRow, targetColumn) || surrounded(sourceRow, sourceColumn)) {
            return 0;
        }
        return getStack(sourceRow, sourceColumn)
            .maximumShot(distance(sourceRow, sourceColumn, targetRow, targetColumn));
    }

    /**
     * Выстрел: стрелок бьёт с места, и цель на удар издали не отвечает. Урон ложится не сразу —
     * сначала летит снаряд, и попадание считается там же, где заканчивается ход после перемещения.
     */
    public void shoot(int sourceRow, int sourceColumn, int targetRow, int targetColumn) {
        if (shot(sourceRow, sourceColumn, targetRow, targetColumn) <= 0) {
            return;
        }
        animating = true;
        pendingShot = new int[]{targetRow, targetColumn};
        support.firePropertyChange("shot", null,
            List.of(new int[]{sourceRow, sourceColumn}, new int[]{targetRow, targetColumn}));
    }

    /** Дальность выстрела: стрела летит по прямой, и шаг наискось стоит столько же, сколько прямой. */
    private int distance(int sourceRow, int sourceColumn, int targetRow, int targetColumn) {
        return Math.max(Math.abs(targetRow - sourceRow), Math.abs(targetColumn - sourceColumn));
    }

    /** Стоит ли рядом с отрядом противник. */
    private boolean surrounded(int row, int column) {
        int[] rows = {0, 0, 1, -1};
        int[] columns = {1, -1, 0, 0};
        for (int i = 0; i < DIRECTIONS_COUNT; i++) {
            int nextRow = row + rows[i];
            int nextColumn = column + columns[i];
            if (isInside(nextRow, nextColumn) && getStack(nextRow, nextColumn) != null
                && isLeft(nextRow, nextColumn) != isLeft(row, column)) {
                return true;
            }
        }
        return false;
    }

    private boolean isBlocked(int row, int column, int sourceRow, int sourceColumn) {
        return isObstacle(row, column)
            || getStack(row, column) != null && (row != sourceRow || column != sourceColumn);
    }

    /**
     * Обход, когда клетка на прямом пути к цели занята: ищет свободную клетку по другую сторону
     * от цели и достраивает к найденному пути завершающий удар.
     */
    private List<int[]> detour(int sourceRow, int sourceColumn, int targetRow, int targetColumn,
                              boolean flying, int speed) {
        int[] rows = {0, 0, 1, -1};
        int[] columns = {1, -1, 0, 0};
        for (int i = 0; i < DIRECTIONS_COUNT; i++) {
            int nextRow = targetRow + rows[i];
            int nextColumn = targetColumn + columns[i];
            if (!isInside(nextRow, nextColumn) || isBlocked(nextRow, nextColumn, sourceRow, sourceColumn)) {
                continue;
            }
            List<int[]> around = getPath(sourceRow, sourceColumn, nextRow, nextColumn, flying);
            if (!around.isEmpty() && around.size() - 1 <= speed) {
                List<int[]> path = new ArrayList<>(around);
                path.add(new int[]{targetRow, targetColumn});
                return path;
            }
        }
        return List.of();
    }

    /** Лежит ли клетка внутри поля боя. */
    public boolean isInside(int row, int column) {
        return row >= 0 && row < ROWS && column >= 0 && column < COLS;
    }

    public void move(int sourceRow, int sourceColumn, int targetRow, int targetColumn) {
        List<int[]> path = movePath(sourceRow, sourceColumn, targetRow, targetColumn);
        if (path.isEmpty()) {
            return;
        }
        String message = String.format("%s(%s) ходит (%d, %d)",
            getStack(sourceRow, sourceColumn).getType().getName(), leftTurn ? "L" : "R", targetRow, targetColumn);
        map[targetRow][targetColumn] = map[sourceRow][sourceColumn];
        map[sourceRow][sourceColumn] = null;
        support.firePropertyChange("log", null, message);
        animating = true;
        support.firePropertyChange("move", null, path);
    }

    public void attack(int sourceRow, int sourceColumn, int targetRow, int targetColumn) {
        List<int[]> path = attackPath(sourceRow, sourceColumn, targetRow, targetColumn);
        if (path.isEmpty()) {
            return;
        }
        int[] melee = path.get(path.size() - 2);
        if (melee[0] == sourceRow && melee[1] == sourceColumn) {
            performAttack(sourceRow, sourceColumn, targetRow, targetColumn);
            finishTurn();
            return;
        }
        map[melee[0]][melee[1]] = map[sourceRow][sourceColumn];
        map[sourceRow][sourceColumn] = null;
        animating = true;
        pendingAttack = new int[]{targetRow, targetColumn};
        support.firePropertyChange("move", null, new ArrayList<>(path.subList(0, path.size() - 1)));
    }

    public void endAction() {
        if (!animating) {
            return;
        }
        if (pendingAttack != null) {
            int targetRow = pendingAttack[0];
            int targetColumn = pendingAttack[1];
            pendingAttack = null;
            Long id = turnQueue.peekFirst();
            int[] pos = getStackCoord(id);
            if (pos.length == 2) {
                performAttack(pos[0], pos[1], targetRow, targetColumn);
            }
        }
        if (pendingShot != null) {
            int targetRow = pendingShot[0];
            int targetColumn = pendingShot[1];
            pendingShot = null;
            int[] pos = getStackCoord(turnQueue.peekFirst());
            if (pos.length == 2) {
                performShot(pos[0], pos[1], targetRow, targetColumn);
            }
        }
        finishTurn();
    }

    private void finishTurn() {
        Long id = turnQueue.pollFirst();
        Unit.Stack stack = getStackById(id);
        if (stack != null) {
            stack.setActed(true);
        }
        animating = false;
        checkTurnEnd();
        support.firePropertyChange("map", null, null);
    }

    private void performShot(int sourceRow, int sourceColumn, int targetRow, int targetColumn) {
        Unit.Stack stack = getStack(sourceRow, sourceColumn);
        Unit.Stack target = getStack(targetRow, targetColumn);
        if (stack == null || target == null) {
            return;
        }
        int distance = distance(sourceRow, sourceColumn, targetRow, targetColumn);
        int startSize = target.size();
        target.damage(stack.shot(distance));
        support.firePropertyChange("log", null, String.format("%s(%s) стреляет в %s с %d клеток (-%d)",
            stack.getType().getName(), leftTurn ? "L" : "R", target.getType().getName(),
            distance, startSize - target.size()));
        if (target.isEmpty()) {
            removeStack(targetRow, targetColumn);
        }
    }

    private void performAttack(int sourceRow, int sourceColumn, int targetRow, int targetColumn) {
        Unit.Stack stack = getStack(sourceRow, sourceColumn);
        Unit.Stack target = getStack(targetRow, targetColumn);
        if (stack == null || target == null) {
            return;
        }
        int startSize = target.size();
        target.damage(stack.melee());
        int killed = startSize - target.size();
        String message = String.format("%s(%s) бьет %s (-%d)", stack.getType().getName(), leftTurn ? "R" : "L",
            target.getType().getName(), killed);
        support.firePropertyChange("log", null, message);
        if (target.isEmpty()) {
            removeStack(targetRow, targetColumn);
        } else if (!target.hasCounterAttacked()) {
            int stackSize = stack.size();
            stack.damage(target.counterAttack());
            int stackKilled = stackSize - stack.size();
            String counterMessage = String.format("%s(%s) отвечает",
                target.getType().getName(), leftTurn ? "R" : "L");
            if (stackKilled > 0) {
                counterMessage += "(-" + stackKilled + ")";
            }
            support.firePropertyChange("log", null, counterMessage);
            target.setCounterAttacked(true);
            if (stack.isEmpty()) {
                removeStack(sourceRow, sourceColumn);
            }
        }
    }

    private void removeStack(int row, int column) {
        Long id = map[row][column];
        left.remove(id);
        right.remove(id);
        map[row][column] = null;
        turnQueue.remove(id);
    }

    private void checkTurnEnd() {
        if (outcome() != Outcome.NONE) {
            turnQueue.clear();
            support.firePropertyChange("log", null,
                String.format("--- Бой окончен, победа: %s ---", outcome().getName()));
            return;
        }
        while (turnQueue.isEmpty()) {
            leftTurn = !leftTurn;
            String message = String.format("--- Ход %s ---", leftTurn ? "ЛЕВЫХ" : "ПРАВЫХ");
            support.firePropertyChange("log", null, message);
            left.values().forEach(key -> {
                key.stack.setActed(false);
                key.stack.setCounterAttacked(false);
            });
            right.values().forEach(key -> {
                key.stack.setActed(false);
                key.stack.setCounterAttacked(false);
            });
            fillTurnQueue();
            if (left.isEmpty() && right.isEmpty()) {
                break;
            }
        }
    }

    public List<int[]> getPath(int sourceRow, int sourceColumn, int targetRow, int targetColumn, boolean flying) {
        int[][] dists = getDistances(sourceRow, sourceColumn, flying, targetRow, targetColumn);
        if (dists[targetRow][targetColumn] == Integer.MAX_VALUE) {
            return new ArrayList<>();
        }
        List<int[]> path = new ArrayList<>();
        int currentRow = targetRow;
        int currentColumn = targetColumn;
        path.add(new int[]{currentRow, currentColumn});
        int[] drs = {0, 0, 1, -1};
        int[] dcs = {1, -1, 0, 0};
        while (currentRow != sourceRow || currentColumn != sourceColumn) {
            boolean stepped = false;
            for (int i = 0; i < 4; i++) {
                int nextRow = currentRow + drs[i];
                int nextColumn = currentColumn + dcs[i];
                if (nextRow >= 0 && nextRow < ROWS && nextColumn >= 0 && nextColumn < COLS &&
                    dists[nextRow][nextColumn] == dists[currentRow][currentColumn] - 1) {
                    currentRow = nextRow;
                    currentColumn = nextColumn;
                    path.add(0, new int[]{currentRow, currentColumn});
                    stepped = true;
                    break;
                }
            }
            if (!stepped) {
                return List.of();
            }
        }
        return path;
    }

    public int[][] getDistances(int row, int col, boolean flying) {
        return getDistances(row, col, flying, -1, -1);
    }

    public int[][] getDistances(int row, int col, boolean flying, int targetRow, int targetColumn) {
        int[][] dist = new int[ROWS][COLS];
        for (int[] r : dist) {
            Arrays.fill(r, Integer.MAX_VALUE);
        }
        dist[row][col] = 0;
        Queue<int[]> queue = new PriorityQueue<>(Comparator.comparingInt(cell -> cell[2]));
        queue.add(new int[]{row, col, 0});
        int[] drs = {0, 0, 1, -1};
        int[] dcs = {1, -1, 0, 0};
        while (!queue.isEmpty()) {
            int[] curr = queue.poll();
            int r = curr[0];
            int c = curr[1];
            int d = curr[2];
            if (d > dist[r][c]) {
                continue;
            }
            for (int i = 0; i < 4; i++) {
                int nextRow = r + drs[i];
                int nextColumn = c + dcs[i];
                if (nextRow >= 0 && nextRow < ROWS && nextColumn >= 0 && nextColumn < COLS) {
                    if (map[nextRow][nextColumn] != null && (nextRow != targetRow || nextColumn != targetColumn)) {
                        continue;
                    }
                    if (!flying && obstacles[nextRow][nextColumn]) {
                        continue;
                    }
                    int nd = d + 1;
                    if (nd < dist[nextRow][nextColumn]) {
                        dist[nextRow][nextColumn] = nd;
                        queue.add(new int[]{nextRow, nextColumn, nd});
                    }
                }
            }
        }
        return dist;
    }

    private void generateObstacles(Random random) {
        int count = OBSTACLES_COUNT_BASE + random.nextInt(OBSTACLES_COUNT_RANDOM);
        int placed = 0;
        while (placed < count) {
            int r = random.nextInt(ROWS);
            int c = OBSTACLES_COL_MIN + random.nextInt(OBSTACLES_COL_RANDOM);
            if (map[r][c] == null && !obstacles[r][c]) {
                obstacles[r][c] = true;
                placed++;
            }
        }
    }

    /** Кто выиграл бой: сторона, у которой на поле остались отряды, или никто, пока бой идёт. */
    @Getter
    public enum Outcome {
        NONE("никто"),
        LEFT("ЛЕВЫЕ (Зеленые)"),
        RIGHT("ПРАВЫЕ (Красные)");

        private final String name;

        Outcome(String name) {
            this.name = name;
        }

    }

    private record StackKey(long id, Unit.Stack stack) {
    }
}
