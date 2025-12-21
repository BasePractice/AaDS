package ru.mifi.practice.voln.heroes;

import lombok.EqualsAndHashCode;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
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
    private final AtomicLong idCount = new AtomicLong(0);
    private final NavigableMap<Long, StackKey> left = new TreeMap<>();
    private final NavigableMap<Long, StackKey> right = new TreeMap<>();
    private final Long[][] map = new Long[ROWS][COLS];
    private final boolean[][] obstacles = new boolean[ROWS][COLS];
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
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
        int stackCount = 5 + random.nextInt(3);
        for (int i = 0; i < stackCount; i++) {
            int row;
            do {
                row = random.nextInt(ROWS);
            } while (map[row][0] != null);
            addLeft(row, 0, createRandomStack(random));

            do {
                row = random.nextInt(ROWS);
            } while (map[row][14] != null);
            addRight(row, 14, createRandomStack(random));
        }
    }

    private Unit.Stack createRandomStack(Random random) {
        Unit.Type[] types = Unit.Type.values();
        Unit.Type type = types[random.nextInt(types.length)];
        Unit.Stack stack = new Unit.Stack(type);
        int count = 1 + random.nextInt(10);
        int speed = 3 + random.nextInt(5);
        for (int i = 0; i < count; i++) {
            stack.add(new Unit(10 + random.nextInt(20), 5 + random.nextInt(10), 50 + random.nextInt(50), speed));
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

    public boolean isLeftTurn() {
        return leftTurn;
    }

    public void move(int r, int c, int tr, int tc) {
        Unit.Stack stack = getStack(r, c);
        if (stack == null || stack.hasActed() || isLeft(r, c) != leftTurn) {
            return;
        }
        List<int[]> path = getPath(r, c, tr, tc, stack.getType() == Unit.Type.FLYER);
        if (!path.isEmpty() && path.size() - 1 <= stack.speed()) {
            map[tr][tc] = map[r][c];
            map[r][c] = null;
            stack.setActed(true);
            String msg = String.format("%s %s ходит на (%d, %d)",
                    leftTurn ? "Левый" : "Правый", stack.getType().getName(), tr, tc);
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
        int startSize = target.size();
        target.damage(stack.attack());
        int killed = startSize - target.size();
        String msg = String.format("%s %s атакует %s (-%d)",
                leftTurn ? "Левый" : "Правый", stack.getType().getName(), target.getType().getName(), killed);
        support.firePropertyChange("log", null, msg);
        if (target.size() == 0) {
            removeStack(tr, tc);
        } else if (!target.hasCounterAttacked()) {
            int sStart = stack.size();
            stack.damage(target.attack());
            int sKilled = sStart - stack.size();
            String cmsg = String.format("%s %s контратакует (-%d)",
                    leftTurn ? "Правый" : "Левый", target.getType().getName(), sKilled);
            support.firePropertyChange("log", null, cmsg);
            target.setCounterAttacked(true);
            if (stack.size() == 0) {
                removeStack(r, c);
            }
        }
        if (stack.size() > 0) {
            stack.setActed(true);
        }
        checkTurnEnd();
        support.firePropertyChange("map", null, null);
    }

    public void skip(int r, int c) {
        Unit.Stack stack = getStack(r, c);
        if (stack != null && isLeft(r, c) == leftTurn && !stack.hasActed()) {
            stack.setActed(true);
            String msg = String.format("%s %s пропустил ход",
                    leftTurn ? "Левый" : "Правый", stack.getType().getName());
            support.firePropertyChange("log", null, msg);
            checkTurnEnd();
            support.firePropertyChange("map", null, null);
        }
    }

    private void removeStack(int r, int c) {
        Long id = map[r][c];
        left.remove(id);
        right.remove(id);
        map[r][c] = null;
    }

    private void checkTurnEnd() {
        NavigableMap<Long, StackKey> currentSide = leftTurn ? left : right;
        boolean allActed = currentSide.values().stream().allMatch(sk -> sk.stack.hasActed());
        if (allActed) {
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
            boolean found = false;
            for (int i = 0; i < 4; i++) {
                int nr = currR + drs[i];
                int nc = currC + dcs[i];
                if (nr >= 0 && nr < ROWS && nc >= 0 && nc < COLS && dists[nr][nc] == dists[currR][currC] - 1) {
                    currR = nr;
                    currC = nc;
                    path.add(0, new int[]{currR, currC});
                    found = true;
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
        if (flying) {
            for (int r = 0; r < ROWS; r++) {
                for (int c = 0; c < COLS; c++) {
                    if (map[r][c] != null && (r != row || c != col) && (r != tr || c != tc)) {
                        dist[r][c] = Integer.MAX_VALUE;
                    }
                }
            }
        }
        return dist;
    }

    private void generateObstacles(Random random) {
        int count = 5 + random.nextInt(5);
        int placed = 0;
        while (placed < count) {
            int r = random.nextInt(ROWS);
            int c = 5 + random.nextInt(5);
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
