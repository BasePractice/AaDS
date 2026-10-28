package ru.mifi.practice.voln.heroes;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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

    /** Снимок бойца: те же числа, но менять их уже некому. */
    Fighter snapshot() {
        return new Fighter(attack, defense, health, speed);
    }

    /**
     * Боец в снимке отряда. Перебор вариантов считает по снимкам, поэтому удар в уме никого не
     * ранит: раненый боец — это новый боец, а не тот же самый с другим здоровьем.
     */
    public record Fighter(int power, int defense, int health, int speed) {

        /** Урон бойца: раненый бьёт слабее ровно в долю оставшегося здоровья. */
        public int attack() {
            return power * health / ATTACK_DIVIDER;
        }

        /** Тот же боец после раны. */
        Fighter wounded(int damage) {
            return new Fighter(power, defense, health - damage, speed);
        }
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

    /**
     * Неизменяемый снимок отряда: считает те же удары и потери, что и живой отряд, но ничего в
     * нём не меняет.
     *
     * <p>По снимкам ходит перебор вариантов: удар возвращает новый отряд, а не калечит прежний,
     * поэтому откатывать ничего не нужно. Разброс броска в снимке заменён верхней границей —
     * иначе одна и та же позиция оценивалась бы каждый раз по-новому.
     */
    public record Squad(Type type, List<Fighter> fighters, int shots) {
        private static final int MELEE_SHARE = 70;
        private static final int WHOLE_SHARE = 100;

        public Squad(Type type, List<Fighter> fighters, int shots) {
            this.type = type;
            this.fighters = List.copyOf(fighters);
            this.shots = shots;
        }

        public int size() {
            return fighters.size();
        }

        public boolean isEmpty() {
            return fighters.isEmpty();
        }

        public int totalHealth() {
            return fighters.stream().mapToInt(Fighter::health).sum();
        }

        public int speed() {
            return fighters.stream().mapToInt(Fighter::speed).min().orElse(1);
        }

        public int maximumAttack() {
            return fighters.stream().mapToInt(Fighter::attack).sum();
        }

        /** Наибольший удар вплотную: тактике нужно сравнить исходы до броска. */
        public int maximumMelee() {
            return hand(maximumAttack());
        }

        /** Наибольшая сила выстрела на расстоянии. */
        public int maximumShot(int distance) {
            return volley(maximumAttack(), distance);
        }

        /**
         * Удар вплотную: стрелок отбивается руками, и выходит у него семь десятых от самого
         * сильного своего выстрела, — а пехота и летуны бьют в полную силу. Запас стрел на удар
         * не влияет: без стрел стрелок остаётся той же пехотой, что и с ними.
         */
        int hand(int force) {
            if (type != Type.SHOOTER) {
                return force;
            }
            return bolt(force, 1) * MELEE_SHARE / WHOLE_SHARE;
        }

        /**
         * Выстрел такой силы на такое расстояние, а ноль означает, что стрелять нельзя: не тот
         * род войск, кончились стрелы или цель дальше самого длинного выстрела.
         */
        int volley(int force, int distance) {
            if (type != Type.SHOOTER || shots <= 0 || distance > Constants.SPAN) {
                return 0;
            }
            return bolt(force, distance);
        }

        /** Урон падает с дальностью: вплотную стрела бьёт в полную силу, с края в край вполовину. */
        private int bolt(int force, int distance) {
            return force * (2 * Constants.SPAN - distance) / (2 * Constants.SPAN);
        }

        /**
         * Сколько бойцов погибнет от удара такой силы. Удар разбирают самые слабые: он добивает
         * их одного за другим, пока хватает силы.
         */
        public int casualties(int amount) {
            int remaining = amount;
            int killed = 0;
            for (Fighter fighter : weakest()) {
                int kick = Math.max(0, remaining - fighter.defense());
                if (kick < fighter.health()) {
                    break;
                }
                remaining = kick - fighter.health();
                ++killed;
            }
            return killed;
        }

        /**
         * Отряд после удара такой силы: погибшие пропадают, а последний задетый остаётся в строю
         * раненым и гасит собой остаток удара.
         */
        public Squad damaged(int amount) {
            List<Fighter> survivors = new ArrayList<>();
            List<Fighter> order = weakest();
            int remaining = amount;
            for (int i = 0; i < order.size(); i++) {
                Fighter fighter = order.get(i);
                int kick = Math.max(0, remaining - fighter.defense());
                if (remaining <= 0 || kick <= 0) {
                    survivors.addAll(order.subList(i, order.size()));
                    break;
                }
                if (fighter.health() <= kick) {
                    remaining = kick - fighter.health();
                } else {
                    survivors.add(fighter.wounded(kick));
                    remaining = 0;
                }
            }
            return new Squad(type, survivors, shots);
        }

        /** Тот же отряд одной стрелой беднее. */
        public Squad spent() {
            return new Squad(type, fighters, shots - 1);
        }

        /** Бойцы от самого слабого к самому крепкому: удар достаётся им в этом порядке. */
        private List<Fighter> weakest() {
            return fighters.stream().sorted(Comparator.comparingInt(Fighter::health)).toList();
        }
    }

    public static final class Stack {
        private static final int ATTACK_SPREAD = 20;
        private static final int BOLTS = 15;
        @Getter
        private final Type type;
        private final Queue<Unit> units = new PriorityQueue<>(Comparator.comparing(Unit::health));
        private int shots = BOLTS;
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

        /** Снимок отряда: по нему считает тактика, не трогая живых бойцов. */
        public Squad snapshot() {
            return new Squad(type, units.stream().map(Unit::snapshot).toList(), shots);
        }

        public int counterAttack() {
            int maximum = units.stream().mapToInt(Unit::attack).sum();
            return snapshot().hand(between(maximum / 2, maximum));
        }

        /**
         * Удар вплотную: стрелок отбивается руками, и выходит у него семь десятых от самого
         * сильного своего выстрела, — а пехота и летуны бьют в полную силу. Запас стрел на удар
         * не влияет: без стрел стрелок остаётся той же пехотой, что и с ними.
         */
        public int melee() {
            return snapshot().hand(attack());
        }

        /** Наибольший удар вплотную: тактике нужно сравнить исходы до броска. */
        public int maximumMelee() {
            return snapshot().maximumMelee();
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
         * Сила выстрела на расстоянии: стрелок достаёт куда угодно на поле, но урон падает
         * пропорционально дальности — вплотную в полную силу, с края в край вполовину.
         */
        public int shot(int distance) {
            return snapshot().volley(attack(), distance);
        }

        /**
         * Наибольшая сила выстрела: тактике нужно сравнить исходы до броска, а сам бросок отдаёт
         * случайное значение из разброса.
         */
        public int maximumShot(int distance) {
            return snapshot().maximumShot(distance);
        }

        /** Сколько выстрелов осталось: запас конечен, и с последней стрелой стрелок — пехота. */
        public int shots() {
            return shots;
        }

        /** Потратить стрелу. */
        public void spend() {
            shots--;
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
            return snapshot().casualties(attackAmount);
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
