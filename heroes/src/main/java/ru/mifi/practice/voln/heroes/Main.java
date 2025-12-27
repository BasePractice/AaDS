package ru.mifi.practice.voln.heroes;

import ru.mifi.practice.voln.heroes.ui.BattleGui;

import javax.swing.SwingUtilities;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BattleMap map = new BattleMap();
            map.fillRandomly();
            BattleGui gui = new BattleGui(map);
            gui.setVisible(true);
        });
    }
}
