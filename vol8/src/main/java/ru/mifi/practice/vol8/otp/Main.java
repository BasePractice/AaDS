package ru.mifi.practice.vol8.otp;

import ru.mifi.practice.vol8.Machine;

import java.util.UUID;

import static ru.mifi.practice.vol8.otp.OTPKey.CODE;
import static ru.mifi.practice.vol8.otp.OTPMachine.PERSISTENCE_CODE;

public abstract class Main {
    public static void main(String[] args) {
        Machine machine = new OTPMachine();
        Machine.Context context = machine.getContext();
        OTPKey.MAX_SEND_CODE_ATTEMPTS.set(context, 3);
        Machine.State state = machine.execute();
        if (state.equals(OTP.WAS_SENT)) {
            context.set(CODE, UUID.randomUUID().toString());
        }
        state = machine.execute();
        if (!state.equals(OTP.FAILED_VERIFIED)) {
            throw new IllegalStateException();
        }
        OTPKey.RESENT.set(context, Boolean.TRUE);
        state = machine.execute();
        if (state.equals(OTP.WAS_SENT)) {
            context.set(CODE, context.get(PERSISTENCE_CODE, String.class).orElseThrow());
        }
        state = machine.execute();
        if (!state.equals(OTP.VERIFIED)) {
            throw new IllegalStateException();
        }
    }
}
