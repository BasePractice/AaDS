package ru.mifi.practice.voln.heroes.net;

import ru.mifi.practice.voln.heroes.Tactics;

/**
 * Сообщение сетевой партии в текстовом виде: тип, затем поля через точку с запятой.
 *
 * <p>Формат намеренно читается глазами — партию можно проследить обычным curl, а сервер остаётся
 * простым реле: он передаёт строку сопернику, ничего не зная о правилах боя.
 *
 * <p>Ход не несёт координат ходящего отряда: очередь у обеих сторон одна и та же, и отряд всегда
 * берётся из её головы.
 */
public interface Message {

    String encode();

    /** Отдать себя получателю, не заставляя его выяснять свой тип. */
    void accept(Sink sink);

    /** Разобрать строку протокола; неизвестный тип — немедленный отказ, а не молчание. */
    static Message decode(String line) {
        String[] parts = line.split(";", -1);
        return switch (parts[0]) {
            case "ACTION" -> new Action(Tactics.Kind.valueOf(parts[1]),
                Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
            case "CHAT" -> new Chat(parts[1], line.split(";", 3)[2]);
            case "START" -> new Start(Long.parseLong(parts[1]));
            case "LEAVE" -> new Leave(parts[1]);
            default -> throw new IllegalArgumentException("Неизвестное сообщение протокола: " + line);
        };
    }

    /** Получатель сообщений комнаты. */
    interface Sink {

        void started(long seed);

        void acted(Action action);

        void said(String author, String text);

        void left(String author);
    }

    /** Ход соперника: что он сделал и с какой клеткой. */
    record Action(Tactics.Kind kind, int row, int column) implements Message {

        @Override
        public String encode() {
            return String.join(";", "ACTION", kind.name(), String.valueOf(row), String.valueOf(column));
        }

        @Override
        public void accept(Sink sink) {
            sink.acted(this);
        }
    }

    /** Реплика в чате комнаты. */
    record Chat(String author, String text) implements Message {

        @Override
        public String encode() {
            return String.join(";", "CHAT", author, text);
        }

        @Override
        public void accept(Sink sink) {
            sink.said(author, text);
        }
    }

    /** Оба игрока на местах: зерно, из которого обе стороны соберут одинаковое поле. */
    record Start(long seed) implements Message {

        @Override
        public String encode() {
            return String.join(";", "START", String.valueOf(seed));
        }

        @Override
        public void accept(Sink sink) {
            sink.started(seed);
        }
    }

    /** Соперник покинул комнату. */
    record Leave(String author) implements Message {

        @Override
        public String encode() {
            return String.join(";", "LEAVE", author);
        }

        @Override
        public void accept(Sink sink) {
            sink.left(author);
        }
    }
}
