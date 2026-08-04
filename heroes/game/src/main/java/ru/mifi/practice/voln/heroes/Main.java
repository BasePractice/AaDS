package ru.mifi.practice.voln.heroes;

import ru.mifi.practice.voln.heroes.net.Remote;
import ru.mifi.practice.voln.heroes.ui.Arena;
import ru.mifi.practice.voln.heroes.ui.BattleGui;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.net.URI;

/**
 * Точка входа: спрашивает режим боя и запускает выбранный.
 *
 * <p>Режима два. Локальный — левыми играет человек, правыми программа. Сетевой — вторая сторона
 * сидит за другой машиной, а сервер сводит игроков и передаёт ходы и реплики.
 */
public final class Main {
    private static final String DEFAULT_ADDRESS = "http://localhost:9097";

    private Main() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::choose);
    }

    private static void choose() {
        Object[] modes = {"Против компьютера", "По сети"};
        int mode = JOptionPane.showOptionDialog(null, "Выберите режим боя", "Герои",
            JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, modes, modes[0]);
        if (mode == 1) {
            online();
        } else if (mode == 0) {
            alone();
        }
    }

    private static void alone() {
        BattleMap map = new BattleMap();
        map.fillRandomly();
        new BattleGui(new Battle.Local(map, new Tactics.Greedy()), new Talk.Silent()).setVisible(true);
    }

    private static void online() {
        String address = JOptionPane.showInputDialog(null, "Адрес сервера", DEFAULT_ADDRESS);
        if (address == null || address.isBlank()) {
            return;
        }
        String nickname = JOptionPane.showInputDialog(null, "Ваше имя", "герой");
        if (nickname == null || nickname.isBlank()) {
            return;
        }
        Thread joining = new Thread(
            () -> enter(new Arena(new Remote.Http(URI.create(address.trim())), nickname.trim())), "heroes-join");
        joining.setDaemon(true);
        joining.start();
    }

    /** Вход в комнату идёт по сети, поэтому отказ сервера показывается диалогом, а не трассой. */
    private static void enter(Arena arena) {
        try {
            arena.enter();
        } catch (RuntimeException e) {
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null,
                e.getMessage(), "Не удалось войти в комнату", JOptionPane.ERROR_MESSAGE));
        }
    }
}
