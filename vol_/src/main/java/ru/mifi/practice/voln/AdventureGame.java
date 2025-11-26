package ru.mifi.practice.voln;

import lombok.Getter;
import ru.mifi.practice.voln.logic.Item;
import ru.mifi.practice.voln.logic.Person;
import ru.mifi.practice.voln.logic.Updatable;
import ru.mifi.practice.voln.transmit.Output;

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

    public AdventureGame(Output output, Person.Player player) {
        this.output = output;
        this.player = player;
        running = true;
        nextGameLine();
    }

    private void nextGameLine() {
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
                gameLine[index] = new Person.Mob("Paul", 80, index,
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
            default -> throw new IllegalStateException("Unexpected value: " + type);
        }
    }

    void update() {
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
            output.println("Game over");
            running = false;
        } else if (person instanceof Person.Mob mob) {
            gameLine[index] = null;
            index = mob.getIndex();
            gameLine[index] = player;
            player.addInventory(new Item.Health(100 - player.health()));
        }
    }

    private View viewIndex(int index) {
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
        return viewIndex(index + 1);
    }

    @Override
    public View view(Person.Mob mob) {
        int i = mob.getIndex();
        Objects.checkIndex(i - 1, gameLine.length);
        return viewIndex(i - 1);
    }

    @Override
    public void hit(Person person, Item item) {
        output.println("Hit " + person + " on " + item);
    }

    public View playerView() {
        return view(player);
    }

    public void forward() {
        if (index + 1 < gameLine.length && gameLine[index + 1] instanceof Person.Mob) {
            output.println("We cant move forward");
            return;
        }
        index++;
        if (index >= gameLine.length) {
            nextGameLine();
        } else {
            gameLine[index - 1] = null;
            gameLine[index] = player;
        }
    }

    public void attack() {
        View view = playerView();
        if (view.type() == Type.ENEMY) {
            Person.Mob mob = (Person.Mob) view.element();
            mob.hit(player.getSelectedItem(), this);
        } else {
            output.println("We cant kick empty space");
        }
    }

    public List<Item> listItems() {
        return player.items();
    }

    public void selectItem(int itemIndex) {
        player.selectItem(itemIndex, this);
    }

    public void catchItem() {
        Updatable.View view = playerView();
        if (view.type() == Updatable.Type.ITEM) {
            player.addInventory((Item) view.element());
            gameLine[index + 1] = null;
            forward();
        }
    }
}
