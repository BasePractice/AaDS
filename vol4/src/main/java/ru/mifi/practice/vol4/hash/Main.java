package ru.mifi.practice.vol4.hash;

import ru.mifi.practice.vol4.Counter;

import java.util.Date;
import java.util.Random;

public abstract class Main {
    private static final String ORIGINAL_TEXT = "text";

    /**
     * Origin  : text
     * Hash    : 3556653
     * Generate: tfYt
     * Generate: v'yU
     * Generate: uFxt
     * Generate: v'xt
     * Generate: uGZU
     * Generate: teyU
     * Generate: tg<6
     * Generate: tf[6
     * Hash    : 3556653
     *
     * @param origin оригинальный текст для которого необходимо подобрать хеш
     * @param hash   функция хеширования
     */
    private static void collision(String title, String origin, Hash hash) {
        Generator generator = new Generator();
        Counter counter = new Counter.Default();
        int originHash = hash.hash(origin, counter);
        final String count = counter.toString();
        int generateHash = 0;
        String generate = "";
        int length = origin.length();
        int it = 0;
        while (originHash != generateHash) {
            counter.reset();
            if (it > 10000000) {
                it = 0;
                length++;
                System.out.println("Length  : " + length);
            }
            generate = generator.generate(length);
            generateHash = hash.hash(generate, counter);
            ++it;
        }
        System.out.println("Name    : " + title);
        System.out.println("Origin  : " + origin);
        System.out.println("Count   : " + count);
        System.out.println("Hash    : " + originHash);
        System.out.println("Generate: " + generate);
        System.out.println("Iterate : " + it);
        System.out.println("Hash    : " + generateHash);
        System.out.println("==================");
    }

    private static void search(String title, String text, String subtext, Search search) {
        var counter = new Counter.Default();
        var index = search.search(text, subtext, counter);
        System.out.println("Name    : " + title);
        System.out.println("Iterate : " + counter);
        if (index.isEmpty()) {
            System.out.println("Index   : ()");
        } else {
            var d = index.get();
            System.out.println("Text    : " + d.text());
            System.out.println("SubText : " + d.subtext());
            System.out.println("Index   : " + d.index());
        }
        System.out.println("==================");
    }

    public static void main(String[] args) {
        Hash hash = new Hash.DefaultHash();
        System.out.println("======Hashing=====");
        collision("Default", ORIGINAL_TEXT, hash);
        hash = new Hash.PolynomialHash();
        collision("Polynomial", ORIGINAL_TEXT, hash);
        hash = new Hash.PolynomialHashCached();
        collision("PolyCached", ORIGINAL_TEXT, hash);

        System.out.println("=====Searching====");
        Search search = new Search.PolynomialSearchCached();
        search("Cached", "100000045608889", "4560", search);
        search = new Search.SimpleSearch();
        search("Simple", "100000045608889", "4560", search);
    }

    private static final class Generator {
        private final Random random = new Random(new Date().getTime());

        public String generate(int length) {
            return generate(length, 33, 126);
        }

        private String generate(int length, int left, int right) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < length; i++) {
                int symbol = random.nextInt(right - left) + left;
                sb.append((char) symbol);
            }
            return sb.toString();
        }
    }
}
