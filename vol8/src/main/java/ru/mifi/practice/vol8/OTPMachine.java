package ru.mifi.practice.vol8;

public final class OTPMachine extends Machine {
    public OTPMachine() {
        context.setCurrentState(OTP.INITIATE);
        context.set(MACHINE_CLASS, OTPMachine.class);
    }

    private static final class OTPHandler implements Handler {
        @Override
        public void printf(String format, Object... args) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean sendNextCode(Context context) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean isCodeEquals(Context context, Key codeKey) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void persist(Context context) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
