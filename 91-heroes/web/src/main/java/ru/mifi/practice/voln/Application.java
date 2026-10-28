package ru.mifi.practice.voln;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SuppressWarnings("PMD.UseUtilityClass")
@SpringBootApplication(scanBasePackages = {
    "ru.mifi.practice.voln.configuration",
    "ru.mifi.practice.voln.controller",
    "ru.mifi.practice.voln.service",
})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
