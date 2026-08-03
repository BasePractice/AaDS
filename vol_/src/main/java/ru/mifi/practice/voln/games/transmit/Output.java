package ru.mifi.practice.voln.games.transmit;

/** Приёмник форматированного вывода игры. */
public interface Output {
    void print(String format, Object... args);

    default void println(String format, Object... args) {
        print(format + "%n", args);
    }
}
