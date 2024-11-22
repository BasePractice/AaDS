package ru.mifi.practice.vol4;

public interface Hash {
    int hash(String text);

    final class DefaultHash implements Hash {

        @Override
        public int hash(String text) {
            int hash = 0;
            for (int i = 0; i < text.length(); i++) {
                hash = hash * 31 + text.charAt(i);
            }
            return hash;
        }
    }
}
