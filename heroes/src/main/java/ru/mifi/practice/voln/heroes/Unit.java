package ru.mifi.practice.voln.heroes;

import lombok.Getter;
import lombok.Setter;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ThreadLocalRandom;

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

    @Getter
    public enum Type {
        SHOOTER("Стрелок"),
        FLYER("Летающий"),
        WALKER("Пехота");

        private final String name;

        Type(String name) {
            this.name = name;
        }

    }

    public static final class Stack {
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
            int minimum = maximum / 2;
            return ThreadLocalRandom.current().nextInt(minimum, maximum);
        }

        public int attack() {
            int maximum = units.stream().mapToInt(Unit::attack).sum();
            int minimum = maximum - 20;
            return ThreadLocalRandom.current().nextInt(minimum, maximum);
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
