package ru.mifi.practice.voln.cache.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.pubsub.RedisPubSubAdapter;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import lombok.SneakyThrows;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@SuppressWarnings("PMD.AvoidUsingVolatile")
final class LocalRedis implements AutoCloseable {
    private final RedisClient client;
    private final StatefulRedisConnection<String, Long> connection;
    private volatile StatefulRedisPubSubConnection<String, String> pubSub;

    LocalRedis(String url) {
        client = RedisClient.create(url);
        connection = client.connect(LongCodec.INSTANCE);
    }

    void registerUpdate(String balanceTopic, Consumer<Long> update) {
        validatePubSub();
        pubSub.addListener(new RedisPubSubAdapter<>() {
            @Override
            public void message(String channel, String message) {
                if (balanceTopic.equals(channel)) {
                    Long userId = Long.valueOf(message);
                    update.accept(userId);
                }
            }
        });
        pubSub.async().subscribe(balanceTopic);
    }

    private void validatePubSub() {
        if (pubSub == null) {
            synchronized (this) {
                if (pubSub == null) {
                    pubSub = client.connectPubSub();
                    pubSub.setAutoFlushCommands(true);
                }
            }
        }
    }

    @SneakyThrows
    void send(String channel, long userId) {
        validatePubSub();
        if (!pubSub.isOpen()) {
            throw new IllegalStateException("Redis is not open");
        }
        pubSub.async().publish(channel, String.valueOf(userId)).await(10, TimeUnit.SECONDS);
    }

    void hSet(String key, Map<String, Long> values) {
        connection.sync().hset(key, values);
    }

    Map<String, Long> hGet(String key) {
        return connection.sync().hgetall(key);
    }

    @Override
    public void close() {
        connection.close();
        client.close();
    }

    protected enum LongCodec implements RedisCodec<String, Long> {
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
