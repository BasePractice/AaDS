package ru.mifi.practice.voln.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.MapSessionRepository;
import org.springframework.session.SessionRepository;

import java.util.concurrent.ConcurrentHashMap;

/** Конфигурация хранилища сессий в памяти. */
@Configuration
public class SessionConfiguration {
    @Bean
    public SessionRepository<?> reactiveSessionRepository() {
        return new MapSessionRepository(new ConcurrentHashMap<>());
    }
}
