package ru.mifi.practice.vol3;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public abstract class NumberGenerator {
    static final int MAX_GENERATED_ELEMENT_VALUE = 100;

    @SuppressWarnings("PMD.UnusedPrivateMethod")
    static List<Integer> generateSlice(int length) {
        Random r = new Random(new Date().getTime());
        List<Integer> slice = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            slice.add(r.nextInt(MAX_GENERATED_ELEMENT_VALUE + 1));
        }
        return slice;
    }
}
