package ru.mifi.practice.voln.games;

import lombok.Getter;
import ru.mifi.practice.voln.games.logic.Item;
import ru.mifi.practice.voln.games.logic.Person;
import ru.mifi.practice.voln.games.logic.Updatable;
import ru.mifi.practice.voln.games.transmit.Output;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public final class AdventureGame implements Updatable, Updatable.Context {
    private static final int GAME_LINE_LENGTH = 20;
    private final Random random = new Random(new Date().getTime());
    private final Object[] gameLine = new Object[GAME_LINE_LENGTH + 1];
    private final Output output;
    private final Person.Player player;
    private int index;
    @Getter
    private boolean running;
    @Getter
    private int steps;
    @Getter
    private int level;

    public AdventureGame(Output output, Person.Player player) {
        this.output = output;
        this.player = player;
        running = true;
        nextGameLine();
    }

    public int getIdleTicks() {
        return player.getIdleTicks();
    }

    public int getLineLength() {
        return gameLine.length;
    }

    public int getPlayerIndex() {
        return index;
    }

    private void nextGameLine() {
        level++;
        Arrays.fill(gameLine, null);
        index = 0;
        gameLine[0] = player;
        int items = random.nextInt(3) + 5;
        int enemies = random.nextInt(7) + 2;
        for (int i = 0; i < items; i++) {
            generateElement(Type.ITEM);
        }
        for (int i = 0; i < enemies; i++) {
            generateElement(Type.ENEMY);
        }
    }

    private void generateElement(Type type) {
        int index = 2;
        while (gameLine[index] != null) {
            index = random.nextInt(gameLine.length - 3) + 2;
        }
        switch (type) {
            case ENEMY -> {
                gameLine[index] = new Person.Mob("Миша",
                    random.nextInt(50) + 40, index,
                    new Item.DamageItem(random.nextInt(10) + 5), false);
            }
            case EMPTY -> {
            }
            case ITEM -> {
                if (random.nextBoolean()) {
                    gameLine[index] = new Item.Health(random.nextInt(30) + 15);
                } else {
                    gameLine[index] = new Item.Hummer();
                }
            }
            default -> throw new IllegalStateException("Неизвестное значение: " + type);
        }
    }

    public void idleTick() {
        player.idleTick();
    }

    public void resetIdle() {
        player.resetIdle();
    }

    public void update() {
        steps++;
        update(this);
    }

    @Override
    public void update(Context context) {
        for (Object o : gameLine) {
            if (o instanceof Updatable update) {
                update.update(context);
            }
        }
    }

    @Override
    public void died(Person person) {
        if (person instanceof Person.Player) {
            output.println("Игра завершена");
            running = false;
        } else if (person instanceof Person.Mob mob) {
            player.addKill();
            gameLine[index] = null;
            index = mob.getIndex();
            gameLine[index] = player;
            if (player.health() < 100) {
                Item healthItem = new Item.Health(100 - player.health());
                player.addInventory(healthItem, this);
                output.println("Поднял: " + healthItem);
            }
        }
    }

    public View viewAt(int index) {
        Objects.checkIndex(index, gameLine.length);
        Object object = gameLine[index];
        if (object instanceof Item item) {
            return new View(Type.ITEM, item);
        } else if (object instanceof Person.Mob mob) {
            return new View(Type.ENEMY, mob);
        } else if (object instanceof Person.Player p) {
            return new View(Type.PLAYER, p);
        }
        return new View(Type.EMPTY, null);
    }

    @Override
    public View view(Person.Player player) {
        if (index + 1 >= gameLine.length) {
            return new View(Type.EMPTY, null);
        }
        return viewAt(index + 1);
    }

    @Override
    public View view(Person.Mob mob) {
        int i = mob.getIndex();
        Objects.checkIndex(i - 1, gameLine.length);
        return viewAt(i - 1);
    }

    @Override
    public void hit(Person person, Item item) {
        output.println("Ударил " + person + " этим " + item);
    }

    @Override
    public void log(String message) {
        output.println(message);
    }

    public View playerView() {
        return view(player);
    }

    public void forward() {
        if (index + 1 < gameLine.length && gameLine[index + 1] instanceof Person.Mob) {
            output.println("Нельзя двигаться вперед");
            return;
        }
        if (index + 1 < gameLine.length && gameLine[index + 1] instanceof Item item) {
            player.addInventory(item, this);
            output.println("Поднял: " + item);
        }
        index++;
        if (index >= gameLine.length) {
            nextGameLine();
        } else {
            gameLine[index - 1] = null;
            gameLine[index] = player;
        }
    }

    public void backward() {
        if (index <= 0) {
            output.println("Мы не можем двигаться назад");
            return;
        }
        if (gameLine[index - 1] instanceof Person.Mob) {
            output.println("Мы не можем двигаться назад");
            return;
        }
        if (gameLine[index - 1] instanceof Item item) {
            player.addInventory(item, this);
            output.println("Поднял: " + item);
        }
        gameLine[index] = null;
        index--;
        gameLine[index] = player;
    }

    public void restart() {
        steps = 0;
        level = 0;
        player.reset();
        running = true;
        nextGameLine();
    }

    public void attack() {
        View view = playerView();
        if (view.type() == Type.ENEMY) {
            Person.Mob mob = (Person.Mob) view.element();
            int bonus = player.getBaseAttack();
            if (bonus > 0) {
                mob.hit(new Item.BonusItem(player.getSelectedItem(), bonus), this);
            } else {
                mob.hit(player.getSelectedItem(), this);
            }
        } else {
            output.println("Мы не можем бить пустоту");
        }
    }

    public List<Item> listItems() {
        return player.items();
    }

    public void selectItem(int itemIndex) {
        player.selectItem(itemIndex, this);
    }

    public void useItem(int itemIndex) {
        player.useItem(itemIndex, this);
    }

    public void removeItem(int itemIndex) {
        List<Item> items = player.items();
        if (itemIndex >= 0 && itemIndex < items.size()) {
            output.println("Удалили: " + items.get(itemIndex));
        }
        player.removeItem(itemIndex);
    }

    public void catchItem() {
        Updatable.View view = playerView();
        if (view.type() == Updatable.Type.ITEM) {
            Item item = (Item) view.element();
            player.addInventory(item, this);
            output.println("Подняли: " + item);
            gameLine[index + 1] = null;
            forward();
        }
    }
}
