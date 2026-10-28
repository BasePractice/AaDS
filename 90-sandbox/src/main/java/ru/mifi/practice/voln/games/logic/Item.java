package ru.mifi.practice.voln.games.logic;

import ru.mifi.practice.voln.games.logic.Updatable.Context;

/** Предмет с уроном и опциональным одноразовым эффектом. */
public interface Item {

    int damage();

    interface Once extends Item {
        void apply(Person person, Context context);
    }

    final class Health implements Once {
        private final int health;

        public Health(int health) {
            this.health = health;
        }

        @Override
        public int damage() {
            return 1;
        }

        @Override
        public void apply(Person person, Context context) {
            person.healthUp(health);
            context.log("Использование эликсира: " + this);
        }

        @Override
        public String toString() {
            return "H" + health;
        }
    }

    final class DamageItem implements Item {
        private final int damage;

        public DamageItem(int damage) {
            this.damage = damage;
        }

        @Override
        public int damage() {
            return damage;
        }

        @Override
        public String toString() {
            return "D" + damage;
        }
    }

    final class Hummer implements Item {
        private final Item damage = new DamageItem(10);

        @Override
        public int damage() {
            return damage.damage();
        }

        @Override
        public String toString() {
            return damage.toString();
        }
    }

    final class BonusItem implements Item {
        private final Item item;
        private final int bonus;

        public BonusItem(Item item, int bonus) {
            this.item = item;
            this.bonus = bonus;
        }

        @Override
        public int damage() {
            return item.damage() + bonus;
        }

        @Override
        public String toString() {
            return item.toString() + "+" + bonus;
        }
    }
}
