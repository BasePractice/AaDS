package ru.mifi.practice.vol1;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public interface Iter<E> extends Iterator<E> {
    static <E> Iterable<E> of(List<E> data) {
        return () -> new ListIt<>(data);
    }

    @Override
    boolean hasNext();

    @Override
    E next();

    final class ListIt<E> implements Iter<E> {
        private final List<E> data;
        private int cursor;

        public ListIt(List<E> data, boolean copy) {
            this.data = copy ? List.copyOf(data) : data;
            this.cursor = 0;
        }

        public ListIt(List<E> data) {
            this(data, false);
        }

        @Override
        public boolean hasNext() {
            return cursor < data.size();
        }

        @Override
        public E next() {
            if (hasNext()) {
                int last = cursor;
                ++cursor;
                return data.get(last);
            }
            throw new NoSuchElementException();
        }
    }
}
