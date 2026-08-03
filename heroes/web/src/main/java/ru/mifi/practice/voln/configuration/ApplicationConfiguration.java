package ru.mifi.practice.voln.configuration;

import io.micrometer.core.instrument.binder.logging.LogbackMetrics;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "ru.mifi.practice.voln.service")
public class ApplicationConfiguration {
    @Bean
    public LogbackMetrics logbackMetrics() {
        return new LogbackMetrics();
    }
}
