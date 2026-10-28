package ru.mifi.practice.vol10;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

/**
 * Задание слайда 515: группировка аккаунтов. Реализуйте Accounts.Joined на DisjointSet, снимите
 * {@code @Disabled} и добейтесь зелёных тестов.
 */
@DisplayName("Группировка аккаунтов")
final class AccountsTest {

    @Disabled("Учебное задание слайда 515: группировка аккаунтов")
    @DisplayName("Связывает аккаунты по цепочке пар")
    @Test
    @Timeout(1)
    void joinsAccountsAlongAChainOfLinks() {
        List<Accounts.Link> links = List.of(
            new Accounts.Link("anna@mail", "anna.tg"), new Accounts.Link("anna.tg", "a.vk"),
            new Accounts.Link("boris@mail", "b.vk")
        );
        assertThat("a chain of links dont end up in one group",
            new Accounts.Joined().group(links), hasItem(Set.of("anna@mail", "anna.tg", "a.vk")));
    }

    @Disabled("Учебное задание слайда 515: число людей")
    @DisplayName("Число групп равно числу людей")
    @Test
    @Timeout(1)
    void countsOneGroupPerPerson() {
        List<Accounts.Link> links = List.of(
            new Accounts.Link("a", "b"), new Accounts.Link("c", "d"),
            new Accounts.Link("b", "e"), new Accounts.Link("f", "f")
        );
        assertThat("groups dont match people", new Accounts.Joined().group(links).size(), is(3));
    }
}
