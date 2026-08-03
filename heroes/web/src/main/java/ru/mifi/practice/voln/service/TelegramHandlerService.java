package ru.mifi.practice.voln.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.EventListener;
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
import ru.mifi.practice.voln.api.TelegramHandler;
import ru.mifi.practice.voln.domain.entity.MessageEntity;
import ru.mifi.practice.voln.domain.entity.UserEntity;
import ru.mifi.practice.voln.repository.MessageRepository;
import ru.mifi.practice.voln.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service("TelegramHandler.Default")
public class TelegramHandlerService implements TelegramHandler {
    private static final ObjectMapper HISTORICAL_MAPPER = new ObjectMapper()
        .setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
        .configure(JsonParser.Feature.IGNORE_UNDEFINED, true)
        .configure(JsonGenerator.Feature.IGNORE_UNKNOWN, true);
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final MeterRegistry registry;
    private TelegramLongPollingBot bot;

    private static void sendContactPermissions(TelegramLongPollingBot bot, Long chatId) {
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
    }

    @SuppressWarnings("PMD.CloseResource")
    @EventListener(ApplicationReadyEvent.class)
    public void onReady(ApplicationReadyEvent event) {
        ConfigurableApplicationContext context = event.getApplicationContext();
        bot = context.getBean(TelegramLongPollingBot.class);
    }

    @Override
    public void received(TelegramLongPollingBot bot, Update update) {
        processing(bot, update);
    }

    @Override
    public void send(Long telegramId, String message) {
        if (bot != null) {
            try {
                bot.execute(SendMessage.builder().chatId(telegramId).text(message).build());
            } catch (TelegramApiException e) {
                if (log.isErrorEnabled()) {
                    log.error("", e);
                }
            }
        }
    }

    /**
     * Метод вызывается напрямую из received, то есть через this и мимо прокси Spring, поэтому
     * стоявший здесь @Transactional не создавал транзакции вообще и лишь вводил в заблуждение.
     * Аннотация убрана: каждая запись — одиночный saveAndFlush, которому хватает autocommit.
     * Если появится запись в несколько таблиц, транзакцию надо будет открывать снаружи прокси.
     */
    protected void processing(TelegramLongPollingBot bot, Update update) {
        Message message = update.getMessage();
        if (message == null) {
            if (log.isDebugEnabled()) {
                log.debug("Skip empty message {}", update);
            }
            return;
        }
        Long chatId = message.getChatId();
        User user = message.getFrom();
        if (user == null) {
            if (log.isDebugEnabled()) {
                log.debug("Skip message without author {}", update);
            }
            return;
        }
        Long userId = user.getId();
        if (chatId == null || !chatId.equals(userId)) {
            if (log.isDebugEnabled()) {
                log.debug("Skip not private message {}", update);
            }
            return;
        }
        if (log.isDebugEnabled()) {
            log.debug("Processing message {}", update);
        }
        Contact contact = message.getContact();
        if (contact != null && userId.equals(contact.getUserId())) {
            userRepository.saveAndFlush(UserEntity.builder()
                .telegramId(userId)
                .telegramPhone(contact.getPhoneNumber())
                .telegramLastName(contact.getLastName())
                .telegramFirstName(contact.getFirstName())
                .telegramUsername(user.getUserName())
                .build());
            return;
        }
        Optional<UserEntity> userEntity = userRepository.findByTelegramId(userId);
        if (userEntity.isPresent()) {
            UserEntity ue = userEntity.get();
            if (ue.isTelegramRegistered()) {
                processing(bot, update, ue);
            } else {
                sendContactPermissions(bot, chatId);
            }
        } else {
            sendContactPermissions(bot, chatId);
        }
    }

    @SneakyThrows
    @SuppressWarnings("PMD.UnusedFormalParameter")
    protected void processing(TelegramLongPollingBot bot, Update update, UserEntity entity) {
        String text = HISTORICAL_MAPPER.writeValueAsString(update);
        if (log.isDebugEnabled()) {
            log.debug(text);
        }
        messageRepository.saveAndFlush(MessageEntity.builder().user(entity).message(text).build());
    }
}
