package ru.mifi.practice.vol10;

import java.util.List;
import java.util.Set;

/**
 * Заготовка к заданию слайда 515: группировка аккаунтов по людям. Каждая пара «эти два аккаунта
 * принадлежат одному человеку» — это union, а человек — это множество в DisjointSet. Имена
 * аккаунтов нумеруются по мере появления, поэтому хватает одного прохода по парам.
 */
public interface Accounts {

    /**
     * Группы аккаунтов, по одной на человека.
     */
    List<Set<String>> group(List<Link> links);

    /**
     * Два аккаунта одного человека.
     */
    record Link(String left, String right) {
    }

    /**
     * Место для решения студента.
     */
    final class Joined implements Accounts {

        @Override
        public List<Set<String>> group(List<Link> links) {
            throw new UnsupportedOperationException("Задание слайда 515 ещё не выполнено");
        }
    }
}
