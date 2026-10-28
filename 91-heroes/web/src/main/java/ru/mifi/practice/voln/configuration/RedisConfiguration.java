package ru.mifi.practice.voln.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.UUID;

/** Конфигурация Redis-шаблона для хранения комнат. */
@Configuration
public class RedisConfiguration {
    @Bean
    public RedisTemplate<UUID, Long> redisRoomTemplate(RedisConnectionFactory factory) {
        RedisTemplate<UUID, Long> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        return template;
    }
}
