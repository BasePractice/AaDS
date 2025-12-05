package ru.mifi.practice.voln.cache.redis;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.RedisPubSubAdapter;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.cache.GuavaCacheMetrics;
import lombok.SneakyThrows;
import ru.mifi.practice.voln.cache.Notifiable;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public final class NotifiableRedis implements Notifiable {
    private final AtomicInteger inOrderSize = new AtomicInteger(0);
    private final StatefulRedisPubSubConnection<String, String> pubSub;
    private final Cache<Long, String> notified;

    public NotifiableRedis(RedisClient client, MeterRegistry registry) {
        this.pubSub = client.connectPubSub();
        this.notified = CacheBuilder.newBuilder()
            .expireAfterWrite(1000, TimeUnit.MILLISECONDS)
            .recordStats()
            .maximumSize(1000)
            .build();
        GuavaCacheMetrics.monitor(registry, notified, "CacheNotifyRedis");
        Gauge.builder("CacheNotifyRedis", inOrderSize, AtomicInteger::intValue).tag("target", "in-order-size").register(registry);
    }

    @Override
    public void registerNotify(String updateChannel, Consumer<Long> callback) {
        pubSub.addListener(new RedisPubSubAdapter<>() {
            @Override
            public void message(String channel, String message) {
                if (updateChannel.equals(channel)) {
                    inOrderSize.decrementAndGet();
                    Long key = Long.valueOf(message);
                    callback.accept(key);
                    notified.invalidate(key);
                }
            }
        });
        pubSub.async().subscribe(updateChannel);
    }

    @SneakyThrows
    @Override
    public void notify(String channel, long key) {
        if (!pubSub.isOpen()) {
            throw new IllegalStateException("Redis is not open");
        }
        String present = notified.getIfPresent(key);
        if (present != null) {
            //FIXME: Повторное оповещение
            return;
        }
        pubSub.async().publish(channel, String.valueOf(key)).await(10, TimeUnit.SECONDS);
        inOrderSize.incrementAndGet();
        notified.put(key, channel);
    }

    @Override
    public void close() throws Exception {
        pubSub.close();
    }
}
