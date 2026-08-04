package ru.mifi.practice.voln.games.logic;

import ru.mifi.practice.voln.games.logic.Updatable.Context;

import java.util.ArrayList;
import java.util.List;

/** Сумка персонажа: хранит предметы, помнит выбранный и вытесняет слабейший при переполнении. */
final class Inventory {
    private static final Item FIST = new Item.DamageItem(5);
    private static final int LIMIT = 10;
    private final List<Item> items = new ArrayList<>();
    private Item selected = FIST;

    List<Item> items() {
        return items;
    }

    Item selected() {
        return selected;
    }

    void select(int index) {
        if (index < 0 || index >= items.size()) {
            selected = FIST;
            return;
        }
        selected = items.get(index);
    }

    void use(int index, Person owner, Context context) {
        if (index < 0 || index >= items.size()) {
            return;
        }
        Item element = items.get(index);
        if (element instanceof Item.Once once) {
            once.apply(owner, context);
            remove(index);
        } else {
            selected = element;
        }
    }

    void remove(int index) {
        if (index < 0 || index >= items.size()) {
            return;
        }
        Item item = items.remove(index);
        if (item.equals(selected)) {
            selected = FIST;
        }
    }

    void add(Item item, Context context) {
        items.add(item);
        while (items.size() > LIMIT) {
            int weakest = weakest();
            Item removed = items.get(weakest);
            if (context != null) {
                context.log("Removed (limit): " + removed);
            }
            remove(weakest);
        }
    }

    void clear() {
        items.clear();
        selected = FIST;
    }

    private int weakest() {
        int index = 0;
        int damage = items.get(0).damage();
        for (int i = 1; i < items.size(); i++) {
            if (items.get(i).damage() < damage) {
                damage = items.get(i).damage();
                index = i;
            }
        }
        return index;
    }
}
