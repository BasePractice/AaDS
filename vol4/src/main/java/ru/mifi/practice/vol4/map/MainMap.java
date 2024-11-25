package ru.mifi.practice.vol4.map;

import ru.mifi.practice.vol4.Counter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

public abstract class MainMap {
    private static final int CAPACITY = 10;

    public static void main(String[] args) {
        HashTable<String, String> map = new HashTable.Default<>(3);
        add("Пушкин", "наше все", map);
        add("Дантес", "не наше, не все", map);
        add("Блок", "аптека, улица, фонарь", map);
        add("Есенин", "осень настала", map);
        add("Чуковский", "а лисички взяли спички", map);
        add("Народ", "из-за леса из-за гор", map);
        add("Маяковский", "а вы ноктюрн сыграть смогли бы?", map);
        add("Цой", "Ты должен быть сильным", map);
        System.out.println("=================");
        System.out.println(map);
        System.out.println("=================");
        remove("Цой", map);
        System.out.println(map);
        remove("Есенин", map);
        System.out.println(map);
        map.clear();
        HashTable<String, Integer> stat = new HashTable.Default<>(CAPACITY);
        stream("/90202836.txt").forEach(text -> {
            Counter.Default counter = new Counter.Default();
            Optional<Integer> element = stat.remove(text, counter);
            stat.put(text, element.map(n -> n + 1).orElse(1), counter);
        });
        System.out.println("=================");
        stat.sorted((o1, o2) -> Integer.compare(o2.value, o1.value))
            .limit(10).forEach(entry -> {
                System.out.printf("Prn. %15s: %s%n", entry.key, entry.value);
            });
        System.out.println("=================");
        SetTable<String> using = new SetTable.Default<>(CAPACITY);
        stream("/90202836.txt").forEach(text -> {
            Counter.Default counter = new Counter.Default();
            if (using.contains(text, counter)) {
                System.out.printf("Prn. %15s: %s%n", text, counter);
            } else {
                using.add(text, counter);
            }
        });
    }

    private static void add(String key, String value, HashTable<String, String> map) {
        Counter.Default counter = new Counter.Default();
        map.put(key, value, counter);
        System.out.printf("Add. %15s: %s%n", key, counter);
    }

    private static void remove(String key, HashTable<String, String> map) {
        Counter.Default counter = new Counter.Default();
        Optional<String> removed = map.remove(key, counter);
        System.out.printf("Del. %15s: %s, %s%n", key, counter, removed.isEmpty() ? "None" : "Removed");
    }

    private static Stream<String> stream(String resourceName) {
        return Stream.generate(new StringStream(resourceName)).takeWhile(Objects::nonNull);
    }

    @SuppressWarnings("PMD.AvoidStringBufferField")
    private static final class StringStream implements Supplier<String> {
        /**
         * FIXME: Так не стоит делать
         */
        private final StringBuilder builder = new StringBuilder();
        private final BufferedReader reader;
        private int ch;

        private StringStream(String resourceName) {
            this.reader = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(MainMap.class.getResourceAsStream(resourceName))));
        }

        private void skipWhitespace() throws IOException {
            while ((ch = reader.read()) != -1) {
                if (Character.isLetterOrDigit(ch)) {
                    break;
                }
            }
        }

        private boolean nextText() throws IOException {
            skipWhitespace();
            if (eof()) {
                return false;
            }
            builder.setLength(0);
            while (!eof()) {
                builder.append((char) ch);
                ch = reader.read();
                if (!Character.isLetterOrDigit(ch)) {
                    break;
                }
            }
            return !builder.isEmpty();
        }

        private boolean eof() {
            return ch == -1;
        }

        @Override
        public String get() {
            try {
                while (nextText()) {
                    if (builder.length() > 3) {
                        return builder.toString();
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        }
    }
}
