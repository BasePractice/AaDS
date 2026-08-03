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
    private int[] pendingAttack;

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void addLeft(int row, int col, Unit.Stack stack) {
        if (map[row][col] != null) {
            throw new IllegalArgumentException();
        }
        long id = idCount.getAndIncrement();
        left.put(id, new StackKey(id, stack));
        map[row][col] = id;
        support.firePropertyChange("map", null, null);
    }

    public void addRight(int row, int col, Unit.Stack stack) {
        if (map[row][col] != null) {
            throw new IllegalArgumentException();
        }
        long id = idCount.getAndIncrement();
        right.put(id, new StackKey(id, stack));
        map[row][col] = id;
        support.firePropertyChange("map", null, null);
    }

    public void fillRandomly() {
        Random random = new Random();
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
            String msg = String.format("%s %s пропустил ход",
                leftTurn ? "Левый" : "Правый", stack.getType().getName());
            support.firePropertyChange("log", null, msg);
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

    public void move(int sourceRow, int sourceColumn, int targetRow, int targetColumn) {
        if (animating) {
            return;
        }
        Unit.Stack stack = getStack(sourceRow, sourceColumn);
        if (stack == null || stack.hasActed() || isLeft(sourceRow, sourceColumn) != leftTurn) {
            return;
        }
        if (getStack(targetRow, targetColumn) != null || isObstacle(targetRow, targetColumn)) {
            return;
        }
        List<int[]> path = getPath(sourceRow, sourceColumn, targetRow, targetColumn, stack.getType() == Unit.Type.FLYER);
        if (!path.isEmpty() && path.size() - 1 <= stack.speed()) {
            map[targetRow][targetColumn] = map[sourceRow][sourceColumn];
            map[sourceRow][sourceColumn] = null;
            String msg = String.format("%s(%s) ходит (%d, %d)",
                stack.getType().getName(), leftTurn ? "L" : "R", targetRow, targetColumn);
            support.firePropertyChange("log", null, msg);
            animating = true;
            support.firePropertyChange("move", null, path);
        }
    }

    public void attack(int sourceRow, int sourceColumn, int targetRow, int targetColumn) {
        if (animating) {
            return;
        }
        Unit.Stack stack = getStack(sourceRow, sourceColumn);
        Unit.Stack target = getStack(targetRow, targetColumn);
        if (stack == null || target == null || stack.hasActed() ||
            isLeft(sourceRow, sourceColumn) != leftTurn || isLeft(targetRow, targetColumn) == leftTurn) {
            return;
        }

        List<int[]> path = getPath(sourceRow, sourceColumn, targetRow, targetColumn, stack.getType() == Unit.Type.FLYER);
        if (path.isEmpty()) {
            return;
        }

        int[] moveTarget = path.get(path.size() - 2);
        if (isObstacle(moveTarget[0], moveTarget[1]) || (getStack(moveTarget[0], moveTarget[1]) != null &&
            (moveTarget[0] != sourceRow || moveTarget[1] != sourceColumn))) {
            int[] drs = {0, 0, 1, -1};
            int[] dcs = {1, -1, 0, 0};
            boolean found = false;
            for (int i = 0; i < DIRECTIONS_COUNT; i++) {
                int nextRow = targetRow + drs[i];
                int nextColumn = targetColumn + dcs[i];
                if (nextRow >= 0 && nextRow < ROWS && nextColumn >= 0 && nextColumn < COLS && !isObstacle(nextRow, nextColumn) &&
                    (getStack(nextRow, nextColumn) == null || (nextRow == sourceRow && nextColumn == sourceColumn))) {
                    List<int[]> newPath = getPath(sourceRow, sourceColumn, nextRow, nextColumn, stack.getType() == Unit.Type.FLYER);
                    if (!newPath.isEmpty() && newPath.size() - 1 <= stack.speed()) {
                        path = new ArrayList<>(newPath);
                        path.add(new int[]{targetRow, targetColumn});
                        moveTarget = new int[]{nextRow, nextColumn};
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                return;
            }
        }

        if (path.size() - 2 > stack.speed()) {
            return;
        }

        int mtr = moveTarget[0];
        int mtc = moveTarget[1];
        if (mtr != sourceRow || mtc != sourceColumn) {
            map[mtr][mtc] = map[sourceRow][sourceColumn];
            map[sourceRow][sourceColumn] = null;
            animating = true;
            pendingAttack = new int[]{targetRow, targetColumn};
            support.firePropertyChange("move", null, new ArrayList<>(path.subList(0, path.size() - 1)));
        } else {
            performAttack(sourceRow, sourceColumn, targetRow, targetColumn);
            finishTurn();
        }
    }

    public void endAction() {
        if (!animating) {
            return;
        }
        if (pendingAttack != null) {
            int tr = pendingAttack[0];
            int tc = pendingAttack[1];
            pendingAttack = null;
            Long id = turnQueue.peekFirst();
            int[] pos = getStackCoord(id);
            performAttack(pos[0], pos[1], tr, tc);
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

    private void performAttack(int sourceRow, int sourceColumn, int targetRow, int targetColumn) {
        Unit.Stack stack = getStack(sourceRow, sourceColumn);
        Unit.Stack target = getStack(targetRow, targetColumn);
        if (stack == null || target == null) {
            return;
        }
        int startSize = target.size();
        target.damage(stack.attack());
        int killed = startSize - target.size();
        String msg = String.format("%s(%s) бьет %s (-%d)", stack.getType().getName(), leftTurn ? "R" : "L",
            target.getType().getName(), killed);
        support.firePropertyChange("log", null, msg);
        if (target.isEmpty()) {
            removeStack(targetRow, targetColumn);
        } else if (!target.hasCounterAttacked()) {
            int sStart = stack.size();
            stack.damage(target.counterAttack());
            int sKilled = sStart - stack.size();
            String cmsg = String.format("%s(%s) отвечает",
                target.getType().getName(), leftTurn ? "R" : "L");
            if (sKilled > 0) {
                cmsg += "(-" + sKilled + ")";
            }
            support.firePropertyChange("log", null, cmsg);
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
        while (turnQueue.isEmpty()) {
            leftTurn = !leftTurn;
            String msg = String.format("--- Ход %s ---", leftTurn ? "ЛЕВЫХ" : "ПРАВЫХ");
            support.firePropertyChange("log", null, msg);
            left.values().forEach(sk -> {
                sk.stack.setActed(false);
                sk.stack.setCounterAttacked(false);
            });
            right.values().forEach(sk -> {
                sk.stack.setActed(false);
                sk.stack.setCounterAttacked(false);
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
            for (int i = 0; i < 4; i++) {
                int nextRow = currentRow + drs[i];
                int nextColumn = currentColumn + dcs[i];
                if (nextRow >= 0 && nextRow < ROWS && nextColumn >= 0 && nextColumn < COLS &&
                    dists[nextRow][nextColumn] == dists[currentRow][currentColumn] - 1) {
                    currentRow = nextRow;
                    currentColumn = nextColumn;
                    path.add(0, new int[]{currentRow, currentColumn});
                    break;
                }
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
        Queue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[2]));
        pq.add(new int[]{row, col, 0});
        int[] drs = {0, 0, 1, -1};
        int[] dcs = {1, -1, 0, 0};
        while (!pq.isEmpty()) {
            int[] curr = pq.poll();
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
                        pq.add(new int[]{nextRow, nextColumn, nd});
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

    private record StackKey(long id, Unit.Stack stack) {
    }
}
