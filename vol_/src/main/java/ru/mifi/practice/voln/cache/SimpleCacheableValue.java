package ru.mifi.practice.voln.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class SimpleCacheableValue implements CacheableValue {
    private static final int MINIMUM_L1_DELTA_MS = 100;
    private static final String UPDATE_DATE = "update_date";
    private static final String VALUE = "value";
    private static final ZoneId ZONE_ID = ZoneId.of("UTC");
    private static final String VALUE_KEY_PREFIX = "val:";
    private static final String VALUE_UPDATE_TOPIC = "value_update";
    private final Function<Long, Long> fetchValue;
    private final long timeActualMs;
    private final Cache<Long, Value> cacheValues;
    private final AtomicInteger cacheHits = new AtomicInteger(0);
    private final AtomicInteger cacheLastHits = new AtomicInteger(0);
    private final Notifiable notifiable;
    private final CacheableMap cacheableMap;

    public SimpleCacheableValue(CacheableMap cacheableMap,
                                Notifiable notifiable,
                                Function<Long, Long> fetchValue,
                                long timeActualMs,
                                long maximumCacheSize) {
        this.cacheableMap = cacheableMap;
        this.notifiable = notifiable;
        this.fetchValue = fetchValue;
        this.timeActualMs = timeActualMs;
        if (timeActualMs + MINIMUM_L1_DELTA_MS <= 0) {
            this.cacheValues = null;
        } else {
            this.cacheValues = CacheBuilder.newBuilder()
                .expireAfterWrite(timeActualMs + MINIMUM_L1_DELTA_MS, TimeUnit.MILLISECONDS)
                .recordStats()
                .maximumSize(maximumCacheSize)
                .build();
        }
        notifiable.registerNotify(VALUE_UPDATE_TOPIC, this::updateValue);
    }

    private static String key(long userId) {
        return VALUE_KEY_PREFIX + userId;
    }

    private static long timestampCurrent() {
        ZonedDateTime zdt = LocalDateTime.now().atZone(ZONE_ID);
        return zdt.toInstant().toEpochMilli();
    }

    private static LocalDateTime timestampLdt(long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        return LocalDateTime.ofInstant(instant, ZONE_ID);
    }

    public long lastCacheHits() {
        return cacheLastHits.longValue();
    }

    private void updateValue(long key) {
        Long value = fetchValue.apply(key);
        long millis = timestampCurrent();
        updateCache(key, new SimpleValue(value, true));
        cacheableMap.hSet(key(key), Map.of(VALUE, value, UPDATE_DATE, millis + timeActualMs));
    }

    private void updateCache(long key, CacheableValue.Value value) {
        cacheLastHits.set(cacheHits.get());
        cacheHits.set(0);
        if (cacheValues != null) {
            cacheValues.put(key, value);
        }
    }

    private void sendUpdate(long key) {
        notifiable.notify(VALUE_UPDATE_TOPIC, key);
    }

    @Override
    public Optional<Value> getValue(long key) {
        Value value = getCacheValue(key);
        if (value != null) {
            return Optional.of(value);
        }
        String keyText = key(key);
        Map<String, Long> map = cacheableMap.hGet(keyText);
        if (map == null || map.isEmpty()) {
            sendUpdate(key);
            return Optional.empty();
        }
        Long updateDate = map.get(UPDATE_DATE);
        long countValue = map.getOrDefault(VALUE, 0L);
        if (updateDate == null) {
            sendUpdate(key);
            return Optional.of(new SimpleValue(countValue, false));
        } else {
            LocalDateTime localDateTime = timestampLdt(updateDate).plus(timeActualMs, ChronoUnit.MILLIS);
            if (localDateTime.isBefore(LocalDateTime.now())) {
                sendUpdate(key);
                return Optional.of(new SimpleValue(countValue, false));
            }
            SimpleValue actual = new SimpleValue(countValue, true);
            updateCache(key, actual);
            return Optional.of(actual);
        }
    }

    private Value getCacheValue(long key) {
        if (cacheValues == null) {
            return null;
        }
        Value value = cacheValues.getIfPresent(key);
        if (value != null) {
            cacheHits.incrementAndGet();
        }
        return value;
    }

    @Override
    public void close() throws Exception {
        notifiable.close();
        cacheableMap.close();
    }

    private record SimpleValue(long value, boolean isActual) implements CacheableValue.Value {
    }
}
