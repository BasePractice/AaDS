package ru.mifi.practice.voln.api;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface TelegramHandler {

    void received(TelegramLongPollingBot bot, Update update);

    void send(Long telegramId, String message);
}
