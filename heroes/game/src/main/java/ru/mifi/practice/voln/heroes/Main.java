package ru.mifi.practice.voln.heroes;

import ru.mifi.practice.voln.heroes.net.Remote;
import ru.mifi.practice.voln.heroes.ui.Arena;
import ru.mifi.practice.voln.heroes.ui.Screen;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.net.URI;
import java.util.List;

/**
 * Точка входа: открывает окно игры с выбором режима и запускает выбранный.
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

    /** Выбор режима рисуется в самом окне игры, а не спрашивается диалогом поверх него. */
    private static void choose() {
        Screen screen = new Screen();
        screen.offer(List.of(
            new Screen.Choice("Против компьютера", () -> alone(screen)),
            new Screen.Choice("По сети", () -> online(screen)),
            new Screen.Choice("Выход", screen::dispose)));
        screen.setVisible(true);
    }

    private static void alone(Screen screen) {
        BattleMap map = new BattleMap();
        map.fillRandomly();
        screen.battle(new Battle.Local(map, new Tactics.Greedy()), new Talk.Silent());
    }

    private static void online(Screen screen) {
        String address = JOptionPane.showInputDialog(screen, "Адрес сервера", DEFAULT_ADDRESS);
        if (address == null || address.isBlank()) {
            return;
        }
        String nickname = JOptionPane.showInputDialog(screen, "Ваше имя", "герой");
        if (nickname == null || nickname.isBlank()) {
            return;
        }
        Thread joining = new Thread(() -> enter(
            new Arena(screen, new Remote.Http(URI.create(address.trim())), nickname.trim())), "heroes-join");
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
