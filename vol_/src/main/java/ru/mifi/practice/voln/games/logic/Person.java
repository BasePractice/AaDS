package ru.mifi.practice.voln.games.logic;

import java.util.ArrayList;
import java.util.List;

public interface Person extends Updatable {
    void hit(Item item, Context context);

    void healthUp(int health);

    int health();

    abstract class AbstractPerson implements Person {
        private final String name;
        private int hitPoints;
        private int updateCount;

        protected AbstractPerson(String name, int hitPoints) {
            this.name = name;
            this.hitPoints = hitPoints;
        }

        @Override
        public int health() {
            return hitPoints;
        }

        @Override
        public void update(Context context) {
            ++updateCount;
            if (updateCount % 150 == 0 && hitPoints < 100) {
                ++hitPoints;
            }
        }

        @Override
        public void hit(Item item, Context context) {
            context.hit(this, item);
            hitPoints -= item.damage();
            if (hitPoints <= 0) {
                context.died(this);
            }
        }

        @Override
        public void healthUp(int health) {
            hitPoints += health;
            if (hitPoints > 100) {
                hitPoints = 100;
            }
        }

        @Override
        public String toString() {
            return name + ": " + hitPoints;
        }
    }

    final class Mob extends AbstractPerson {
        private final int index;
        private final Item.DamageItem damage;
        private boolean aggressive;
        private boolean toArge;

        public int getIndex() {
            return index;
        }

        public Mob(String name, int hitPoints, int index, Item.DamageItem damage, boolean aggressive) {
            super(name, hitPoints);
            this.index = index;
            this.damage = damage;
            this.aggressive = aggressive;
            this.toArge = false;
        }

        @Override
        public void update(Context context) {
            super.update(context);
            View view = context.view(this);
            if (view != null && view.type() == Type.PLAYER && (toArge || aggressive)) {
                toArge = true;
                Player player = (Player) view.element();
                player.hit(damage, context);
            }
        }

        @Override
        public void hit(Item item, Context context) {
            super.hit(item, context);
            toArge = true;
        }
    }

    final class Player extends AbstractPerson {
        private static final Item FIST = new Item.DamageItem(5);
        private final List<Item> inventory = new ArrayList<>();
        private Item selectedItem;
        private int idleTicks;
        private int kills;

        public Item getSelectedItem() {
            return selectedItem;
        }

        public Player(String name) {
            super(name, 100);
            selectedItem = FIST;
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
            return kills / 10;
        }

        public void idleTick() {
            idleTicks++;
            if (idleTicks >= 1000) {
                healthUp(1);
                idleTicks = 0;
            }
        }

        public void resetIdle() {
            idleTicks = 0;
        }

        public void selectItem(int item, Context context) {
            if (item < 0 || item >= inventory.size()) {
                selectedItem = FIST;
                return;
            }
            selectedItem = inventory.get(item);
        }

        public void useItem(int item, Context context) {
            if (item < 0 || item >= inventory.size()) {
                return;
            }
            Item element = inventory.get(item);
            if (element instanceof Item.Once once) {
                once.apply(this, context);
                removeItem(item);
            } else {
                selectedItem = element;
            }
        }

        public void removeItem(int index) {
            if (index < 0 || index >= inventory.size()) {
                return;
            }
            Item item = inventory.remove(index);
            if (item.equals(selectedItem)) {
                selectedItem = FIST;
            }
        }

        public List<Item> items() {
            return inventory;
        }

        public void addInventory(Item item, Context context) {
            inventory.add(item);
            while (inventory.size() > 10) {
                int minIdx = 0;
                int minDamage = inventory.get(0).damage();
                for (int i = 1; i < inventory.size(); i++) {
                    if (inventory.get(i).damage() < minDamage) {
                        minDamage = inventory.get(i).damage();
                        minIdx = i;
                    }
                }
                Item removed = inventory.get(minIdx);
                if (context != null) {
                    context.log("Removed (limit): " + removed);
                }
                removeItem(minIdx);
            }
        }

        public void reset() {
            healthUp(100 - health());
            inventory.clear();
            selectedItem = FIST;
            idleTicks = 0;
            kills = 0;
        }
    }
}
