package ru.mifi.practice.voln.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"ru.mifi.practice.voln.service", "ru.mifi.practice.voln.repository.implementation"})
public class ApplicationConfiguration {
}
