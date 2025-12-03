package ru.mifi.practice.voln.features;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public interface CountRange {

    void increment(long countId, long userId, long delta);

    void acceptValue(long countId, long userId);

    boolean availableValue(long countId, long userId);

    Optional<Long> userValue(long countId, long userId);

    Optional<Long> countValue(long countId, long userId);

    final class Default implements CountRange {
        private static final Value EMPTY = new Value(0, 0, null);
        private final Map<Long, Count> counts = new HashMap<>();
        private final Map<Key, Value> values = new HashMap<>();

        private static int indexOf(long[] ranges, long value) {
            if (value < ranges[0]) {
                return 0;
            }
            for (int i = 1; i < ranges.length; i++) {
                if (value > ranges[i - 1] && value < ranges[i]) {
                    return i;
                }
            }
            return 0;
        }

        public long addCount(String name, long[] ranges, long[] values) {
            int sized = counts.size();
            long id = sized + 1;
            counts.put(id, new Count(id, name, ranges, values));
            return id;
        }

        @Override
        public Optional<Long> userValue(long countId, long userId) {
            return Optional.ofNullable(values.get(new Key(countId, userId))).map(Value::value);
        }

        @Override
        public void increment(long countId, long userId, long delta) {
            Count count = this.counts.get(countId);
            if (count == null) {
                return;
            } else if (delta <= 0) {
                return;
            }
            Key key = new Key(countId, userId);
            Value value = values.computeIfAbsent(key, k -> EMPTY.copy());
            values.put(key, value.toBuilder().value(value.value + delta).updatedAt(LocalDateTime.now()).build());
        }

        @Override
        public Optional<Long> countValue(long countId, long userId) {
            Count count = this.counts.get(countId);
            if (count == null) {
                return Optional.empty();
            }
            Key key = new Key(countId, userId);
            return Optional.ofNullable(values.get(key))
                .map(Value::value)
                .map(v -> count.values[indexOf(count.ranges, v)]);
        }

        @Override
        public void acceptValue(long countId, long userId) {
            Count count = this.counts.get(countId);
            if (count == null) {
                return;
            }
            Key key = new Key(countId, userId);
            Value value = values.computeIfAbsent(key, k -> EMPTY.copy());
            int currentIt = indexOf(count.ranges, value.value);
            if (currentIt > value.acceptedIndex()) {
                values.put(key, value.toBuilder().acceptedIndex(value.acceptedIndex() + 1).build());
            }
        }

        @Override
        public boolean availableValue(long countId, long userId) {
            Count count = this.counts.get(countId);
            if (count == null) {
                return false;
            }
            Key key = new Key(countId, userId);
            Value value = values.computeIfAbsent(key, k -> EMPTY.copy());
            int currentIt = indexOf(count.ranges, value.value);
            return currentIt > value.acceptedIndex();
        }

        private record Key(long rangesId, long userId) {
        }
    }


    @Builder(toBuilder = true)
    record Value(long value, long acceptedIndex, LocalDateTime updatedAt) {
        private Value copy() {
            return new Value(value, acceptedIndex, updatedAt);
        }
    }

    record Count(long id, String name, long[] ranges, long[] values) {
        public Count {
            if (ranges.length != values.length) {
                throw new IllegalArgumentException("Размер диапазонов и размер значений должен совпадать");
            }
        }
    }
}
