package ru.mifi.practice.vol8;

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
    public Machine.State next(Machine.Context context, Machine.Handler handler) {
        switch (this) {
            case INITIATE -> {
                return CHECK_SEND;
            }
            case CHECK_SEND -> {
                if (context.isSentCodeAttemptsOverflow()) {
                    context.setBlockingTimeout();
                    context.setBlockingReason("Превышено количество отосланных");
                    return BLOCKING;
                } else if (context.isVerificationCodeAttemptsOverflow()) {
                    context.setWaitingTimeout();
                    context.setWaitingReason("Превышено количество не ");
                    return WAITING;
                }
                return SEND;
            }
            case WAITING -> {
                if (context.isWaitingTimeoutExpired()) {
                    context.clear();
                    return CHECK_SEND;
                }
                return WAITING;
            }
            case BLOCKING -> {
                if (context.isBlockingTimeoutExpired()) {
                    context.clear();
                    return CHECK_SEND;
                }
                return BLOCKING;
            }
            case SEND -> {
                boolean sent = handler.sendNextCode(context);
                if (sent) {
                    context.incrementAttemptsSentCode();
                    return WAS_SENT;
                }
                context.setUnSetWaitingTimeout();
                return UN_SENT;
            }
            case WAS_SENT -> {
                if (context.isMustResend()) {
                    return CHECK_SEND;
                }
                if (context.isCodeEquals()) {
                    if (context.isCodeExpired()) {
                        return FAILED_EXPIRED_CODE;
                    }
                    return VERIFIED;
                }
                return FAILED_VERIFIED;
            }
            case UN_SENT -> {
                if (context.isUnSendTimeoutExpired()) {
                    context.clear();
                    return CHECK_SEND;
                }
                return UN_SENT;
            }
            case FAILED_EXPIRED_CODE -> {
                context.incrementAttemptsVerificationCode();
                return CHECK_SEND;
            }
            case FAILED_VERIFIED -> {
                if (context.isMustResend()) {
                    context.clearVerificationCodeAttempts();
                    return CHECK_SEND;
                } else if (context.isVerificationCodeAttemptsOverflow()) {
                    return CHECK_SEND;
                }
                if (context.isCodeEquals()) {
                    if (context.isCodeExpired()) {
                        return FAILED_EXPIRED_CODE;
                    }
                    return VERIFIED;
                }
                context.incrementAttemptsVerificationCode();
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
    public void handle(Machine.Handler handler) {
        //Empty
    }
}
