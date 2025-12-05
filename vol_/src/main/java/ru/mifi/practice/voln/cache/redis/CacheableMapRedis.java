package ru.mifi.practice.voln.cache.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import ru.mifi.practice.voln.cache.CacheableMap;

import java.nio.ByteBuffer;
import java.util.Map;

public final class CacheableMapRedis implements CacheableMap {
    private final RedisClient client;
    private final StatefulRedisConnection<String, Long> connection;

    public CacheableMapRedis(String url) {
        client = RedisClient.create(url);
        connection = client.connect(LongCodec.INSTANCE);
    }

    @Override
    public void hSet(String key, Map<String, Long> values) {
        connection.sync().hset(key, values);
    }

    @Override
    public Map<String, Long> hGet(String key) {
        return connection.sync().hgetall(key);
    }

    @Override
    public void close() throws Exception {
        connection.close();
        client.close();
    }

    private enum LongCodec implements RedisCodec<String, Long> {
        INSTANCE;

        @Override
        public String decodeKey(ByteBuffer bytes) {
            return StringCodec.ASCII.decodeKey(bytes);
        }

        @Override
        public Long decodeValue(ByteBuffer bytes) {
            return Long.valueOf(decodeKey(bytes));
        }

        @Override
        public ByteBuffer encodeKey(String key) {
            return StringCodec.ASCII.encodeKey(key);
        }

        @Override
        public ByteBuffer encodeValue(Long value) {
            return encodeKey(value.toString());
        }
    }
}
