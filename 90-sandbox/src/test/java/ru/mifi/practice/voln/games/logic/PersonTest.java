package ru.mifi.practice.voln.games.logic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Person.Player")
final class PersonTest {

    @DisplayName("Инвентарь наполняется до предела в десять предметов")
    @Test
    @Timeout(1)
    void inventoryFillsUpToTenItems() {
        Person.Player player = new Person.Player("Герой");
        for (int i = 0; i < 10; i++) {
            player.addInventory(new Item.DamageItem(10 + i), null);
        }
        assertThat("inventory doesnt fill up to ten items", player.items().size(), is(10));
    }

    @DisplayName("Переполнение инвентаря удерживает десять предметов")
    @Test
    @Timeout(1)
    void inventoryOverflowKeepsTenItems() {
        Person.Player player = new Person.Player("Герой");
        for (int i = 0; i < 10; i++) {
            player.addInventory(new Item.DamageItem(10 + i), null);
        }
        player.addInventory(new Item.DamageItem(20), null);
        assertThat("inventory overflow doesnt keep exactly ten items", player.items().size(), is(10));
    }

    @DisplayName("Переполнение вытесняет предмет с наименьшим уроном")
    @Test
    @Timeout(1)
    void inventoryOverflowEvictsWeakestItem() {
        Person.Player player = new Person.Player("Герой");
        for (int i = 0; i < 10; i++) {
            player.addInventory(new Item.DamageItem(10 + i), null);
        }
        player.addInventory(new Item.DamageItem(20), null);
        boolean keepsWeakest = player.items().stream().anyMatch(item -> item.damage() == 10);
        assertThat("inventory overflow doesnt evict the weakest item", keepsWeakest, is(false));
    }

    @DisplayName("Переполнение сохраняет более сильный новый предмет")
    @Test
    @Timeout(1)
    void inventoryOverflowKeepsStrongerNewItem() {
        Person.Player player = new Person.Player("Герой");
        for (int i = 0; i < 10; i++) {
            player.addInventory(new Item.DamageItem(10 + i), null);
        }
        player.addInventory(new Item.DamageItem(20), null);
        boolean keepsNew = player.items().stream().anyMatch(item -> item.damage() == 20);
        assertThat("inventory overflow doesnt keep the stronger new item", keepsNew, is(true));
    }

    @DisplayName("Слабый новый предмет не попадает в полный инвентарь")
    @Test
    @Timeout(1)
    void weakNewItemDoesNotEnterFullInventory() {
        Person.Player player = new Person.Player("Герой");
        for (int i = 0; i < 10; i++) {
            player.addInventory(new Item.DamageItem(10 + i), null);
        }
        player.addInventory(new Item.DamageItem(20), null);
        player.addInventory(new Item.DamageItem(5), null);
        boolean weakStored = player.items().stream().anyMatch(item -> item.damage() == 5);
        assertThat("weak new item enters the full inventory", weakStored, is(false));
    }

    @DisplayName("Добавление слабого предмета удерживает десять предметов")
    @Test
    @Timeout(1)
    void addingWeakItemKeepsTenItems() {
        Person.Player player = new Person.Player("Герой");
        for (int i = 0; i < 10; i++) {
            player.addInventory(new Item.DamageItem(10 + i), null);
        }
        player.addInventory(new Item.DamageItem(20), null);
        player.addInventory(new Item.DamageItem(5), null);
        assertThat("adding a weak item doesnt keep exactly ten items", player.items().size(), is(10));
    }
}
