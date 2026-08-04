package ru.mifi.practice.voln.heroes.ui;

import ru.mifi.practice.voln.heroes.BattleMap;
import ru.mifi.practice.voln.heroes.net.Message;
import ru.mifi.practice.voln.heroes.net.Online;
import ru.mifi.practice.voln.heroes.net.Remote;

import javax.swing.SwingUtilities;

/**
 * Сетевой бой целиком: занять слот, показать пустое поле и ждать соперника, а дальше разводить
 * приходящие из комнаты сообщения по своим местам.
 *
 * <p>Окно открывается сразу после входа в комнату, ещё до того, как соперник найдётся: ожидание
 * видно в журнале боя, и отдельного диалога для него не нужно. Поле заполняется по зерну из
 * события начала — до этого момента оно пустое у обоих игроков.
 */
public final class Arena implements Message.Sink {
    private final Remote remote;
    private final String nickname;
    private Online battle;
    private BattleGui window;

    public Arena(Remote remote, String nickname) {
        this.remote = remote;
        this.nickname = nickname;
    }

    /**
     * Занять слот в комнате и подписаться на её события. Обращение к серверу блокирующее,
     * поэтому вызывать его нужно вне потока отрисовки — окно откроется оттуда само.
     */
    public void enter() {
        Remote.Seat seat = remote.join(nickname);
        SwingUtilities.invokeLater(() -> open(seat));
    }

    @Override
    public void started(long seed) {
        battle.map().fillRandomly(seed);
        window.logged("Соперник на месте, бой начинается");
    }

    @Override
    public void acted(Message.Action action) {
        battle.accept(action);
    }

    @Override
    public void said(String author, String text) {
        window.heard(author + ": " + text);
    }

    @Override
    public void left(String author) {
        window.logged(author + " покинул комнату");
    }

    private void open(Remote.Seat seat) {
        battle = new Online(new BattleMap(), remote, seat.left(), nickname);
        window = new BattleGui(battle, battle);
        window.setVisible(true);
        window.logged("Комната " + seat.room());
        window.logged("Играем за " + (seat.left() ? "ЛЕВЫХ" : "ПРАВЫХ"));
        window.logged("Ждём соперника");
        remote.listen(message -> SwingUtilities.invokeLater(() -> message.accept(this)));
    }
}
