package ru.mifi.practice.vol8;

import ru.mifi.practice.vol8.Machine.Context;
import ru.mifi.practice.vol8.Machine.Handler;

public enum OTP implements Machine.State {
    INITIATE("Инициализация", true),
    CHECK_SEND("Проверка", false),
    WAITING("Ожидание", true),
    BLOCKING("Блокировка", true),
    SEND("Отсылка", false),
    WAS_SENT("Отослано", true),
    UN_SENT("Ошибка отсылки", true),
    FAILED_EXPIRED_CODE("Код просрочен", true),
    FAILED_VERIFIED("Код не верный", true),
    VERIFIED("Код правильный", true);

    private final String title;
    private final boolean isTerminated;

    OTP(String title, boolean isTerminated) {
        this.title = title;
        this.isTerminated = isTerminated;
    }

    @Override
    public Machine.State next(Context context, Handler handler) {
        switch (this) {
            case INITIATE -> {
                return CHECK_SEND;
            }
            case CHECK_SEND -> {
                if (OTPKey.SEND_CODE_ATTEMPTS.isOverflow(context, OTPKey.MAX_SEND_CODE_ATTEMPTS)) {
                    OTPKey.BLOCKING_TIME_EXPIRED.setTimeSecond(context, OTPKey.BLOCKING_TIME_EXPIRED_SECOND);
                    OTPKey.BLOCKING_REASON.set(context, "Превышено количество отосланных");
                    return BLOCKING;
                } else if (OTPKey.VERIFICATION_CODE_ATTEMPTS.isOverflow(context, OTPKey.MAX_VERIFICATION_CODE_ATTEMPTS)) {
                    OTPKey.WAITING_TIME_EXPIRED.setTimeSecond(context, OTPKey.WAITING_TIME_EXPIRED_SECOND);
                    OTPKey.WAITING_REASON.set(context, "Превышено количество не ");
                    return WAITING;
                }
                return SEND;
            }
            case WAITING -> {
                if (OTPKey.WAITING_TIME_EXPIRED.isExpired(context)) {
                    context.clear(OTPKey.WAITING_REASON, OTPKey.WAITING_TIME_EXPIRED);
                    return CHECK_SEND;
                }
                return WAITING;
            }
            case BLOCKING -> {
                if (OTPKey.BLOCKING_TIME_EXPIRED.isExpired(context)) {
                    context.clear(OTPKey.BLOCKING_REASON, OTPKey.BLOCKING_TIME_EXPIRED);
                    return CHECK_SEND;
                }
                return BLOCKING;
            }
            case SEND -> {
                boolean sent = handler.sendNextCode(context);
                if (sent) {
                    OTPKey.CODE_TIME_EXPIRED.setTimeSecond(context, OTPKey.CODE_TIME_EXPIRED_SECOND);
                    OTPKey.SEND_CODE_ATTEMPTS.increment(context);
                    return WAS_SENT;
                }
                OTPKey.UNSENT_TIME_EXPIRED.setTimeNow(context);
                return UN_SENT;
            }
            case WAS_SENT -> {
                if (OTPKey.RESENT.is(context)) {
                    return CHECK_SEND;
                }
                if (handler.isCodeEquals(context, OTPKey.CODE)) {
                    if (OTPKey.CODE_TIME_EXPIRED.isExpired(context)) {
                        return FAILED_EXPIRED_CODE;
                    }
                    return VERIFIED;
                }
                return FAILED_VERIFIED;
            }
            case UN_SENT -> {
                if (OTPKey.UNSENT_TIME_EXPIRED.isExpired(context)) {
                    context.clear(OTPKey.UNSENT_REASON, OTPKey.UNSENT_TIME_EXPIRED);
                    return CHECK_SEND;
                }
                return UN_SENT;
            }
            case FAILED_EXPIRED_CODE -> {
                OTPKey.VERIFICATION_CODE_ATTEMPTS.increment(context);
                return CHECK_SEND;
            }
            case FAILED_VERIFIED -> {
                if (OTPKey.RESENT.is(context)) {
                    OTPKey.VERIFICATION_CODE_ATTEMPTS.clear(context);
                    return CHECK_SEND;
                } else if (OTPKey.VERIFICATION_CODE_ATTEMPTS.isOverflow(context, OTPKey.MAX_VERIFICATION_CODE_ATTEMPTS)) {
                    return CHECK_SEND;
                }
                if (handler.isCodeEquals(context, OTPKey.CODE)) {
                    if (OTPKey.CODE_TIME_EXPIRED.isExpired(context)) {
                        return FAILED_EXPIRED_CODE;
                    }
                    return VERIFIED;
                }
                OTPKey.VERIFICATION_CODE_ATTEMPTS.increment(context);
                return FAILED_VERIFIED;
            }
            case VERIFIED -> {
                return VERIFIED;
            }
            default -> throw new IllegalStateException("Unexpected value: " + this);
        }
    }

    @Override
    public boolean isTerminated() {
        return isTerminated;
    }

    @Override
    public boolean isSuccessful() {
        return this == VERIFIED;
    }

    @Override
    public String title() {
        return title;
    }

    @Override
    public void handle(Handler handler) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String toString() {
        return title();
    }
}
