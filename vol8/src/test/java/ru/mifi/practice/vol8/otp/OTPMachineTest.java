package ru.mifi.practice.vol8.otp;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import ru.mifi.practice.vol8.Machine;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesRegex;
import static org.hamcrest.Matchers.notNullValue;

/** Проверка автомата одноразового пароля: начальное состояние, обработчик и выдача кода. */
@DisplayName("Автомат одноразового пароля")
final class OTPMachineTest {

    @DisplayName("Автомат встаёт в начальное состояние")
    @Test
    @Timeout(1)
    void startsFromTheInitialState() {
        assertThat("the machine dont start from the initial state",
            new OTPMachine().getContext().currentState(), is(OTP.INITIATE));
    }

    @DisplayName("Автомат кладёт свой обработчик в контекст")
    @Test
    @Timeout(1)
    void putsItsHandlerIntoTheContext() {
        assertThat("the machine dont put its handler into the context",
            new OTPMachine().getContext().get(Machine.MACHINE_HANDLER, Machine.Handler.class).orElseThrow(),
            is(notNullValue()));
    }

    @DisplayName("Выдача кода кладёт его в контекст")
    @Test
    @Timeout(1)
    void putsTheSentCodeIntoTheContext() {
        Machine.Context context = new Machine.Context.Standard();
        new OTPMachine.OTPHandler().sendNextCode(context);
        assertThat("the sent code dont land in the context",
            context.get(OTPMachine.PERSISTENCE_CODE, String.class).orElseThrow(), matchesRegex("\\d{4}"));
    }

    @DisplayName("Свежий код совпадает сам с собой")
    @Test
    @Timeout(1)
    void acceptsTheCodeItHasJustSent() {
        Machine.Context context = new Machine.Context.Standard();
        OTPMachine.OTPHandler handler = new OTPMachine.OTPHandler();
        handler.sendNextCode(context);
        assertThat("the handler dont accept the code it has just sent",
            handler.isCodeEquals(context, OTPMachine.PERSISTENCE_CODE), is(true));
    }

    @DisplayName("Чужой код не принимается")
    @Test
    @Timeout(1)
    void refusesAForeignCode() {
        Machine.Context context = new Machine.Context.Standard();
        OTPMachine.OTPHandler handler = new OTPMachine.OTPHandler();
        handler.sendNextCode(context);
        context.set(OTPMachine.PERSISTENCE_CODE, "0000");
        assertThat("a foreign code gets accepted",
            handler.isCodeEquals(context, OTPMachine.PERSISTENCE_CODE), is(false));
    }

    @DisplayName("Без выданного кода проверка не проходит")
    @Test
    @Timeout(1)
    void refusesAnyCodeBeforeSending() {
        Machine.Context context = new Machine.Context.Standard();
        context.set(OTPMachine.PERSISTENCE_CODE, "1234");
        assertThat("a code gets accepted before anything was sent",
            new OTPMachine.OTPHandler().isCodeEquals(context, OTPMachine.PERSISTENCE_CODE), is(false));
    }
}
