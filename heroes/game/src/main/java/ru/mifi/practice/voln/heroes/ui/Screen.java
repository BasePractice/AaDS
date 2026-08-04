package ru.mifi.practice.voln.heroes.ui;

import ru.mifi.practice.voln.heroes.Battle;
import ru.mifi.practice.voln.heroes.Talk;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.util.List;

/**
 * Основное окно игры: сперва на нём нарисован выбор режима, потом — поле боя.
 *
 * <p>Окно одно на всю игру: режим выбирается не диалогом поверх, а тем же холстом, на котором
 * дальше идёт бой.
 */
public final class Screen extends JFrame {

    public Screen() {
        setTitle("Герои");
        setType(Type.UTILITY);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
    }

    /** Показать выбор: пункты рисуются на холсте окна. */
    public void offer(List<Choice> choices) {
        display(new Menu(choices));
    }

    /** Сменить выбор режима на поле боя. */
    public BattleGui battle(Battle battle, Talk talk) {
        BattleGui gui = new BattleGui(battle, talk);
        display(gui);
        return gui;
    }

    private void display(JPanel panel) {
        getContentPane().removeAll();
        add(panel, BorderLayout.CENTER);
        revalidate();
        pack();
        setLocationRelativeTo(null);
        repaint();
    }

    /** Пункт выбора: имя на холсте и то, что случится по клику. */
    public record Choice(String name, Runnable action) {
    }
}
