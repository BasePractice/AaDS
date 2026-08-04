package ru.mifi.practice.voln.heroes;

import java.util.List;

/**
 * Выбор хода за ту сторону, которой сейчас принадлежит очередь.
 *
 * <p>И человек, и программа выражают ход одним и тем же решением, поэтому поле боя не знает,
 * кто именно ходит: клик мышью и расчёт машины приходят в него по одной дороге.
 */
public interface Tactics {

    Decision decide(BattleMap map);

    /** Что делает отряд в свой ход. */
    enum Kind {
        ATTACK,
        SHOOT,
        MOVE,
        WAIT,
        SKIP
    }

    /** Решение тактики: действие и клетка, к которой оно относится. */
    record Decision(Kind kind, int row, int column) {
    }

    /**
     * Жадная тактика: перебор всех достижимых целей с оценкой размена, а при недостижимости —
     * сближение с ближайшим противником.
     *
     * <p>Это минимакс глубины один: программа считает свой удар и немедленный ответ цели, но не
     * заглядывает дальше. Достижимость берётся из обхода поля в ширину, который поле боя уже
     * умеет делать для подсветки хода, так что отдельного поиска пути тактике не нужно.
     *
     * <p>Оценка размена складывается из погибших бойцов цели и нанесённого урона за вычетом
     * собственных потерь от контратаки. Гибель отряда весит на два порядка больше урона, поэтому
     * добить ослабленный отряд для программы важнее, чем поцарапать свежий.
     */
    final class Greedy implements Tactics {
        private static final int CASUALTY_WEIGHT = 100;
        private static final int[] NEIGHBOUR_ROWS = {0, 0, 1, -1};
        private static final int[] NEIGHBOUR_COLUMNS = {1, -1, 0, 0};

        @Override
        public Decision decide(BattleMap map) {
            Long active = map.getTurnQueue().peekFirst();
            Unit.Stack stack = map.getStackById(active);
            int[] coord = map.getStackCoord(active);
            if (stack == null || coord.length != 2) {
                return new Decision(Kind.SKIP, -1, -1);
            }
            Decision strike = strike(map, stack, coord);
            if (strike.kind() != Kind.SKIP) {
                return strike;
            }
            return approach(map, stack, coord);
        }

        /** Самый выгодный из достижимых ударов или пропуск, если ни одна цель не достаётся. */
        private Decision strike(BattleMap map, Unit.Stack stack, int[] coord) {
            Aim best = new Aim(new Decision(Kind.SKIP, -1, -1), Integer.MIN_VALUE);
            for (int row = 0; row < Constants.ROWS; row++) {
                for (int column = 0; column < Constants.COLS; column++) {
                    Aim aim = aim(map, stack, coord, row, column);
                    if (aim.profit() > best.profit()) {
                        best = aim;
                    }
                }
            }
            return best.decision();
        }

        /**
         * Лучшее, что отряд может сделать с одной целью: выстрелить, если стрела долетает, или
         * подойти вплотную. Выстрел не вызывает ответа, поэтому и оценивается без вычета
         * собственных потерь — и оказывается выгоднее равного по урону подхода.
         */
        private Aim aim(BattleMap map, Unit.Stack stack, int[] coord, int row, int column) {
            Aim none = new Aim(new Decision(Kind.SKIP, -1, -1), Integer.MIN_VALUE);
            if (!isEnemy(map, row, column)) {
                return none;
            }
            int shot = map.shot(coord[0], coord[1], row, column);
            if (shot > 0) {
                return new Aim(new Decision(Kind.SHOOT, row, column), gain(map.getStack(row, column), shot));
            }
            if (map.attackPath(coord[0], coord[1], row, column).isEmpty()) {
                return none;
            }
            return new Aim(new Decision(Kind.ATTACK, row, column), profit(stack, map.getStack(row, column)));
        }

        /**
         * Оценка размена: погибшие бойцы цели и нанесённый урон минус собственные потери от
         * ответного удара. Ответ считается по уцелевшей части цели, поэтому добивающий удар
         * оценивается выше равного по урону, но не смертельного.
         */
        private int profit(Unit.Stack attacker, Unit.Stack defender) {
            int damage = attacker.maximumMelee();
            int killed = defender.casualties(damage);
            int gain = gain(defender, damage);
            if (killed >= defender.size() || defender.hasCounterAttacked()) {
                return gain;
            }
            int counter = defender.maximumMelee() * (defender.size() - killed) / defender.size();
            return gain - attacker.casualties(counter) * CASUALTY_WEIGHT - Math.min(counter, attacker.totalHealth());
        }

        /** Выгода удара по цели: гибель бойца весит на два порядка больше нанесённого урона. */
        private int gain(Unit.Stack defender, int damage) {
            return defender.casualties(damage) * CASUALTY_WEIGHT + Math.min(damage, defender.totalHealth());
        }

        /** Шаг в сторону ближайшего противника на всю доступную скорость. */
        private Decision approach(BattleMap map, Unit.Stack stack, int[] coord) {
            boolean flying = stack.getType() == Unit.Type.FLYER;
            int[][] distances = map.getDistances(coord[0], coord[1], flying);
            int[] aim = nearest(map, distances);
            if (aim.length != 2) {
                return new Decision(Kind.SKIP, -1, -1);
            }
            List<int[]> path = map.getPath(coord[0], coord[1], aim[0], aim[1], flying);
            for (int step = Math.min(stack.speed(), path.size() - 1); step > 0; step--) {
                int[] cell = path.get(step);
                if (!map.movePath(coord[0], coord[1], cell[0], cell[1]).isEmpty()) {
                    return new Decision(Kind.MOVE, cell[0], cell[1]);
                }
            }
            return new Decision(Kind.SKIP, -1, -1);
        }

        /** Ближайшая по обходу в ширину клетка, соседняя хоть с одним противником. */
        private int[] nearest(BattleMap map, int[][] distances) {
            int[] found = new int[0];
            int best = Integer.MAX_VALUE;
            for (int row = 0; row < Constants.ROWS; row++) {
                for (int column = 0; column < Constants.COLS; column++) {
                    if (!isEnemy(map, row, column)) {
                        continue;
                    }
                    int[] approach = closest(distances, row, column, best);
                    if (approach.length == 2) {
                        best = distances[approach[0]][approach[1]];
                        found = approach;
                    }
                }
            }
            return found;
        }

        /** Ближе ли какая-нибудь соседняя с целью клетка, чем уже найденная. */
        private int[] closest(int[][] distances, int row, int column, int limit) {
            int[] found = new int[0];
            int best = limit;
            for (int i = 0; i < NEIGHBOUR_ROWS.length; i++) {
                int nextRow = row + NEIGHBOUR_ROWS[i];
                int nextColumn = column + NEIGHBOUR_COLUMNS[i];
                if (nextRow < 0 || nextRow >= Constants.ROWS || nextColumn < 0 || nextColumn >= Constants.COLS) {
                    continue;
                }
                if (distances[nextRow][nextColumn] < best) {
                    best = distances[nextRow][nextColumn];
                    found = new int[]{nextRow, nextColumn};
                }
            }
            return found;
        }

        private boolean isEnemy(BattleMap map, int row, int column) {
            return map.getStack(row, column) != null && map.isLeft(row, column) != map.isLeftTurn();
        }

        /** Прицел: что тактика сделает с целью и во что это ей обойдётся. */
        private record Aim(Decision decision, int profit) {
        }
    }
}
