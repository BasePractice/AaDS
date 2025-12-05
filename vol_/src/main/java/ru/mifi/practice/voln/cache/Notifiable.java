package ru.mifi.practice.voln.cache;

import java.util.function.Consumer;

public interface Notifiable extends AutoCloseable {
    void registerNotify(String updateChannel, Consumer<Long> callback);

    void notify(String channel, long key);
}
