package ru.mifi.practice.vol8.otp;

import ru.mifi.practice.vol8.Machine;
import ru.mifi.practice.vol8.Machine.Context;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("PMD")
public enum OTPKey implements Machine.Key {
    BLOCKING_TIME_EXPIRED("blocking_time_expired", LocalDateTime.class),
    BLOCKING_TIME_EXPIRED_SECOND("blocking_time_expired_second", Integer.class),
    BLOCKING_REASON("blocking_reason", String.class),

    WAITING_TIME_EXPIRED("waiting_time_expired", LocalDateTime.class),
    WAITING_TIME_EXPIRED_SECOND("waiting_time_expired_second", Integer.class),
    WAITING_REASON("waiting_reason", String.class),

    UNSENT_TIME_EXPIRED("unsent_timeout", LocalDateTime.class),
    UNSENT_TIME_EXPIRED_SECOND("unsent_time_expired_second", Integer.class),
    UNSENT_REASON("unsent_reason", String.class),

    RESENT("resent", Boolean.class),
    CODE("code", String.class),
    CODE_TIME_EXPIRED("code_time_expired", LocalDateTime.class),
    CODE_TIME_EXPIRED_SECOND("code_time_expired_second", Integer.class),

    SEND_CODE_ATTEMPTS("send_code_attempts", Integer.class),
    MAX_SEND_CODE_ATTEMPTS("max_send_code_attempts", Integer.class),
    VERIFICATION_CODE_ATTEMPTS("send_code_attempts", Integer.class),
    MAX_VERIFICATION_CODE_ATTEMPTS("max_send_code_attempts", Integer.class),
    ;
    private final String key;
    private final Class<?> valueClass;
    private final Function<Context, Optional<?>> value;

    OTPKey(String key, Class<?> valueClass, Function<Context, Optional<?>> value) {
        this.key = key;
        this.valueClass = valueClass;
        this.value = value;
    }

    OTPKey(String key, Class<?> valueClass) {
        this(key, valueClass, context -> context.get(() -> key, valueClass));
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(Context context) {
        return value.apply(context).map(v -> (T) v);
    }

    public <T> void set(Context context, T value) {
        context.set(this, value);
    }

    public void setTimeNow(Context context) {
        context.set(this, LocalDateTime.now());
    }

    public void setTimeSecond(Context context, OTPKey seconds) {
        context.set(this, LocalDateTime.now().plusSeconds(seconds.get(context).map(v -> ((Number) v).longValue()).orElse(3200L)));
    }

    public boolean isExpired(Context context) {
        return LocalDateTime.now().isAfter(get(context).map(v -> (LocalDateTime) v).orElse(LocalDateTime.now()));
    }

    public boolean is(Context context) {
        return get(context).map(v -> (Boolean) v).orElse(false);
    }

    public void increment(Context context) {
        set(context, get(context).map(v -> (Integer) v).orElse(0) + 1);
    }

    public boolean isOverflow(Context context, OTPKey overflow) {
        Integer nOverflow = overflow.get(context).map(v -> (Integer) v).orElse(0);
        return get(context).map(v -> (Integer) v).orElse(0) > nOverflow;
    }

    public void clear(Context context) {
        context.clear(this);
    }

    @Override
    public String key() {
        return key;
    }
}
