package ru.mifi.practice.voln.heroes.ui;

import lombok.Getter;
import ru.mifi.practice.voln.heroes.Battle;
import ru.mifi.practice.voln.heroes.BattleMap;
import ru.mifi.practice.voln.heroes.Tactics;
import ru.mifi.practice.voln.heroes.Talk;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.util.List;

/**
 * Окно боя: поле, кнопки хода и — в сетевой партии — панель разговора с соперником.
 *
 * <p>Ход соперника-программы считается по таймеру, а не сразу вслед за нашим: иначе вся очередь
 * правых отыгралась бы внутри одного обработчика клика и на экране выглядела бы как скачок.
 */
public final class BattleGui extends JFrame {
    private static final int ANSWER_DELAY = 400;

    @Getter
    private final transient Battle battle;
    private final BattlePanel board;
    private final ChatPanel chat;

    public BattleGui(Battle battle, Talk talk) {
        this.battle = battle;
        setTitle("Герои");
        setType(Type.UTILITY);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        this.board = new BattlePanel(this);
        this.chat = talk.present() ? new ChatPanel(talk) : null;
        add(board, BorderLayout.CENTER);
        if (chat != null) {
            add(chat, BorderLayout.EAST);
        }
        add(buttons(), BorderLayout.SOUTH);
        battle.map().addPropertyChangeListener(this::observe);
        new Timer(ANSWER_DELAY, e -> answer()).start();
        pack();
        setLocationRelativeTo(null);
    }

    public BattleMap getMap() {
        return battle.map();
    }

    /** Показать реплику соперника. */
    public void heard(String line) {
        if (chat != null) {
            chat.append(line);
        }
    }

    /** Записать событие боя в журнал. */
    public void logged(String line) {
        board.addLog(line);
        board.repaint();
    }

    private JPanel buttons() {
        JPanel row = new JPanel();
        JButton postpone = new JButton("Ожидание");
        postpone.addActionListener(e -> postpone());
        JButton skip = new JButton("Пропуск");
        skip.addActionListener(e -> skip());
        row.add(postpone);
        row.add(skip);
        return row;
    }

    /**
     * Ожидание меняет очередь ходов, поэтому идёт тем же путём, что удар и перемещение: иначе
     * в сетевой партии очередь у сторон разошлась бы после первого же нажатия.
     */
    private void postpone() {
        if (battle.ours()) {
            battle.apply(new Tactics.Decision(Tactics.Kind.WAIT, -1, -1));
            board.repaint();
        }
    }

    private void skip() {
        if (battle.ours()) {
            battle.apply(new Tactics.Decision(Tactics.Kind.SKIP, -1, -1));
            board.repaint();
        }
    }

    private void answer() {
        battle.answer();
        board.repaint();
    }

    private void observe(PropertyChangeEvent event) {
        if ("log".equals(event.getPropertyName())) {
            board.addLog((String) event.getNewValue());
        } else if ("move".equals(event.getPropertyName())) {
            //noinspection unchecked
            board.startAnimation((List<int[]>) event.getNewValue());
        }
        board.repaint();
    }
}
