package ru.mifi.practice.voln.cache.redis;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import ru.mifi.practice.voln.cache.Count;

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

public class CountRedis implements Count {
    private static final String UPDATE_DATE = "update_date";
    private static final String VALUE = "value";
    private static final ZoneId ZONE_ID = ZoneId.of("UTC");
    private static final String COUNT_KEY_PREFIX = "cnt:";
    private static final String COUNT_TOPIC = "count_update";
    private final Function<Long, Long> fetchValue;
    private final long timeActualDeltaMs;
    private final Cache<Long, Count.Value> cacheCount;
    private final AtomicInteger cacheHit = new AtomicInteger(0);
    private final AtomicInteger cacheLastHits = new AtomicInteger(0);
    private final LocalRedis localRedis;

    public CountRedis(String url,
                      Function<Long, Long> fetchValue,
                      long timeActualDeltaMs,
                      long maximumLocalCacheSize) {
        this.localRedis = new LocalRedis(url);
        this.fetchValue = fetchValue;
        this.timeActualDeltaMs = timeActualDeltaMs;
        if (timeActualDeltaMs - 1 <= 0) {
            this.cacheCount = null;
        } else {
            this.cacheCount = CacheBuilder.newBuilder()
                .expireAfterWrite(timeActualDeltaMs - 1, TimeUnit.MILLISECONDS)
                .recordStats()
                .maximumSize(maximumLocalCacheSize)
                .build();
        }
        localRedis.registerUpdate(COUNT_TOPIC, this::updateCount);
    }

    private static String key(long userId) {
        return COUNT_KEY_PREFIX + userId;
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

    private void updateCount(long userId) {
        Long value = fetchValue.apply(userId);
        long millis = timestampCurrent();
        updateCache(userId, new CountActual(value, true));
        localRedis.hSet(key(userId), Map.of(VALUE, value, UPDATE_DATE, millis + timeActualDeltaMs));
    }

    private void updateCache(long userId, Count.Value value) {
        cacheLastHits.set(cacheHit.get());
        cacheHit.set(0);
        if (cacheCount != null) {
            cacheCount.put(userId, value);
        }
    }

    private void putUpdate(long userId) {
        localRedis.send(COUNT_TOPIC, userId);
    }

    @Override
    public Optional<Value> getValue(long userId) {
        Value value = getCacheValue(userId);
        if (value != null) {
            return Optional.of(value);
        }
        String key = key(userId);
        Map<String, Long> map = localRedis.hGet(key);
        if (map == null || map.isEmpty()) {
            putUpdate(userId);
            return Optional.empty();
        }
        Optional<Long> v = Optional.ofNullable(map.get(VALUE));
        Optional<Long> updateDate = Optional.ofNullable(map.get(UPDATE_DATE));
        long countValue = v.orElse(0L);
        if (updateDate.isEmpty()) {
            putUpdate(userId);
            return Optional.of(new CountActual(countValue, false));
        } else {
            long timestamp = updateDate.get();
            LocalDateTime localDateTime = timestampLdt(timestamp).plus(timeActualDeltaMs, ChronoUnit.MILLIS);
            if (localDateTime.isBefore(LocalDateTime.now())) {
                putUpdate(userId);
                return Optional.of(new CountActual(countValue, false));
            }
            CountActual actual = new CountActual(countValue, true);
            updateCache(userId, actual);
            return Optional.of(actual);
        }
    }

    private Value getCacheValue(long userId) {
        if (cacheCount == null) {
            return null;
        }
        Value value = cacheCount.getIfPresent(userId);
        if (value != null) {
            cacheHit.incrementAndGet();
        }
        return value;
    }

    @Override
    public void close() {
        localRedis.close();
    }

    private record CountActual(long value, boolean isActual) implements Count.Value {
    }
}
