package ru.mifi.practice.voln.games.logic;

import lombok.Getter;

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
            if (updateCount % 1000 == 0 && hitPoints < 50) {
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
        }

        @Override
        public String toString() {
            return name + ": " + hitPoints;
        }
    }

    final class Mob extends AbstractPerson {
        @Getter
        private final int index;
        private final Item.DamageItem damage;
        //NOTICE: Если агрессивный, то бьет первым
        private boolean aggressive;
        private boolean toArge;

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
            if (toArge) {
                if (view != null && view.type() == Type.PLAYER) {
                    Player player = (Player) view.element();
                    player.hit(damage, context);
                }
            } else if (aggressive) {
                if (view != null && view.type() == Type.PLAYER) {
                    toArge = true;
                }
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
        @Getter
        private Item selectedItem;

        public Player(String name) {
            super(name, 100);
            selectedItem = FIST;
        }

        public void selectItem(int item, Context context) {
            if (item < 0 || item >= inventory.size()) {
                selectedItem = FIST;
                return;
            }
            Item element = inventory.get(item);
            if (element instanceof Item.Once once) {
                once.apply(this, context);
                inventory.remove(item);
            } else {
                selectedItem = element;
            }
        }

        public List<Item> items() {
            return inventory;
        }

        public void addInventory(Item item) {
            inventory.add(item);
        }
    }
}
