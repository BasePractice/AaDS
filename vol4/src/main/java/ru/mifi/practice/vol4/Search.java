package ru.mifi.practice.vol4;

import java.util.Optional;

public interface Search {
    Optional<Index> search(String text, String substring);

    record Index(String text, String subtext, int index) {

    }
}
