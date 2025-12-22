package ru.mifi.practice.voln.heroes;

import lombok.EqualsAndHashCode;
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

    private void fillTurnQueue() {
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
        if (turnQueue.isEmpty()) {
            return;
        }
        Long id = turnQueue.pollFirst();
        turnQueue.addLast(id);
        support.firePropertyChange("map", null, null);
    }

    public void skipTurn() {
        if (turnQueue.isEmpty()) {
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

    public void move(int r, int c, int tr, int tc) {
        Unit.Stack stack = getStack(r, c);
        if (stack == null || stack.hasActed() || isLeft(r, c) != leftTurn) {
            return;
        }
        List<int[]> path = getPath(r, c, tr, tc, stack.getType() == Unit.Type.FLYER);
        if (!path.isEmpty() && path.size() - 1 <= stack.speed()) {
            turnQueue.remove(map[r][c]);
            map[tr][tc] = map[r][c];
            map[r][c] = null;
            stack.setActed(true);
            String msg = String.format("%s(%s) ходит (%d, %d)",
                stack.getType().getName(), leftTurn ? "L" : "R", tr, tc);
            support.firePropertyChange("log", null, msg);
            support.firePropertyChange("move", null, path);
            checkTurnEnd();
            support.firePropertyChange("map", null, null);
        }
    }

    public void attack(int r, int c, int tr, int tc) {
        Unit.Stack stack = getStack(r, c);
        Unit.Stack target = getStack(tr, tc);
        if (stack == null || target == null || stack.hasActed() || isLeft(r, c) != leftTurn || isLeft(tr, tc) == leftTurn) {
            return;
        }

        List<int[]> path = getPath(r, c, tr, tc, stack.getType() == Unit.Type.FLYER);
        if (path.isEmpty()) {
            return;
        }

        int[] moveTarget = path.get(path.size() - 2);
        if (isObstacle(moveTarget[0], moveTarget[1]) || (getStack(moveTarget[0], moveTarget[1]) != null &&
            (moveTarget[0] != r || moveTarget[1] != c))) {
            int[] drs = {0, 0, 1, -1};
            int[] dcs = {1, -1, 0, 0};
            boolean found = false;
            for (int i = 0; i < DIRECTIONS_COUNT; i++) {
                int nr = tr + drs[i];
                int nc = tc + dcs[i];
                if (nr >= 0 && nr < ROWS && nc >= 0 && nc < COLS && !isObstacle(nr, nc) &&
                    (getStack(nr, nc) == null || (nr == r && nc == c))) {
                    List<int[]> newPath = getPath(r, c, nr, nc, stack.getType() == Unit.Type.FLYER);
                    if (!newPath.isEmpty() && newPath.size() - 1 <= stack.speed()) {
                        path = new ArrayList<>(newPath);
                        path.add(new int[]{tr, tc});
                        moveTarget = new int[]{nr, nc};
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

        turnQueue.remove(map[r][c]);
        int mtr = moveTarget[0];
        int mtc = moveTarget[1];
        if (mtr != r || mtc != c) {
            map[mtr][mtc] = map[r][c];
            map[r][c] = null;
            support.firePropertyChange("move", null, new ArrayList<>(path.subList(0, path.size() - 1)));
            r = mtr;
            c = mtc;
        }

        int startSize = target.size();
        target.damage(stack.attack());
        int killed = startSize - target.size();
        String msg = String.format("%s %s атакует %s (-%d)",
            leftTurn ? "Левый" : "Правый", stack.getType().getName(), target.getType().getName(), killed);
        support.firePropertyChange("log", null, msg);
        if (target.isEmpty()) {
            removeStack(tr, tc);
        } else if (!target.hasCounterAttacked()) {
            int sStart = stack.size();
            stack.damage(target.attack());
            int sKilled = sStart - stack.size();
            String cmsg = String.format("%s %s контратакует (-%d)",
                leftTurn ? "Правый" : "Левый", target.getType().getName(), sKilled);
            support.firePropertyChange("log", null, cmsg);
            target.setCounterAttacked(true);
            if (stack.isEmpty()) {
                removeStack(r, c);
            }
        }
        if (!stack.isEmpty()) {
            stack.setActed(true);
        }
        checkTurnEnd();
        support.firePropertyChange("map", null, null);
    }

    private void removeStack(int r, int c) {
        Long id = map[r][c];
        left.remove(id);
        right.remove(id);
        map[r][c] = null;
    }

    private void checkTurnEnd() {
        if (turnQueue.isEmpty()) {
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
            if (turnQueue.isEmpty()) {
                checkTurnEnd(); // Switch back if other side is also empty (should not happen in normal game)
            }
        }
    }

    public List<int[]> getPath(int r, int c, int tr, int tc, boolean flying) {
        int[][] dists = getDistances(r, c, flying, tr, tc);
        if (dists[tr][tc] == Integer.MAX_VALUE) {
            return new ArrayList<>();
        }
        List<int[]> path = new ArrayList<>();
        int currR = tr;
        int currC = tc;
        path.add(new int[]{currR, currC});
        int[] drs = {0, 0, 1, -1};
        int[] dcs = {1, -1, 0, 0};
        while (currR != r || currC != c) {
            for (int i = 0; i < 4; i++) {
                int nr = currR + drs[i];
                int nc = currC + dcs[i];
                if (nr >= 0 && nr < ROWS && nc >= 0 && nc < COLS && dists[nr][nc] == dists[currR][currC] - 1) {
                    currR = nr;
                    currC = nc;
                    path.add(0, new int[]{currR, currC});
                    break;
                }
            }
        }
        return path;
    }

    public int[][] getDistances(int row, int col, boolean flying) {
        return getDistances(row, col, flying, -1, -1);
    }

    public int[][] getDistances(int row, int col, boolean flying, int tr, int tc) {
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
                int nr = r + drs[i];
                int nc = c + dcs[i];
                if (nr >= 0 && nr < ROWS && nc >= 0 && nc < COLS) {
                    if (!flying && (obstacles[nr][nc] || (map[nr][nc] != null && (nr != tr || nc != tc)))) {
                        continue;
                    }
                    int nd = d + 1;
                    if (nd < dist[nr][nc]) {
                        dist[nr][nc] = nd;
                        pq.add(new int[]{nr, nc, nd});
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

    @EqualsAndHashCode(of = "id")
    private static final class StackKey {
        private final long id;
        private final Unit.Stack stack;

        private StackKey(long id, Unit.Stack stack) {
            this.id = id;
            this.stack = stack;
        }
    }
}
