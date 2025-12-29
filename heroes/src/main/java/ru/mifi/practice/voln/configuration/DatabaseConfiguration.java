package ru.mifi.practice.voln.configuration;

import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackages = "ru.mifi.practice.voln.domain.entity")
@EnableJpaRepositories(basePackages = "ru.mifi.practice.voln.repository")
public class DatabaseConfiguration {
}
