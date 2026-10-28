package ru.mifi.practice.voln.cache.memory;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import ru.mifi.practice.voln.cache.CacheableMap;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

/** Проверка кэша в памяти — первого уровня двухуровневой схемы. */
@DisplayName("Кэш в памяти")
final class CacheableMapMemoryTest {

    @DisplayName("Положенное значение читается обратно")
    @Test
    @Timeout(5)
    void readsBackWhatWasPut() throws Exception {
        try (CacheableMap cache = new CacheableMapMemory(new SimpleMeterRegistry())) {
            cache.hSet("счётчик", Map.of("шаги", 7L));
            assertThat("the cache dont read back what was put", cache.hGet("счётчик"), is(Map.of("шаги", 7L)));
        }
    }

    @DisplayName("Незнакомый ключ ничего не даёт")
    @Test
    @Timeout(5)
    void givesNothingForAnUnknownKey() throws Exception {
        try (CacheableMap cache = new CacheableMapMemory(new SimpleMeterRegistry())) {
            assertThat("an unknown key gives a value", cache.hGet("нет такого"), is(nullValue()));
        }
    }

    @DisplayName("Повторная запись заменяет прежнее значение")
    @Test
    @Timeout(5)
    void replacesTheEarlierValue() throws Exception {
        try (CacheableMap cache = new CacheableMapMemory(new SimpleMeterRegistry())) {
            cache.hSet("счётчик", Map.of("шаги", 7L));
            cache.hSet("счётчик", Map.of("шаги", 9L));
            assertThat("a second write dont replace the earlier value",
                cache.hGet("счётчик"), is(Map.of("шаги", 9L)));
        }
    }

    @DisplayName("Разные ключи не мешают друг другу")
    @Test
    @Timeout(5)
    void keepsDifferentKeysApart() throws Exception {
        try (CacheableMap cache = new CacheableMapMemory(new SimpleMeterRegistry())) {
            cache.hSet("первый", Map.of("шаги", 1L));
            cache.hSet("второй", Map.of("шаги", 2L));
            assertThat("different keys interfere with each other",
                cache.hGet("первый"), is(Map.of("шаги", 1L)));
        }
    }
}
