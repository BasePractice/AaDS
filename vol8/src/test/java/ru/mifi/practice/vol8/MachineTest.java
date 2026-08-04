package ru.mifi.practice.vol8;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Проверка исполнителя конечного автомата.
 *
 * <p>Автомат в проверках самый маленький, какой бывает: обратный отсчёт, где ноль терминален.
 * Смысл в том, что исполнитель крутит переходы до терминального состояния и обрабатывает только
 * его, а не в самих состояниях.
 */
@DisplayName("Исполнитель автомата")
final class MachineTest {

    @DisplayName("Исполнение доводит автомат до терминального состояния")
    @Test
    @Timeout(1)
    void runsUntilTheTerminalState() {
        assertThat("the run dont reach the terminal state",
            machine(3).execute(new Silent(new ArrayList<>(), new ArrayList<>())).isTerminated(), is(true));
    }

    @DisplayName("Исполнение проходит все промежуточные состояния")
    @Test
    @Timeout(1)
    void walksEveryIntermediateState() {
        Silent handler = new Silent(new ArrayList<>(), new ArrayList<>());
        machine(3).execute(handler);
        assertThat("the run dont walk every intermediate state",
            handler.walked(), contains("шаг 3", "шаг 2", "шаг 1", "конец"));
    }

    @DisplayName("Обрабатывается только терминальное состояние")
    @Test
    @Timeout(1)
    void handlesTheTerminalStateOnly() {
        Silent handler = new Silent(new ArrayList<>(), new ArrayList<>());
        machine(3).execute(handler);
        assertThat("the run handles more than the terminal state", handler.handled(), contains("конец"));
    }

    @DisplayName("Успешность терминального состояния доступна снаружи")
    @Test
    @Timeout(1)
    void tellsWhetherTheRunSucceeded() {
        assertThat("the run dont tell whether it succeeded",
            machine(2).execute(new Silent(new ArrayList<>(), new ArrayList<>())).isSuccessful(), is(true));
    }

    @DisplayName("Автомат без состояния отказывается исполняться")
    @Test
    @Timeout(1)
    void refusesToRunWithoutAState() {
        Machine machine = new Machine() {
        };
        assertThrows(IllegalStateException.class,
            () -> machine.execute(new Silent(new ArrayList<>(), new ArrayList<>())),
            "a machine without a state still runs");
    }

    @DisplayName("Контекст помнит состояние, на котором автомат остановился")
    @Test
    @Timeout(1)
    void remembersWhereTheRunStopped() {
        Machine machine = machine(2);
        machine.execute(new Silent(new ArrayList<>(), new ArrayList<>()));
        assertThat("the context dont remember where the run stopped",
            machine.getContext().currentState().title(), is("конец"));
    }

    @DisplayName("Копия контекста хранит те же значения")
    @Test
    @Timeout(1)
    void copiesTheContextValues() {
        Machine.Context context = new Machine.Context.Standard();
        context.setCurrentState(new Countdown(1));
        assertThat("the context copy dont hold the same values",
            context.copy().currentState().title(), is("шаг 1"));
    }

    @DisplayName("Очистка стирает значения контекста")
    @Test
    @Timeout(1)
    void clearsTheContextValues() {
        Machine.Context context = new Machine.Context.Standard();
        context.setCurrentState(new Countdown(1));
        context.clear();
        assertThat("clearing dont wipe the context", context.currentState(), is(nullState()));
    }

    private static Machine.State nullState() {
        return null;
    }

    /** Автомат из счётчика шагов: каждый переход уменьшает счётчик, ноль терминален. */
    private static Machine machine(int steps) {
        Machine machine = new Machine() {
        };
        machine.getContext().setCurrentState(new Countdown(steps));
        return machine;
    }

    /** Состояние обратного отсчёта: пока счётчик больше нуля, переход ведёт к меньшему. */
    private record Countdown(int left) implements Machine.State {

        @Override
        public Machine.State next(Machine.Context context, Machine.Handler handler) {
            return new Countdown(left - 1);
        }

        @Override
        public boolean isTerminated() {
            return left <= 0;
        }

        @Override
        public boolean isSuccessful() {
            return isTerminated();
        }

        @Override
        public String title() {
            return left <= 0 ? "конец" : "шаг " + left;
        }

        @Override
        public void handle(Machine.Handler handler) {
            handler.printf(title());
        }

        @Override
        public String toString() {
            return title();
        }
    }

    /** Обработчик-заглушка: запоминает пройденное и обработанное вместо печати. */
    private record Silent(List<String> walked, List<String> handled) implements Machine.Handler {

        @Override
        public void printf(String format, Object... args) {
            handled.add(format);
        }

        @Override
        public void debugf(String format, Object... args) {
            walked.add(String.valueOf(args[0]));
        }

        @Override
        public boolean sendNextCode(Machine.Context context) {
            return false;
        }

        @Override
        public boolean isCodeEquals(Machine.Context context, Machine.Key codeKey) {
            return false;
        }

        @Override
        public void persist(Machine.Context context) {
            //сохранять контекст в проверках некуда
        }
    }
}
