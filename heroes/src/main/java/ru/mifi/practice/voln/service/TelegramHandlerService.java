package ru.mifi.practice.voln.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.mifi.practice.voln.api.TelegramHandler;

import java.util.List;

@Slf4j
@Service("TelegramHandler.Default")
public class TelegramHandlerService implements TelegramHandler {

    @Override
    public void received(TelegramLongPollingBot bot, Update update) {
        if (log.isDebugEnabled()) {
            log.debug("{}", update);
        }
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup(List.of(
            new KeyboardRow(List.of(KeyboardButton.builder().text("Отправить карточку").requestContact(true).build()))
        ));
        try {
            bot.execute(SendMessage.builder().chatId(update.getMessage().getChatId())
                .text("Для регистрации отправьте свою персональную карточку").replyMarkup(markup).build());
        } catch (TelegramApiException e) {
            if (log.isErrorEnabled()) {
                log.error("", e);
            }
        }
    }
}
