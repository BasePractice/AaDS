package ru.mifi.practice.voln.transmit;

public interface Output {
    void print(String format, Object... args);

    default void println(String format, Object... args) {
        print(format + "%n", args);
    }
}
