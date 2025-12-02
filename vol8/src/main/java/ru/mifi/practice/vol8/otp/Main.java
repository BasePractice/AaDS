package ru.mifi.practice.vol8.otp;

import ru.mifi.practice.vol8.Machine;

import static ru.mifi.practice.vol8.Machine.MACHINE_CLASS;
import static ru.mifi.practice.vol8.otp.OTPKey.CODE;

public abstract class Main {
    public static void main(String[] args) {
        Machine.Context context = new Machine.Context.Standard();
        context.setCurrentState(OTP.INITIATE);
        context.set(MACHINE_CLASS, OTPMachine.class);
        Machine machine = Machine.of(context);
        OTPMachine.OTPHandler handler = new OTPMachine.OTPHandler();
        Machine.State state = machine.execute(handler);
        System.out.println(state);
        if (state.equals(OTP.WAS_SENT)) {
            context.set(CODE, handler.getCode());
        }
        state = machine.execute(handler);
        System.out.println(state);
    }
}
