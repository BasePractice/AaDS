package ru.mifi.practice.voln.games.logic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import ru.mifi.practice.voln.games.AdventureGame;
import ru.mifi.practice.voln.games.transmit.Output;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

@DisplayName("GameAuto")
final class GameAutoTest {

    @DisplayName("Автопилот делает шаг в игре")
    @Test
    @Timeout(1)
    void autoPilotAdvancesSteps() {
        Output output = (format, args) -> {
        };
        Person.Player player = new Person.Player("Hero");
        AdventureGame game = new AdventureGame(output, player);
        GameAuto auto = new GameAuto(game, player);
        int initialSteps = game.getSteps();
        auto.tick();
        assertThat("auto pilot doesnt advance the step counter", game.getSteps(), is(greaterThan(initialSteps)));
    }

    @DisplayName("Новый игрок бьёт кулаком на пять урона")
    @Test
    @Timeout(1)
    void freshPlayerUsesFistDamage() {
        Output output = (format, args) -> {
        };
        Person.Player player = new Person.Player("Hero");
        AdventureGame game = new AdventureGame(output, player);
        GameAuto auto = new GameAuto(game, player);
        assertThat("fresh player doesnt start with the fist damage", player.getSelectedItem().damage(), is(5));
    }

    @DisplayName("Автовыбор оружия поднимает урон до молота")
    @Test
    @Timeout(1)
    void autoWeaponSelectionRaisesDamage() {
        Output output = (format, args) -> {
        };
        Person.Player player = new Person.Player("Hero");
        AdventureGame game = new AdventureGame(output, player);
        GameAuto auto = new GameAuto(game, player);
        player.addInventory(new Item.Hummer(), null);
        auto.tick();
        assertThat("auto weapon selection doesnt raise the damage to the hammer", player.getSelectedItem().damage(), is(10));
    }

    @DisplayName("Автовыбор оружия делает молот активным")
    @Test
    @Timeout(1)
    void autoWeaponSelectionPicksHammer() {
        Output output = (format, args) -> {
        };
        Person.Player player = new Person.Player("Hero");
        AdventureGame game = new AdventureGame(output, player);
        GameAuto auto = new GameAuto(game, player);
        Item hammer = new Item.Hummer();
        player.addInventory(hammer, null);
        auto.tick();
        assertThat("auto weapon selection doesnt pick the hammer", player.getSelectedItem(), is(hammer));
    }

    @DisplayName("Автолечение восстанавливает здоровье эликсиром")
    @Test
    @Timeout(1)
    void autoHealAppliesPotion() {
        Output output = (format, args) -> {
        };
        Person.Player player = new Person.Player("Hero");
        AdventureGame game = new AdventureGame(output, player);
        GameAuto auto = new GameAuto(game, player);
        player.healthUp(-35);
        player.addInventory(new Item.Health(30), null);
        auto.tick();
        assertThat("auto heal doesnt apply the potion", player.health(), is(65));
    }

    @DisplayName("Автолечение держит здоровье выше порога")
    @Test
    @Timeout(1)
    void autoHealKeepsHealthAboveThreshold() {
        Output output = (format, args) -> {
        };
        Person.Player player = new Person.Player("Hero");
        AdventureGame game = new AdventureGame(output, player);
        GameAuto auto = new GameAuto(game, player);
        player.healthUp(-35);
        player.addInventory(new Item.Health(30), null);
        auto.tick();
        player.healthUp(-30);
        auto.tick();
        assertThat("auto heal doesnt keep the health above the threshold", player.health(), is(greaterThan(35)));
    }

    @DisplayName("Автоочистка убирает дублирующее оружие")
    @Test
    @Timeout(1)
    void autoCleanupRemovesDuplicateWeapon() {
        Output output = (format, args) -> {
        };
        Person.Player player = new Person.Player("Hero");
        player.addInventory(new Item.DamageItem(10), null);
        player.addInventory(new Item.DamageItem(10), null);
        player.addInventory(new Item.DamageItem(15), null);
        AdventureGame game = new AdventureGame(output, player);
        GameAuto auto = new GameAuto(game, player);
        auto.tick();
        assertThat("auto cleanup doesnt remove the duplicate weapon", player.items().size(), is(2));
    }

    @DisplayName("Автоочистка оставляет один экземпляр урона")
    @Test
    @Timeout(1)
    void autoCleanupKeepsSingleDamageCopy() {
        Output output = (format, args) -> {
        };
        Person.Player player = new Person.Player("Hero");
        player.addInventory(new Item.DamageItem(10), null);
        player.addInventory(new Item.DamageItem(10), null);
        player.addInventory(new Item.DamageItem(15), null);
        AdventureGame game = new AdventureGame(output, player);
        GameAuto auto = new GameAuto(game, player);
        auto.tick();
        long count10 = player.items().stream().filter(i -> i.damage() == 10).count();
        assertThat("auto cleanup doesnt keep a single copy of the duplicated damage", count10, is(1L));
    }
}
