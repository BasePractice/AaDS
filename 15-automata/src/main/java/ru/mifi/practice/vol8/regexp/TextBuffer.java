package ru.mifi.practice.vol8.regexp;

/**
 * Накопитель текста для генераторов диаграмм. Держит незавершённый вывод обхода, поэтому
 * буфер и живёт в поле: обходчик дописывает его по узлу за раз, а читает целиком в конце.
 */
@SuppressWarnings("PMD.AvoidStringBufferField")
public final class TextBuffer {
    private final StringBuilder text = new StringBuilder();

    public void append(Object part) {
        text.append(part);
    }

    public void line(Object part) {
        text.append(part).append('\n');
    }

    public void reset() {
        text.setLength(0);
    }

    @Override
    public String toString() {
        return text.toString();
    }
}
