package ru.mifi.practice.vol8.regexp.machine;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;

@DisplayName("Связь состояния со следующим")
final class LinkTest {
    @DisplayName("Связь помнит номер своего состояния")
    @Test
    @Timeout(5)
    void keepsTheNumberOfItsState() {
        assertThat("link dont keep the number of its own state", new Link(7).index(), is(7));
    }

    @DisplayName("Связь помнит состояние, на которое переведена")
    @Test
    @Timeout(5)
    void keepsTheStateItPointsTo() {
        Link link = new Link(0);
        State.Symbol next = new Manager.Default().symbol('d');
        link.setNext(next);
        assertThat("link dont keep the state it points to", link.next(), is(sameInstance(next)));
    }

    @DisplayName("Связь без продолжения ведёт в никуда")
    @Test
    @Timeout(5)
    void leadsNowhereWithoutContinuation() {
        assertThat("link without a continuation leads somewhere", new Link(0).lastState(), is((State) null));
    }

    @DisplayName("Пустое состояние не принимает символов")
    @Test
    @Timeout(5)
    void makesEmptyStateRejectEveryInput() {
        assertThat("empty state accepts an input", new Link(0).accept(Input.of("a")), is(false));
    }

    @DisplayName("Пустое состояние не совпадает с входом")
    @Test
    @Timeout(5)
    void cannotMatchEmptyState() {
        assertThat("empty state matches an input", new Link(0).match(Input.of("a")).ok(), is(false));
    }

    @DisplayName("Пустое состояние подписывается на диаграмме эпсилоном")
    @Test
    @Timeout(5)
    void labelsEmptyStateAsEpsilon() {
        assertThat("empty state dont label itself as an epsilon", new Link(0).diagramLabel(), is("Epsilon"));
    }

    @DisplayName("Связь печатается стрелкой к следующему состоянию")
    @Test
    @Timeout(5)
    void printsItselfAsArrowToTheNextState() {
        Link link = new Link(0);
        link.setNext(new Manager.Default().symbol('d'));
        assertThat("link dont print itself as an arrow", link.toString(), is(" --> d"));
    }

    @DisplayName("Связь без продолжения печатается пустой строкой")
    @Test
    @Timeout(5)
    void printsBlankWithoutContinuation() {
        assertThat("link without a continuation dont print as a blank string",
            new Link(0).toString(), is(""));
    }
}
