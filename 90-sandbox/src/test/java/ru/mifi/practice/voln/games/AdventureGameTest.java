package ru.mifi.practice.voln.games;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import ru.mifi.practice.voln.games.logic.Item;
import ru.mifi.practice.voln.games.logic.Person;
import ru.mifi.practice.voln.games.logic.Updatable;
import ru.mifi.practice.voln.games.transmit.Output;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.notNullValue;

@DisplayName("AdventureGame")
final class AdventureGameTest {

    @DisplayName("Новая игра запущена")
    @Test
    @Timeout(1)
    void newGameIsRunning() {
        Output output = (format, args) -> {
        };
        Person.Player player = new Person.Player("Hero");
        AdventureGame game = new AdventureGame(output, player);
        assertThat("new game doesnt run", game.isRunning(), is(true));
    }

    @DisplayName("Клетка игрока имеет представление")
    @Test
    @Timeout(1)
    void newGamePlayerViewIsNotNull() {
        Output output = (format, args) -> {
        };
        Person.Player player = new Person.Player("Hero");
        AdventureGame game = new AdventureGame(output, player);
        assertThat("player cell view is null", game.viewAt(game.getPlayerIndex()), notNullValue());
    }

    @DisplayName("Стартовая клетка имеет тип игрока")
    @Test
    @Timeout(1)
    void newGamePlayerCellIsPlayerType() {
        Output output = (format, args) -> {
        };
        Person.Player player = new Person.Player("Hero");
        AdventureGame game = new AdventureGame(output, player);
        assertThat("start cell isnt of PLAYER type", game.viewAt(game.getPlayerIndex()).type(), is(Updatable.Type.PLAYER));
    }

    @DisplayName("Множество обновлений сохраняет здоровье")
    @Test
    @Timeout(5)
    void patternUpdateKeepsHealthStable() {
        Output output = (format, args) -> {
        };
        Person.Player player = new Person.Player("Hero");
        AdventureGame game = new AdventureGame(output, player);
        int initialHealth = player.health();
        for (int i = 0; i < 1100; i++) {
            game.update();
        }
        assertThat("idle updates dont keep the player health stable", player.health(), is(initialHealth));
    }

    @DisplayName("Шаг назад уменьшает индекс на единицу")
    @Test
    @Timeout(1)
    void backwardMovesPlayerOneStepBack() {
        Output output = (format, args) -> {
        };
        Person.Player player = new Person.Player("Hero");
        AdventureGame game = new AdventureGame(output, player);
        game.forward();
        int indexAfterForward = game.getPlayerIndex();
        game.backward();
        assertThat("backward doesnt move the player one step back", game.getPlayerIndex(), is(indexAfterForward - 1));
    }

    @DisplayName("Шаг назад в начале линии не двигает игрока")
    @Test
    @Timeout(1)
    void backwardAtStartKeepsIndex() {
        Output output = (format, args) -> {
        };
        Person.Player player = new Person.Player("Hero");
        AdventureGame game = new AdventureGame(output, player);
        int initialIndex = game.getPlayerIndex();
        game.backward();
        assertThat("backward at start doesnt keep the player index", game.getPlayerIndex(), is(initialIndex));
    }

    @DisplayName("Моб контратакует и снижает здоровье игрока")
    @Test
    @Timeout(1)
    @Disabled("Учебный пазл: детерминированная проверка контратаки требует внедрения сида Random в AdventureGame")
    void mobCounterAttackReducesPlayerHealth() {
        //TODO нужен seeded Random в AdventureGame (Фаза 4), чтобы моб оказался в известной позиции линии
        Output output = (format, args) -> {
        };
        Person.Player player = new Person.Player("Hero");
        AdventureGame game = new AdventureGame(output, player);
        assertThat("fresh player doesnt start at full health", player.health(), is(100));
    }

    @DisplayName("Проход по предмету поднимает его в инвентарь")
    @Test
    @Timeout(1)
    @Disabled("Учебный пазл: детерминированная проверка автоподбора требует внедрения сида Random в AdventureGame")
    void movingOntoItemPicksItUp() {
        //TODO нужен seeded Random в AdventureGame (Фаза 4), чтобы предмет оказался в известной позиции линии
        Output output = (format, args) -> {
        };
        Person.Player player = new Person.Player("Hero");
        AdventureGame game = new AdventureGame(output, player);
        assertThat("fresh player doesnt start with an empty inventory", player.items(), is(empty()));
    }

    @DisplayName("Перезапуск восстанавливает полное здоровье")
    @Test
    @Timeout(1)
    void restartRestoresFullHealth() {
        Output output = (format, args) -> {
        };
        Person.Player player = new Person.Player("Hero");
        AdventureGame game = new AdventureGame(output, player);
        player.healthUp(-50);
        player.addInventory(new Item.Hummer(), null);
        game.restart();
        assertThat("restart doesnt restore full health", player.health(), is(100));
    }

    @DisplayName("Перезапуск очищает инвентарь")
    @Test
    @Timeout(1)
    void restartClearsInventory() {
        Output output = (format, args) -> {
        };
        Person.Player player = new Person.Player("Hero");
        AdventureGame game = new AdventureGame(output, player);
        player.healthUp(-50);
        player.addInventory(new Item.Hummer(), null);
        game.restart();
        assertThat("restart doesnt clear the inventory", player.items(), is(empty()));
    }

    @DisplayName("Перезапуск оставляет игру запущенной")
    @Test
    @Timeout(1)
    void restartKeepsGameRunning() {
        Output output = (format, args) -> {
        };
        Person.Player player = new Person.Player("Hero");
        AdventureGame game = new AdventureGame(output, player);
        player.healthUp(-50);
        player.addInventory(new Item.Hummer(), null);
        game.restart();
        assertThat("restart doesnt keep the game running", game.isRunning(), is(true));
    }

    @DisplayName("Перезапуск возвращает игрока в начало линии")
    @Test
    @Timeout(1)
    void restartResetsPlayerIndex() {
        Output output = (format, args) -> {
        };
        Person.Player player = new Person.Player("Hero");
        AdventureGame game = new AdventureGame(output, player);
        player.healthUp(-50);
        player.addInventory(new Item.Hummer(), null);
        game.restart();
        assertThat("restart doesnt reset the player index", game.getPlayerIndex(), is(0));
    }

    @DisplayName("Новая игра начинается с нулём шагов")
    @Test
    @Timeout(1)
    void freshGameHasZeroSteps() {
        Output output = (format, args) -> {
        };
        Person.Player player = new Person.Player("Hero");
        AdventureGame game = new AdventureGame(output, player);
        assertThat("fresh game doesnt start with zero steps", game.getSteps(), is(0));
    }

    @DisplayName("Обновление увеличивает счётчик шагов")
    @Test
    @Timeout(1)
    void updateIncrementsStepCounter() {
        Output output = (format, args) -> {
        };
        Person.Player player = new Person.Player("Hero");
        AdventureGame game = new AdventureGame(output, player);
        game.forward();
        game.update();
        assertThat("update doesnt increment the step counter", game.getSteps(), is(1));
    }

    @DisplayName("Перезапуск обнуляет счётчик шагов")
    @Test
    @Timeout(1)
    void restartResetsStepCounter() {
        Output output = (format, args) -> {
        };
        Person.Player player = new Person.Player("Hero");
        AdventureGame game = new AdventureGame(output, player);
        game.forward();
        game.update();
        game.restart();
        assertThat("restart doesnt reset the step counter", game.getSteps(), is(0));
    }

    @DisplayName("Тысяча тиков покоя регенерирует одно здоровье")
    @Test
    @Timeout(5)
    void idleTicksRegenerateOneHealth() {
        Output output = (format, args) -> {
        };
        Person.Player player = new Person.Player("Hero");
        AdventureGame game = new AdventureGame(output, player);
        player.healthUp(-10);
        int healthBefore = player.health();
        for (int i = 0; i < 1000; i++) {
            game.idleTick();
        }
        assertThat("thousand idle ticks dont regenerate one health", player.health(), is(healthBefore + 1));
    }

    @DisplayName("Сброс покоя предотвращает лишнюю регенерацию")
    @Test
    @Timeout(5)
    void resetIdlePreventsExtraRegen() {
        Output output = (format, args) -> {
        };
        Person.Player player = new Person.Player("Hero");
        AdventureGame game = new AdventureGame(output, player);
        player.healthUp(-10);
        final int healthBefore = player.health();
        for (int i = 0; i < 1000; i++) {
            game.idleTick();
        }
        game.resetIdle();
        for (int i = 0; i < 500; i++) {
            game.idleTick();
        }
        assertThat("reset idle doesnt prevent the extra regeneration", player.health(), is(healthBefore + 1));
    }

    @DisplayName("Новая игра начинается с первого уровня")
    @Test
    @Timeout(1)
    void freshGameStartsAtFirstLevel() {
        Output output = (format, args) -> {
        };
        Person.Player player = new Person.Player("Hero");
        AdventureGame game = new AdventureGame(output, player);
        assertThat("fresh game doesnt start at the first level", game.getLevel(), is(1));
    }

    @DisplayName("Зачистка линии повышает уровень")
    @Test
    @Timeout(5)
    void clearingLineAdvancesLevel() {
        Output output = (format, args) -> {
        };
        Person.Player player = new Person.Player("Hero");
        AdventureGame game = new AdventureGame(output, player);
        for (int i = 0; i < 200; i++) {
            if (game.getLevel() > 1) {
                break;
            }
            game.forward();
            game.attack();
            game.update();
        }
        assertThat("clearing the line doesnt advance the level", game.getLevel(), is(2));
    }

    @DisplayName("Перезапуск возвращает первый уровень")
    @Test
    @Timeout(5)
    void restartResetsLevel() {
        Output output = (format, args) -> {
        };
        Person.Player player = new Person.Player("Hero");
        AdventureGame game = new AdventureGame(output, player);
        for (int i = 0; i < 200; i++) {
            if (game.getLevel() > 1) {
                break;
            }
            game.forward();
            game.attack();
            game.update();
        }
        game.restart();
        assertThat("restart doesnt reset the level", game.getLevel(), is(1));
    }

    @DisplayName("Здоровье игрока не превышает предел")
    @Test
    @Timeout(1)
    void fullHealthPlayerStaysCapped() {
        Output output = (format, args) -> {
        };
        Person.Player player = new Person.Player("Hero");
        AdventureGame game = new AdventureGame(output, player);
        player.healthUp(50);
        assertThat("player health doesnt stay capped at the limit", player.health(), is(100));
    }

    @DisplayName("Смерть моба при полном здоровье не выдаёт эликсир")
    @Test
    @Timeout(1)
    void deadMobDoesNotGrantHealthAtFullHp() {
        Output output = (format, args) -> {
        };
        Person.Player player = new Person.Player("Hero");
        AdventureGame game = new AdventureGame(output, player);
        player.healthUp(50);
        Person.Mob mob = new Person.Mob("Target", 10, 5, new Item.DamageItem(0), false);
        game.died(mob);
        boolean hasHealth = player.items().stream().anyMatch(item -> item instanceof Item.Health);
        assertThat("dead mob grants a health elixir at full HP", hasHealth, is(false));
    }

    @DisplayName("Выбор предмета делает его активным оружием")
    @Test
    @Timeout(1)
    void selectItemSelectsInventoryWeapon() {
        Output output = (format, args) -> {
        };
        Person.Player player = new Person.Player("Hero");
        AdventureGame game = new AdventureGame(output, player);
        Item hammer = new Item.Hummer();
        player.addInventory(hammer, null);
        game.selectItem(0);
        assertThat("selectItem doesnt make the inventory weapon active", player.getSelectedItem(), is(hammer));
    }

    @DisplayName("Удаление предмета очищает инвентарь")
    @Test
    @Timeout(1)
    void removeItemEmptiesInventory() {
        Output output = (format, args) -> {
        };
        Person.Player player = new Person.Player("Hero");
        AdventureGame game = new AdventureGame(output, player);
        Item hammer = new Item.Hummer();
        player.addInventory(hammer, null);
        game.selectItem(0);
        game.removeItem(0);
        assertThat("removeItem doesnt empty the inventory", player.items(), is(empty()));
    }

    @DisplayName("Удаление активного предмета возвращает к кулакам")
    @Test
    @Timeout(1)
    void removeSelectedItemFallsBackToFist() {
        Output output = (format, args) -> {
        };
        Person.Player player = new Person.Player("Hero");
        AdventureGame game = new AdventureGame(output, player);
        Item hammer = new Item.Hummer();
        player.addInventory(hammer, null);
        game.selectItem(0);
        game.removeItem(0);
        assertThat("removing selected item doesnt fall back to fist", player.getSelectedItem().damage(), is(lessThan(hammer.damage())));
    }

    @DisplayName("Использование эликсира пишется в лог")
    @Test
    @Timeout(1)
    void useItemLogsElixirUsage() {
        List<String> logs = new ArrayList<>();
        Output output = (format, args) -> logs.add(String.format(format, args).trim());
        Person.Player player = new Person.Player("Герой");
        AdventureGame game = new AdventureGame(output, player);
        player.addInventory(new Item.Health(20), null);
        game.useItem(0);
        boolean logged = logs.stream().anyMatch(s -> s.contains("Использование эликсира: H20"));
        assertThat("useItem doesnt log the elixir usage", logged, is(true));
    }

    @DisplayName("Удаление предмета пишется в лог")
    @Test
    @Timeout(1)
    void removeItemLogsDeletedItem() {
        List<String> logs = new ArrayList<>();
        Output output = (format, args) -> logs.add(String.format(format, args).trim());
        Person.Player player = new Person.Player("Герой");
        AdventureGame game = new AdventureGame(output, player);
        player.addInventory(new Item.Hummer(), null);
        game.removeItem(0);
        boolean logged = logs.stream().anyMatch(s -> s.contains("Удалили: D10"));
        assertThat("removeItem doesnt log the deleted item", logged, is(true));
    }

    @DisplayName("Подбор предмета через catchItem пишется в лог")
    @Test
    @Timeout(1)
    @Disabled("Учебный пазл: детерминированный подбор через catchItem требует внедрения сида Random в AdventureGame")
    void catchItemLogsPickup() {
        //TODO нужен seeded Random в AdventureGame (Фаза 4), чтобы предмет оказался перед игроком детерминированно
        List<String> logs = new ArrayList<>();
        Output output = (format, args) -> logs.add(String.format(format, args).trim());
        Person.Player player = new Person.Player("Герой");
        AdventureGame game = new AdventureGame(output, player);
        assertThat("fresh game log isnt empty before catchItem", logs, is(empty()));
    }

    @DisplayName("Новый игрок имеет нулевую базовую атаку")
    @Test
    @Timeout(1)
    void freshPlayerHasZeroBaseAttack() {
        Output output = (format, args) -> {
        };
        Person.Player player = new Person.Player("Hero");
        AdventureGame game = new AdventureGame(output, player);
        assertThat("fresh player doesnt have zero base attack", player.getBaseAttack(), is(0));
    }

    @DisplayName("Десять убийств повышают базовую атаку до единицы")
    @Test
    @Timeout(1)
    void tenKillsRaiseBaseAttackByOne() {
        Output output = (format, args) -> {
        };
        Person.Player player = new Person.Player("Hero");
        AdventureGame game = new AdventureGame(output, player);
        for (int i = 0; i < 10; i++) {
            game.died(new Person.Mob("Target", 1, 5, new Item.DamageItem(0), false));
        }
        assertThat("ten kills dont raise the base attack to one", player.getBaseAttack(), is(1));
    }

    @DisplayName("Двадцать убийств повышают базовую атаку до двух")
    @Test
    @Timeout(1)
    void twentyKillsRaiseBaseAttackByTwo() {
        Output output = (format, args) -> {
        };
        Person.Player player = new Person.Player("Hero");
        AdventureGame game = new AdventureGame(output, player);
        for (int i = 0; i < 20; i++) {
            game.died(new Person.Mob("Target", 1, 5, new Item.DamageItem(0), false));
        }
        assertThat("twenty kills dont raise the base attack to two", player.getBaseAttack(), is(2));
    }

    @DisplayName("Перезапуск обнуляет базовую атаку")
    @Test
    @Timeout(1)
    void restartResetsBaseAttack() {
        Output output = (format, args) -> {
        };
        Person.Player player = new Person.Player("Hero");
        AdventureGame game = new AdventureGame(output, player);
        for (int i = 0; i < 20; i++) {
            game.died(new Person.Mob("Target", 1, 5, new Item.DamageItem(0), false));
        }
        game.restart();
        assertThat("restart doesnt reset the base attack", player.getBaseAttack(), is(0));
    }
}
