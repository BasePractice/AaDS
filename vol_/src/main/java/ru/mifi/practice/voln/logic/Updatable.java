package ru.mifi.practice.voln.logic;

public interface Updatable {
    void update(Context context);

    interface Context {

        void died(Person person);

        View view(Person.Player player);

        View view(Person.Mob mob);

        void hit(Person person, Item item);
    }

    record View(Type type, Object element) {
        @Override
        public String toString() {
            return "[" + type.toString() + "]" + (element == null ? "" : element.toString());
        }
    }

    enum Type {
        ENEMY, EMPTY, ITEM, PLAYER
    }
}
