package ru.mifi.practice.voln.games;

import org.junit.jupiter.api.Test;
import ru.mifi.practice.voln.games.logic.GameAuto;
import ru.mifi.practice.voln.games.logic.Item;
import ru.mifi.practice.voln.games.logic.Person;
import ru.mifi.practice.voln.games.logic.Updatable;
import ru.mifi.practice.voln.games.transmit.Output;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PatternTest {
    @Test
    void testGameInitialization() {
        Output output = (format, args) -> {
        };
        Person.Player player = new Person.Player("Hero");
        AdventureGame game = new AdventureGame(output, player);
        assertTrue(game.isRunning());
        assertNotNull(game.viewAt(game.getPlayerIndex()));
        assertEquals(Updatable.Type.PLAYER, game.viewAt(game.getPlayerIndex()).type());
    }

    @Test
    void testPatternUpdate() {
        Output output = (format, args) -> {
        };
        Person.Player player = new Person.Player("Hero");
        AdventureGame game = new AdventureGame(output, player);
        int initialHealth = player.health();
        for (int i = 0; i < 1100; i++) {
            game.update();
        }
        assertEquals(initialHealth, player.health());
    }

    @Test
    void testBackwardMovement() {
        Output output = (format, args) -> {
        };
        Person.Player player = new Person.Player("Hero");
        AdventureGame game = new AdventureGame(output, player);
        game.forward();
        int indexAfterForward = game.getPlayerIndex();
        game.backward();
        assertEquals(indexAfterForward - 1, game.getPlayerIndex());
    }

    @Test
    void testBackwardAtStart() {
        Output output = (format, args) -> {
        };
        Person.Player player = new Person.Player("Hero");
        AdventureGame game = new AdventureGame(output, player);
        int initialIndex = game.getPlayerIndex();
        game.backward();
        assertEquals(initialIndex, game.getPlayerIndex());
    }

    @Test
    void testMobCounterAttack() {
        Output output = (format, args) -> {
        };
        Person.Player player = new Person.Player("Hero");
        AdventureGame game = new AdventureGame(output, player);
        int mobIndex = -1;
        for (int i = 0; i < game.getLineLength(); i++) {
            if (game.viewAt(i).type() == Updatable.Type.ENEMY) {
                mobIndex = i;
                break;
            }
        }
        if (mobIndex != -1) {
            while (game.getPlayerIndex() < mobIndex - 1) {
                game.forward();
            }
            int healthBefore = player.health();
            game.update();
            assertEquals(healthBefore, player.health());
            game.attack();
            game.update();
            assertTrue(player.health() < healthBefore);
        }
    }

    @Test
    void testAutomaticPickup() {
        Output output = (format, args) -> {
        };
        Person.Player player = new Person.Player("Hero");
        AdventureGame game = new AdventureGame(output, player);
        int itemIndex = -1;
        for (int i = 0; i < game.getLineLength(); i++) {
            if (game.viewAt(i).type() == Updatable.Type.ITEM) {
                itemIndex = i;
                break;
            }
        }
        if (itemIndex != -1) {
            while (game.getPlayerIndex() < itemIndex - 1) {
                game.forward();
            }
            int itemsBefore = player.items().size();
            game.forward();
            assertEquals(itemsBefore + 1, player.items().size());
        }
    }

    @Test
    void testRestart() {
        Output output = (format, args) -> {
        };
        Person.Player player = new Person.Player("Hero");
        AdventureGame game = new AdventureGame(output, player);
        player.healthUp(-50);
        player.addInventory(new Item.Hummer(), null);
        game.restart();
        assertEquals(100, player.health());
        assertTrue(player.items().isEmpty());
        assertTrue(game.isRunning());
        assertEquals(0, game.getPlayerIndex());
    }

    @Test
    void testStepCounter() {
        Output output = (format, args) -> {
        };
        Person.Player player = new Person.Player("Hero");
        AdventureGame game = new AdventureGame(output, player);
        assertEquals(0, game.getSteps());
        game.forward();
        game.update();
        assertEquals(1, game.getSteps());
        game.restart();
        assertEquals(0, game.getSteps());
    }

    @Test
    void testIdleRegen() {
        Output output = (format, args) -> {
        };
        Person.Player player = new Person.Player("Hero");
        AdventureGame game = new AdventureGame(output, player);
        player.healthUp(-10);
        int healthBefore = player.health();
        for (int i = 0; i < 1000; i++) {
            game.idleTick();
        }
        assertEquals(healthBefore + 1, player.health());
        game.resetIdle();
        for (int i = 0; i < 500; i++) {
            game.idleTick();
        }
        assertEquals(healthBefore + 1, player.health());
    }

    @Test
    void testLevelCounter() {
        Output output = (format, args) -> {
        };
        Person.Player player = new Person.Player("Hero");
        AdventureGame game = new AdventureGame(output, player);
        assertEquals(1, game.getLevel());
        for (int i = 0; i < 200; i++) {
            if (game.getLevel() > 1) {
                break;
            }
            game.forward();
            game.attack();
            game.update();
        }
        assertEquals(2, game.getLevel());
        game.restart();
        assertEquals(1, game.getLevel());
    }

    @Test
    void testNegativeHealthBug() {
        Output output = (format, args) -> {
        };
        Person.Player player = new Person.Player("Hero");
        AdventureGame game = new AdventureGame(output, player);
        player.healthUp(50);
        assertEquals(100, player.health());

        Person.Mob mob = new Person.Mob("Target", 10, 5, new Item.DamageItem(0), false);
        game.died(mob);

        boolean hasHealth = false;
        for (Item item : player.items()) {
            if (item instanceof Item.Health) {
                hasHealth = true;
                break;
            }
        }
        assertTrue(!hasHealth);
    }

    @Test
    void testAutoPilot() {
        Output output = (format, args) -> {
        };
        Person.Player player = new Person.Player("Hero");
        AdventureGame game = new AdventureGame(output, player);
        GameAuto auto = new GameAuto(game, player);
        int initialSteps = game.getSteps();
        auto.tick();
        assertTrue(game.getSteps() > initialSteps);
    }

    @Test
    void testAutoWeaponSelection() {
        Output output = (format, args) -> {
        };
        Person.Player player = new Person.Player("Hero");
        AdventureGame game = new AdventureGame(output, player);
        GameAuto auto = new GameAuto(game, player);

        assertEquals(5, player.getSelectedItem().damage());

        Item hammer = new Item.Hummer();
        player.addInventory(hammer, null);

        auto.tick();

        assertEquals(10, player.getSelectedItem().damage());
        assertEquals(hammer, player.getSelectedItem());
    }

    @Test
    void testAutoHealThreshold() {
        Output output = (format, args) -> {
        };
        Person.Player player = new Person.Player("Hero");
        AdventureGame game = new AdventureGame(output, player);
        GameAuto auto = new GameAuto(game, player);

        player.healthUp(-35);
        player.addInventory(new Item.Health(30), null);

        auto.tick();
        assertEquals(65, player.health());

        player.healthUp(-30);
        auto.tick();
        assertTrue(player.health() > 35);
    }

    @Test
    void testManualDeletion() {
        Output output = (format, args) -> {
        };
        Person.Player player = new Person.Player("Hero");
        AdventureGame game = new AdventureGame(output, player);
        Item hammer = new Item.Hummer();
        player.addInventory(hammer, null);
        game.selectItem(0);
        assertEquals(hammer, player.getSelectedItem());
        game.removeItem(0);
        assertTrue(player.items().isEmpty());
        assertTrue(player.getSelectedItem().damage() < hammer.damage());
    }

    @Test
    void testAutoInventoryCleanup() {
        final Output output = (format, args) -> {
        };
        Person.Player player = new Person.Player("Hero");
        player.addInventory(new Item.DamageItem(10), null);
        player.addInventory(new Item.DamageItem(10), null);
        player.addInventory(new Item.DamageItem(15), null);
        assertEquals(3, player.items().size());
        AdventureGame game = new AdventureGame(output, player);
        GameAuto auto = new GameAuto(game, player);
        auto.tick();
        assertEquals(2, player.items().size());
        long count10 = player.items().stream().filter(i -> i.damage() == 10).count();
        assertEquals(1, count10);
    }

    @Test
    void testBaseAttackProgression() {
        Output output = (format, args) -> {
        };
        Person.Player player = new Person.Player("Hero");
        AdventureGame game = new AdventureGame(output, player);
        assertEquals(0, player.getBaseAttack());

        for (int i = 0; i < 10; i++) {
            game.died(new Person.Mob("Target", 1, 5, new Item.DamageItem(0), false));
        }
        assertEquals(1, player.getBaseAttack());

        for (int i = 0; i < 10; i++) {
            game.died(new Person.Mob("Target", 1, 5, new Item.DamageItem(0), false));
        }
        assertEquals(2, player.getBaseAttack());

        game.restart();
        assertEquals(0, player.getBaseAttack());
    }

    @Test
    void testLogging() {
        java.util.List<String> logs = new java.util.ArrayList<>();
        Output output = (format, args) -> {
            logs.add(String.format(format, args).trim());
        };
        Person.Player player = new Person.Player("Герой");
        AdventureGame game = new AdventureGame(output, player);

        player.addInventory(new Item.Health(20), null);
        game.useItem(0);
        assertTrue(logs.stream().anyMatch(s -> s.contains("Использование эликсира: H20")));

        player.addInventory(new Item.Hummer(), null);
        game.removeItem(0);
        assertTrue(logs.stream().anyMatch(s -> s.contains("Удалили: D10")));

        int itemIndex = -1;
        for (int i = 0; i < game.getLineLength(); i++) {
            if (game.viewAt(i).type() == Updatable.Type.ITEM) {
                itemIndex = i;
                break;
            }
        }
        if (itemIndex != -1) {
            while (game.getPlayerIndex() < itemIndex - 1) {
                game.forward();
            }
            logs.clear();
            game.catchItem();
            assertTrue(logs.stream().anyMatch(s -> s.contains("Подняли:")));
        }
    }

    @Test
    void testInventoryLimit() {
        Person.Player player = new Person.Player("Герой");
        for (int i = 0; i < 10; i++) {
            player.addInventory(new Item.DamageItem(10 + i), null);
        }
        assertEquals(10, player.items().size());

        player.addInventory(new Item.DamageItem(20), null);
        assertEquals(10, player.items().size());
        assertTrue(player.items().stream().noneMatch(item -> item.damage() == 10));
        assertTrue(player.items().stream().anyMatch(item -> item.damage() == 20));

        player.addInventory(new Item.DamageItem(5), null);
        assertEquals(10, player.items().size());
        assertTrue(player.items().stream().noneMatch(item -> item.damage() == 5));
    }
}
