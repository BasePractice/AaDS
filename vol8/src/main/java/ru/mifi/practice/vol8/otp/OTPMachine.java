package ru.mifi.practice.vol8.otp;

import ru.mifi.practice.vol8.Machine;

import java.util.Random;

public final class OTPMachine extends Machine {
    public OTPMachine() {
        context.setCurrentState(OTP.INITIATE);
        context.set(MACHINE_CLASS, OTPMachine.class);
    }

    private static final class OTPHandler implements Handler {
        private String code;

        @Override
        public void printf(String format, Object... args) {
            System.out.printf(format + "%n", args);
        }

        @Override
        public boolean sendNextCode(Context context) {
            code = String.valueOf(new Random().nextInt(9999) + 1000);
            debugf("code: %s", code);
            return true;
        }

        @Override
        public boolean isCodeEquals(Context context, Key codeKey) {
            return context.get(codeKey, String.class).map(c -> c.equals(code)).orElse(false);
        }

        @Override
        public void persist(Context context) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
