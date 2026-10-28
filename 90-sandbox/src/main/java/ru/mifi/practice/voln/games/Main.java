package ru.mifi.practice.voln.games;

import ru.mifi.practice.voln.games.logic.Item;
import ru.mifi.practice.voln.games.logic.Person;
import ru.mifi.practice.voln.games.logic.Updatable;
import ru.mifi.practice.voln.games.transmit.Transmit;

import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("PMD.LooseCoupling")
public abstract class Main {
    public static void main(String[] args) {
        Transmit transmit = new Transmit.Standard();
        transmit.println("Input f - move front");
        transmit.println("Input v - view front");
        transmit.println("Input s - select item");
        transmit.println("Input c - catch item");
        transmit.println("Input a - attack");
        transmit.println("Input w - waiting");
        transmit.println("Input q - quit");
        Person.Player player = new Person.Player("Unknown");
        AdventureGame game = new AdventureGame(transmit, player);
        LinkedList<Character> commands = new LinkedList<>();
        boolean isRunning = true;
        while (isRunning && game.isRunning()) {
            if (commands.isEmpty()) {
                transmit.print("Input player command: ");
                String input = transmit.readText();
                input.chars().forEach(c -> commands.add((char) c));
            }
            Character c = commands.removeFirst();
            switch (c) {
                case 'q': {
                    isRunning = false;
                    break;
                }
                case 'w': {
                    transmit.println("waiting...");
                    break;
                }
                case 'a': {
                    game.attack();
                    break;
                }
                case 'v': {
                    Updatable.View view = game.playerView();
                    transmit.println(view.toString());
                    break;
                }
                case 'c': {
                    game.catchItem();
                    break;
                }
                case 's': {
                    List<Item> items = game.listItems();
                    if (!items.isEmpty()) {
                        for (int i = 0; i < items.size(); i++) {
                            Item item = items.get(i);
                            transmit.println("[" + i + "] " + item.toString());
                        }
                        transmit.print("Select item or -1 skip: ");
                        var input = transmit.readInt().orElse(-1);
                        game.useItem(input);
                    }
                    break;
                }
                case 'f': {
                    game.forward();
                    break;
                }
                default: {
                    transmit.println("Invalid command");
                }
            }
            game.update();
        }
        transmit.println("game completed!");
    }
}
