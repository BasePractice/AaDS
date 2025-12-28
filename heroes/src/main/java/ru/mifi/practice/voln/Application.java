package ru.mifi.practice.voln;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import ru.mifi.practice.voln.configuration.ApplicationConfiguration;
import ru.mifi.practice.voln.configuration.SessionConfiguration;
import ru.mifi.practice.voln.configuration.TelegramConfiguration;

@SuppressWarnings("PMD.UseUtilityClass")
@SpringBootApplication
@Import({ApplicationConfiguration.class, SessionConfiguration.class, TelegramConfiguration.class})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
