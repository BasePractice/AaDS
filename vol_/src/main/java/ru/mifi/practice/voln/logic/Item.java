package ru.mifi.practice.voln.logic;

import ru.mifi.practice.voln.logic.Updatable.Context;

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
        }

        @Override
        public String toString() {
            return "H" + health;
        }
    }

    class DamageItem implements Item {
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

    final class Hummer extends DamageItem {

        public Hummer() {
            super(10);
        }
    }
}
