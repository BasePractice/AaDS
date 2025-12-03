package ru.mifi.practice.voln.features;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public interface CountRange {

    Optional<Long> add(long countId, long userId, long delta);

    Optional<Long> userValue(long countId, long userId);

    Optional<Long> countValue(long countId, long userId);

    final class Default implements CountRange {
        private static final Value EMPTY = new Value(0, -1, null);
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
            return ranges.length - 1;
        }

        @Override
        public Optional<Long> userValue(long countId, long userId) {
            return Optional.ofNullable(values.get(new Key(countId, userId))).map(Value::value);
        }

        @Override
        public Optional<Long> add(long countId, long userId, long delta) {
            Count count = this.counts.get(countId);
            if (count == null) {
                return Optional.empty();
            } else if (delta <= 0) {
                return Optional.empty();
            }
            Key key = new Key(countId, userId);
            Value value = values.computeIfAbsent(key, k -> EMPTY.copy());
            long[] ranges = count.ranges;
            int lastIt = indexOf(count.ranges, value.value);
            int newIt = indexOf(ranges, value.value + delta);
            value = value.toBuilder().value(value.value + delta).updatedAt(LocalDateTime.now()).build();
            values.put(key, value);
            if (lastIt < newIt) {
                return Optional.of(count.values[newIt]);
            }
            return Optional.empty();
        }

        @Override
        public Optional<Long> countValue(long countId, long userId) {
            Count count = this.counts.get(countId);
            if (count == null) {
                return Optional.empty();
            }
            Key key = new Key(countId, userId);
            Value value = values.computeIfAbsent(key, k -> EMPTY.copy());
            int index = indexOf(count.ranges, value.value);
            return Optional.of(count.values[index]);
        }

        private record Key(long rangesId, long userId) {
        }
    }


    @Builder(toBuilder = true)
    record Value(long value, long accepted, LocalDateTime updatedAt) {
        private Value copy() {
            return new Value(value, accepted, updatedAt);
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
