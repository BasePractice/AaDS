package ru.mifi.practice.voln.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import org.telegram.telegrambots.starter.TelegramBotStarterConfiguration;
import ru.mifi.practice.voln.api.TelegramHandler;

@Configuration
@Import(TelegramBotStarterConfiguration.class)
public class TelegramConfiguration {

    @Bean
    public LongPollingBot heroesBot(Environment environment, TelegramHandler handler) {
        return new TelegramLongPollingBot(environment.getRequiredProperty("app.telegram.bot.token")) {
            @Override
            public void onUpdateReceived(Update update) {
                handler.received(this, update);
            }

            @Override
            public String getBotUsername() {
                return environment.getProperty("app.telegram.bot.username", "");
            }
        };
    }
}
