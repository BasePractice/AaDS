package ru.mifi.practice.voln.configuration;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.internal.TimedScheduledExecutorService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/** Конфигурация планировщика задач с метриками. */
@Configuration
@EnableScheduling
public class SchedulerConfiguration {

    @Bean
    public ScheduledExecutorService schedulingService(MeterRegistry registry) {
        return new TimedScheduledExecutorService(
            registry,
            Executors.newScheduledThreadPool(10),
            "scheduling",
            null,
            null
        );
    }
}
