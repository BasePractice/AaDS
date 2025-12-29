package ru.mifi.practice.voln.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import ru.mifi.practice.voln.api.TelegramHandler;
import ru.mifi.practice.voln.entity.UserEntity;
import ru.mifi.practice.voln.repository.UserRepository;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Service("TelegramHandler.Default")
public class TelegramHandlerService implements TelegramHandler {
    private final UserRepository userRepository;
    private final Scheduler telegramScheduler;

    @Override
    public void received(TelegramLongPollingBot bot, Update update) {
        processing(bot, update)
            .subscribeOn(telegramScheduler)
            .publishOn(telegramScheduler)
            .subscribe();
    }

    private Mono<Void> processing(TelegramLongPollingBot bot, Update update) {
        Message message = update.getMessage();
        if (message == null) {
            if (log.isDebugEnabled()) {
                log.debug("Skip message {}", update);
            }
            return Mono.empty();
        }
        Long chatId = message.getChatId();
        User user = message.getFrom();
        Long userId = user.getId();
        if (chatId == null || !chatId.equals(userId)) {
            if (log.isDebugEnabled()) {
                log.debug("Skip message {}", update);
            }
            return Mono.empty();
        }
        if (log.isDebugEnabled()) {
            log.debug("Processing message {}", update);
        }
        Contact contact = message.getContact();
        if (contact != null && userId.equals(contact.getUserId())) {
            return userRepository.registrationTelegramUser(userId, user.getUserName(),
                    contact.getPhoneNumber(), contact.getFirstName(), contact.getLastName())
                .then();
        }
        return userRepository.findUserTelegramId(userId)
            .<UserEntity>handle((entity, sink) -> {
                if (entity.isTelegramRegistered()) {
                    sink.next(entity);
                } else {
                    ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup(List.of(
                        new KeyboardRow(List.of(KeyboardButton.builder().text("Отправить карточку").requestContact(true).build()))
                    ));
                    try {
                        bot.execute(SendMessage.builder().chatId(chatId)
                            .text("Для регистрации отправьте свою персональную карточку").replyMarkup(markup).build());
                    } catch (TelegramApiException e) {
                        if (log.isErrorEnabled()) {
                            log.error("", e);
                        }
                    }
                    sink.complete();
                }
            })
            .map(entity -> processing(bot, update, entity))
            .then();
    }

    @SuppressWarnings("PMD.UnusedFormalParameter")
    private Mono<Void> processing(TelegramLongPollingBot bot, Update update, UserEntity entity) {
        return Mono.empty();
    }
}
