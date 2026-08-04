package ru.mifi.practice.voln.heroes;

import lombok.Getter;
import lombok.Setter;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ThreadLocalRandom;

/** Боевой юнит с атакой, защитой, здоровьем и скоростью. */
public final class Unit {
    private static final int ATTACK_DIVIDER = 100;
    private final int attack;
    private final int defense;
    private final int speed;
    private int health;

    public Unit(int attack, int defense, int health, int speed) {
        this.attack = attack;
        this.defense = defense;
        this.health = health;
        this.speed = speed;
    }

    public int health() {
        return health;
    }

    public int defense() {
        return defense;
    }

    public int speed() {
        return speed;
    }

    public int attack() {
        return attack * health / ATTACK_DIVIDER;
    }

    public void takeDamage(int damage) {
        this.health -= damage;
    }

    /** Род войск и дальность его выстрела: нулевая дальность означает, что бьют только вплотную. */
    @Getter
    public enum Type {
        SHOOTER("Стрелок", 6),
        FLYER("Летающий", 0),
        WALKER("Пехота", 0);

        private final String name;
        private final int range;

        Type(String name, int range) {
            this.name = name;
            this.range = range;
        }

    }

    public static final class Stack {
        private static final int ATTACK_SPREAD = 20;
        private static final int MELEE_SHARE = 70;
        private static final int WHOLE_SHARE = 100;
        @Getter
        private final Type type;
        private final Queue<Unit> units = new PriorityQueue<>(Comparator.comparing(Unit::health));
        @Setter
        private boolean acted;
        @Setter
        private boolean counterAttacked;

        public Stack(Type type) {
            this.type = type;
        }

        public boolean hasActed() {
            return acted;
        }

        public boolean hasCounterAttacked() {
            return counterAttacked;
        }

        public void add(Unit unit) {
            units.add(unit);
        }

        public int size() {
            return units.size();
        }

        public int maximumAttack() {
            return units.stream().mapToInt(Unit::attack).sum();
        }

        public int counterAttack() {
            int maximum = units.stream().mapToInt(Unit::attack).sum();
            return hand(between(maximum / 2, maximum));
        }

        /**
         * Удар вплотную: стрелок отбивается руками, и выходит у него семь десятых от выстрела
         * в упор, а пехота и летуны бьют в полную силу.
         */
        public int melee() {
            return hand(attack());
        }

        /** Наибольший удар вплотную: тактике нужно сравнить исходы до броска. */
        public int maximumMelee() {
            return hand(maximumAttack());
        }

        private int hand(int force) {
            if (type.getRange() <= 0) {
                return force;
            }
            return fade(force, 1) * MELEE_SHARE / WHOLE_SHARE;
        }

        public int attack() {
            int maximum = units.stream().mapToInt(Unit::attack).sum();
            return between(maximum - ATTACK_SPREAD, maximum);
        }

        /**
         * Урон стека раненых юнитов вырождается в ноль, а разброс уводит нижнюю границу в минус.
         * Отрицательный урон молча не наносился, а пустой диапазон ронял бросок исключением.
         */
        private static int between(int minimum, int maximum) {
            int high = Math.max(maximum, 0);
            int low = Math.min(Math.max(minimum, 0), high);
            if (low == high) {
                return high;
            }
            return ThreadLocalRandom.current().nextInt(low, high);
        }

        /**
         * Сила выстрела на расстоянии: вплотную стрелок бьёт в полную силу, на границе дальности —
         * вполовину, а дальше границы стрела не долетает вовсе.
         */
        public int shot(int distance) {
            return fade(attack(), distance);
        }

        /**
         * Наибольшая сила выстрела: тактике нужно сравнить исходы до броска, а сам бросок отдаёт
         * случайное значение из разброса.
         */
        public int maximumShot(int distance) {
            return fade(maximumAttack(), distance);
        }

        private int fade(int force, int distance) {
            int range = type.getRange();
            if (range <= 0 || distance > range) {
                return 0;
            }
            return force * (2 * range - distance) / (2 * range);
        }

        public int totalHealth() {
            return units.stream().mapToInt(Unit::health).sum();
        }

        public int speed() {
            return units.stream().mapToInt(Unit::speed).min().orElse(1);
        }

        public boolean isEmpty() {
            return units.isEmpty();
        }

        /**
         * Сколько бойцов погибнет от удара такой силы. Запрос ничего не меняет: тактике нужно
         * сравнить исходы до того, как удар нанесён, а damage считает потери разрушительно.
         */
        public int casualties(int attackAmount) {
            int remaining = attackAmount;
            int killed = 0;
            for (Unit unit : units.stream().sorted(Comparator.comparing(Unit::health)).toList()) {
                int kick = Math.max(0, remaining - unit.defense());
                if (kick < unit.health()) {
                    break;
                }
                remaining = kick - unit.health();
                ++killed;
            }
            return killed;
        }

        public void damage(int attackAmount) {
            int remaining = attackAmount;
            while (!units.isEmpty() && remaining > 0) {
                Unit unit = units.poll();
                int kick = Math.max(0, remaining - unit.defense());
                if (kick <= 0) {
                    units.add(unit);
                    break;
                }
                if (unit.health() <= kick) {
                    remaining = kick - unit.health();
                } else {
                    unit.takeDamage(kick);
                    units.add(unit);
                    remaining = 0;
                }
            }
        }
    }
}
