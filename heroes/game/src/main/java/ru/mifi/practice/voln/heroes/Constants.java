package ru.mifi.practice.voln.heroes;

/** Размеры игрового поля и клетки. */
public final class Constants {
    public static final int ROWS = 11;
    public static final int COLS = 15;
    public static final int CELL_SIZE = 50;
    /** Самый длинный выстрел на поле: стрелок достаёт куда угодно, но с края в край — еле-еле. */
    public static final int SPAN = COLS - 1;

    private Constants() {
    }
}
