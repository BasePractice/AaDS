package ru.mifi.practice.voln.heroes.net;

import ru.mifi.practice.voln.heroes.Battle;
import ru.mifi.practice.voln.heroes.BattleMap;
import ru.mifi.practice.voln.heroes.Tactics;
import ru.mifi.practice.voln.heroes.Talk;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Сетевая партия: наши решения уходят сопернику, его решения приходят из потока комнаты.
 *
 * <p>Поля у обеих сторон одинаковые — они собраны из одного зерна, — поэтому по сети едет не
 * состояние боя, а только само действие. Расхождение невозможно, пока обе стороны применяют
 * ходы в одном порядке, а порядок задаёт общая очередь.
 */
public final class Online implements Battle, Talk {
    private final BattleMap map;
    private final Remote remote;
    private final boolean left;
    private final String nickname;
    private final Deque<Message.Action> pending = new ArrayDeque<>();

    public Online(BattleMap map, Remote remote, boolean left, String nickname) {
        this.map = map;
        this.remote = remote;
        this.left = left;
        this.nickname = nickname;
    }

    @Override
    public BattleMap map() {
        return map;
    }

    @Override
    public boolean ours() {
        return map.isLeftTurn() == left;
    }

    @Override
    public void apply(Tactics.Decision decision) {
        perform(decision);
        remote.send(new Message.Action(decision.kind(), decision.row(), decision.column()));
    }

    /**
     * Ход соперника здесь не считается — он приходит из комнаты. Но пока на поле идёт анимация,
     * правила отвергают любое действие, поэтому пришедший ход ждёт своей очереди и применяется
     * отсюда, с очередного тика интерфейса.
     */
    @Override
    public void answer() {
        if (map.isAnimating() || pending.isEmpty()) {
            return;
        }
        Message.Action action = pending.pollFirst();
        perform(new Tactics.Decision(action.kind(), action.row(), action.column()));
    }

    /** Принять ход соперника, пришедший из комнаты. */
    public void accept(Message.Action action) {
        pending.addLast(action);
        answer();
    }

    @Override
    public boolean present() {
        return true;
    }

    @Override
    public void say(String text) {
        remote.send(new Message.Chat(nickname, text));
    }
}
