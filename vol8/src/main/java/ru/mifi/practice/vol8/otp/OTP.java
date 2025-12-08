package ru.mifi.practice.vol8.otp;

import ru.mifi.practice.vol8.Machine.Context;
import ru.mifi.practice.vol8.Machine.Handler;
import ru.mifi.practice.vol8.Machine.State;

import static ru.mifi.practice.vol8.otp.OTPKey.BLOCKING_REASON;
import static ru.mifi.practice.vol8.otp.OTPKey.BLOCKING_TIME_EXPIRED;
import static ru.mifi.practice.vol8.otp.OTPKey.BLOCKING_TIME_EXPIRED_SECOND;
import static ru.mifi.practice.vol8.otp.OTPKey.CODE;
import static ru.mifi.practice.vol8.otp.OTPKey.CODE_TIME_EXPIRED;
import static ru.mifi.practice.vol8.otp.OTPKey.CODE_TIME_EXPIRED_SECOND;
import static ru.mifi.practice.vol8.otp.OTPKey.MAX_SEND_CODE_ATTEMPTS;
import static ru.mifi.practice.vol8.otp.OTPKey.MAX_VERIFICATION_CODE_ATTEMPTS;
import static ru.mifi.practice.vol8.otp.OTPKey.RESENT;
import static ru.mifi.practice.vol8.otp.OTPKey.SEND_CODE_ATTEMPTS;
import static ru.mifi.practice.vol8.otp.OTPKey.UNSENT_REASON;
import static ru.mifi.practice.vol8.otp.OTPKey.UNSENT_TIME_EXPIRED;
import static ru.mifi.practice.vol8.otp.OTPKey.VERIFICATION_CODE_ATTEMPTS;
import static ru.mifi.practice.vol8.otp.OTPKey.WAITING_REASON;
import static ru.mifi.practice.vol8.otp.OTPKey.WAITING_TIME_EXPIRED;
import static ru.mifi.practice.vol8.otp.OTPKey.WAITING_TIME_EXPIRED_SECOND;

public enum OTP implements State {
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
    public State next(Context context, Handler handler) {
        switch (this) {
            case INITIATE -> {
                return CHECK_SEND;
            }
            case CHECK_SEND -> {
                if (SEND_CODE_ATTEMPTS.isOverflow(context, MAX_SEND_CODE_ATTEMPTS)) {
                    BLOCKING_TIME_EXPIRED.setTimeSecond(context, BLOCKING_TIME_EXPIRED_SECOND);
                    BLOCKING_REASON.set(context, "Превышено количество отосланных");
                    return BLOCKING;
                } else if (VERIFICATION_CODE_ATTEMPTS.isOverflow(context, MAX_VERIFICATION_CODE_ATTEMPTS)) {
                    WAITING_TIME_EXPIRED.setTimeSecond(context, WAITING_TIME_EXPIRED_SECOND);
                    WAITING_REASON.set(context, "Превышено количество не ");
                    return WAITING;
                }
                return SEND;
            }
            case WAITING -> {
                if (WAITING_TIME_EXPIRED.isExpired(context)) {
                    context.clear(WAITING_REASON, WAITING_TIME_EXPIRED);
                    return CHECK_SEND;
                }
                return WAITING;
            }
            case BLOCKING -> {
                if (BLOCKING_TIME_EXPIRED.isExpired(context)) {
                    context.clear(BLOCKING_REASON, BLOCKING_TIME_EXPIRED);
                    return CHECK_SEND;
                }
                return BLOCKING;
            }
            case SEND -> {
                boolean sent = handler.sendNextCode(context);
                if (sent) {
                    CODE_TIME_EXPIRED.setTimeSecond(context, CODE_TIME_EXPIRED_SECOND);
                    SEND_CODE_ATTEMPTS.increment(context);
                    return WAS_SENT;
                }
                UNSENT_TIME_EXPIRED.setTimeNow(context);
                return UN_SENT;
            }
            case WAS_SENT -> {
                if (RESENT.is(context)) {
                    RESENT.clear(context);
                    return CHECK_SEND;
                }
                if (handler.isCodeEquals(context, CODE)) {
                    if (CODE_TIME_EXPIRED.isExpired(context)) {
                        return FAILED_EXPIRED_CODE;
                    }
                    return VERIFIED;
                }
                return FAILED_VERIFIED;
            }
            case UN_SENT -> {
                if (UNSENT_TIME_EXPIRED.isExpired(context)) {
                    context.clear(UNSENT_REASON, UNSENT_TIME_EXPIRED);
                    return CHECK_SEND;
                }
                return UN_SENT;
            }
            case FAILED_EXPIRED_CODE -> {
                VERIFICATION_CODE_ATTEMPTS.increment(context);
                return CHECK_SEND;
            }
            case FAILED_VERIFIED -> {
                if (RESENT.is(context)) {
                    RESENT.clear(context);
                    VERIFICATION_CODE_ATTEMPTS.clear(context);
                    return CHECK_SEND;
                } else if (VERIFICATION_CODE_ATTEMPTS.isOverflow(context, MAX_VERIFICATION_CODE_ATTEMPTS)) {
                    return CHECK_SEND;
                }
                if (handler.isCodeEquals(context, CODE)) {
                    if (CODE_TIME_EXPIRED.isExpired(context)) {
                        return FAILED_EXPIRED_CODE;
                    }
                    return VERIFIED;
                }
                VERIFICATION_CODE_ATTEMPTS.increment(context);
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
        //Nothing
    }

    @Override
    public String toString() {
        return title();
    }
}
