package ru.mifi.practice.voln.heroes.net;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import ru.mifi.practice.voln.heroes.BattleMap;
import ru.mifi.practice.voln.heroes.Tactics;
import ru.mifi.practice.voln.heroes.Unit;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

/** Проверка сетевой партии: отправка своих ходов и приём чужих поверх идущей анимации. */
@DisplayName("Сетевая партия")
final class OnlineTest {

    @DisplayName("Наш ход уходит сопернику")
    @Test
    @Timeout(1)
    void sendsOurDecisionToTheOpponent() {
        BattleMap map = new BattleMap();
        map.addLeft(5, 5, stack());
        List<Message> sent = new ArrayList<>();
        new Online(map, new Loopback(sent), true, "герой")
            .apply(new Tactics.Decision(Tactics.Kind.MOVE, 5, 7));
        assertThat("our decision dont reach the opponent",
            sent, hasItem(new Message.Action(Tactics.Kind.MOVE, 5, 7)));
    }

    @DisplayName("Реплика уходит сопернику подписанной")
    @Test
    @Timeout(1)
    void signsTheChatWithOurName() {
        List<Message> sent = new ArrayList<>();
        new Online(new BattleMap(), new Loopback(sent), true, "герой").say("привет");
        assertThat("the chat message dont carry our name",
            sent, hasItem(new Message.Chat("герой", "привет")));
    }

    @DisplayName("Пока идёт анимация, ход соперника ждёт")
    @Test
    @Timeout(1)
    void holdsTheOpponentActionWhileAnimating() {
        BattleMap map = new BattleMap();
        map.addLeft(5, 5, stack());
        map.addRight(5, 9, stack());
        Online battle = new Online(map, new Loopback(new ArrayList<>()), true, "герой");
        battle.apply(new Tactics.Decision(Tactics.Kind.MOVE, 5, 6));
        battle.accept(new Message.Action(Tactics.Kind.MOVE, 5, 8));
        assertThat("the opponent action lands while the field is still animating",
            map.getStack(5, 8), is(nullValue()));
    }

    @DisplayName("После анимации отложенный ход соперника применяется")
    @Test
    @Timeout(1)
    void appliesTheHeldActionAfterTheAnimation() {
        BattleMap map = new BattleMap();
        map.addLeft(5, 5, stack());
        map.addRight(5, 9, stack());
        Online battle = new Online(map, new Loopback(new ArrayList<>()), true, "герой");
        battle.apply(new Tactics.Decision(Tactics.Kind.MOVE, 5, 6));
        battle.accept(new Message.Action(Tactics.Kind.MOVE, 5, 8));
        map.endAction();
        battle.answer();
        assertThat("the held action never reaches the field", map.getStack(5, 8), is(notNullValue()));
    }

    @DisplayName("Пока ходит соперник, ход не наш")
    @Test
    @Timeout(1)
    void countsTheOpponentTurnAsTheirs() {
        BattleMap map = new BattleMap();
        map.addLeft(5, 5, stack());
        assertThat("the left turn counts as ours while we play the right side",
            new Online(map, new Loopback(new ArrayList<>()), false, "герой").ours(), is(false));
    }

    private static Unit.Stack stack() {
        Unit.Stack stack = new Unit.Stack(Unit.Type.WALKER);
        stack.add(new Unit(100, 0, 100, 5));
        return stack;
    }

    /** Канал-заглушка: запоминает отправленное и ничего никуда не шлёт. */
    private record Loopback(List<Message> sent) implements Remote {

        @Override
        public Seat join(String nickname) {
            return new Seat(UUID.randomUUID(), true, 0L);
        }

        @Override
        public void send(Message message) {
            sent.add(message);
        }

        @Override
        public void listen(Consumer<Message> sink) {
            throw new IllegalStateException("Заглушка не слушает поток комнаты");
        }

        @Override
        public void close() {
            sent.clear();
        }
    }
}
