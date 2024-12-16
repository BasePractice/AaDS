package ru.mifi.practice.vol8;

public interface Machine {

    static State execute(Context context, Handler handler) {
        State state = context.currentState();
        handler.printf("[%10s] start%n", state);
        State next = state.next(context, handler);
        handler.printf("[%10s] next%n", next);
        while (!next.isTerminated()) {
            next = next.next(context, handler);
            handler.printf("[%10s] next%n", next);
        }
        next.handle(handler);
        context.setCurrentState(next);
        return next;
    }

    interface State {
        State next(Context context, Handler handler);

        boolean isTerminated();

        boolean isSuccessful();

        String title();

        void handle(Handler handler);
    }

    interface Context {
        State currentState();

        void setCurrentState(State state);

        default boolean isModified() {
            return false;
        }

        void clear();

        default boolean isDebug() {
            return true;
        }


        //
        boolean isSentCodeAttemptsOverflow();

        void setBlockingTimeout();

        void setBlockingReason(String text);

        void setWaitingTimeout();

        void setWaitingReason(String text);

        boolean isVerificationCodeAttemptsOverflow();

        boolean isWaitingTimeoutExpired();

        boolean isBlockingTimeoutExpired();

        void incrementAttemptsSentCode();

        void setUnSetWaitingTimeout();

        boolean isUnSendTimeoutExpired();

        void incrementAttemptsVerificationCode();

        boolean isCodeExpired();

        boolean isCodeEquals();

        boolean isMustResend();

        void clearVerificationCodeAttempts();
    }

    interface Handler {
        void printf(String format, Object... args);

        boolean sendNextCode(Context context);
    }
}
