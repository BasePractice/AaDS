package ru.mifi.practice.vol4;

import java.util.Date;
import java.util.Random;
import java.util.function.Function;

public abstract class MainHash {
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
     * @param origin оригинальный текст для которого необходимо подобрать хеш
     * @param hash функция хеширования
     */
    private static void collision(String title, String origin, Function<String, Integer> hash) {
        Generator generator = new Generator();
        int originHash = hash.apply(origin);
        int generateHash = 0;
        String generate = "";
        int length = origin.length();
        int it = 0;
        while (originHash != generateHash) {
            if (it > 10000000) {
                it = 0;
                length++;
                System.out.println("Length  : " + length);
            }
            generate = generator.generate(length);
            generateHash = hash.apply(generate);
            ++it;
        }
        System.out.println("Name    : " + title);
        System.out.println("Origin  : " + origin);
        System.out.println("Hash    : " + originHash);
        System.out.println("Generate: " + generate);
        System.out.println("Iterate : " + it);
        System.out.println("Hash    : " + generateHash);
        System.out.println("==================");
    }

    public static void main(String[] args) {
        Hash hash = new Hash.DefaultHash();
        collision("Default", "text", hash::hash);
        hash = new Hash.PolynomialHash();
        collision("Polynomial","text", hash::hash);
        hash = new Hash.PolynomialHashCached();
        collision("PolyCached","text", hash::hash);

        Search search = new Hash.PolynomialHashCached();
        var index = search.search("100000045608889", "456");
        System.out.println(index);
    }

    private static final class Generator {
        private final Random random = new Random(new Date().getTime());

        public String generate(int length) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < length; i++) {
                int symbol = random.nextInt(126 - 33) + 33;
                sb.append((char) symbol);
            }
            return sb.toString();
        }
    }
}
