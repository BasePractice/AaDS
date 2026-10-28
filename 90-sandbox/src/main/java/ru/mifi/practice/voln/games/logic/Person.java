package ru.mifi.practice.voln.games.logic;

import java.util.List;

/** Персонаж игры с очками здоровья, наносящий и получающий урон. */
public interface Person extends Updatable {
    void hit(Item item, Context context);

    void healthUp(int health);

    int health();

    final class Mob implements Person {
        private final Body body;
        private final int index;
        private final Item damage;
        private boolean rage;

        public Mob(String name, int hitPoints, int index, Item damage, boolean aggressive) {
            this.body = new Body(name, hitPoints);
            this.index = index;
            this.damage = damage;
            this.rage = aggressive;
        }

        public int getIndex() {
            return index;
        }

        @Override
        public int health() {
            return body.health();
        }

        @Override
        public void healthUp(int health) {
            body.heal(health);
        }

        @Override
        public void update(Context context) {
            body.regenerate();
            View view = context.view(this);
            if (view != null && view.type() == Type.PLAYER && rage) {
                Player player = (Player) view.element();
                player.hit(damage, context);
            }
        }

        @Override
        public void hit(Item item, Context context) {
            body.hit(this, item, context);
            rage = true;
        }

        @Override
        public String toString() {
            return body.toString();
        }
    }

    final class Player implements Person {
        private static final int IDLE_LIMIT = 1000;
        private static final int KILLS_PER_ATTACK = 10;
        private final Body body;
        private final Inventory inventory = new Inventory();
        private int idleTicks;
        private int kills;

        public Player(String name) {
            this.body = new Body(name, 100);
        }

        public Item getSelectedItem() {
            return inventory.selected();
        }

        public int getIdleTicks() {
            return idleTicks;
        }

        public void addKill() {
            kills++;
        }

        public int getKills() {
            return kills;
        }

        public int getBaseAttack() {
            return kills / KILLS_PER_ATTACK;
        }

        public void idleTick() {
            idleTicks++;
            if (idleTicks >= IDLE_LIMIT) {
                healthUp(1);
                idleTicks = 0;
            }
        }

        public void resetIdle() {
            idleTicks = 0;
        }

        public void selectItem(int item, Context context) {
            inventory.select(item);
        }

        public void useItem(int item, Context context) {
            inventory.use(item, this, context);
        }

        public void removeItem(int index) {
            inventory.remove(index);
        }

        public List<Item> items() {
            return inventory.items();
        }

        public void addInventory(Item item, Context context) {
            inventory.add(item, context);
        }

        public void reset() {
            healthUp(100 - health());
            inventory.clear();
            idleTicks = 0;
            kills = 0;
        }

        @Override
        public int health() {
            return body.health();
        }

        @Override
        public void healthUp(int health) {
            body.heal(health);
        }

        @Override
        public void update(Context context) {
            body.regenerate();
        }

        @Override
        public void hit(Item item, Context context) {
            body.hit(this, item, context);
        }

        @Override
        public String toString() {
            return body.toString();
        }
    }
}
