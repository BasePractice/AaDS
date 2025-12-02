package ru.mifi.practice.vol8.otp;

import ru.mifi.practice.vol8.Machine;

import static ru.mifi.practice.vol8.otp.OTPKey.CODE;
import static ru.mifi.practice.vol8.otp.OTPMachine.PERSISTENCE_CODE;

public abstract class Main {
    public static void main(String[] args) {
        Machine machine = new OTPMachine();
        Machine.Context context = machine.getContext();
        Machine.State state = machine.execute();
        if (state.equals(OTP.WAS_SENT)) {
            context.set(CODE, context.get(PERSISTENCE_CODE, String.class).orElseThrow());
        }
        state = machine.execute();
        if (!state.equals(OTP.VERIFIED)) {
            throw new IllegalStateException();
        }
    }
}
