package ru.mifi.practice.voln.games.logic;

import ru.mifi.practice.voln.games.logic.Updatable.Context;

/** Тело персонажа: имя, запас здоровья и медленное восстановление по тактам. */
final class Body {
    private static final int MAX_HEALTH = 100;
    private static final int REGENERATION_PERIOD = 150;
    private final String name;
    private int hitPoints;
    private int updateCount;

    Body(String name, int hitPoints) {
        this.name = name;
        this.hitPoints = hitPoints;
    }

    int health() {
        return hitPoints;
    }

    void regenerate() {
        ++updateCount;
        if (updateCount % REGENERATION_PERIOD == 0 && hitPoints < MAX_HEALTH) {
            ++hitPoints;
        }
    }

    void heal(int health) {
        hitPoints += health;
        if (hitPoints > MAX_HEALTH) {
            hitPoints = MAX_HEALTH;
        }
    }

    void hit(Person target, Item item, Context context) {
        context.hit(target, item);
        hitPoints -= item.damage();
        if (hitPoints <= 0) {
            context.died(target);
        }
    }

    @Override
    public String toString() {
        return name + ": " + hitPoints;
    }
}
