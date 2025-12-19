package ru.mifi.practice.voln.games.logic;

import ru.mifi.practice.voln.games.AdventureGame;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class GameAuto {
    private enum State {
        EXPLORE, FIGHT, HEAL
    }

    private final AdventureGame game;
    private final Person.Player player;
    private State state = State.EXPLORE;

    public GameAuto(AdventureGame game, Person.Player player) {
        this.game = game;
        this.player = player;
    }

    public void tick() {
        if (!game.isRunning()) {
            return;
        }
        cleanupInventory();
        selectBestWeapon();
        updateState();
        executeState();
    }

    private void cleanupInventory() {
        List<Item> items = player.items();
        Set<Integer> damages = new HashSet<>();
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            if (!(item instanceof Item.Once)) {
                int d = item.damage();
                if (damages.contains(d)) {
                    game.removeItem(i);
                    i--;
                } else {
                    damages.add(d);
                }
            }
        }
    }

    private void selectBestWeapon() {
        List<Item> items = player.items();
        Item current = player.getSelectedItem();
        int maxDamage = current.damage();
        int bestIndex = -1;

        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            if (!(item instanceof Item.Once) && item.damage() > maxDamage) {
                maxDamage = item.damage();
                bestIndex = i;
            }
        }

        if (bestIndex != -1) {
            game.selectItem(bestIndex);
        }
    }

    private void updateState() {
        Updatable.View view = game.playerView();
        int health = player.health();
        boolean hasPotion = hasHealthPotion();

        if (hasPotion && (health < 40 || (view.type() == Updatable.Type.ENEMY && health < 70))) {
            state = State.HEAL;
        } else if (view.type() == Updatable.Type.ENEMY) {
            state = State.FIGHT;
        } else {
            state = State.EXPLORE;
        }
    }

    private void executeState() {
        switch (state) {
            case HEAL -> {
                usePotion();
                game.update();
            }
            case FIGHT -> {
                game.attack();
                game.update();
            }
            case EXPLORE -> {
                game.forward();
                game.update();
            }
            default -> {
            }
        }
    }

    private boolean hasHealthPotion() {
        return player.items().stream().anyMatch(i -> i instanceof Item.Once);
    }

    private void usePotion() {
        List<Item> items = player.items();
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i) instanceof Item.Once) {
                game.useItem(i);
                return;
            }
        }
    }
}
