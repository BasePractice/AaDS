package ru.mifi.practice.voln.codes;

/**
 * Исключение при невозможности восстановить исходные данные из полученных символов.
 */
public final class DecodingFailedException extends RuntimeException {
    /**
     * Создает исключение с сообщением.
     * @param message текст сообщения
     */
    public DecodingFailedException(String message) {
        super(message);
    }
}
