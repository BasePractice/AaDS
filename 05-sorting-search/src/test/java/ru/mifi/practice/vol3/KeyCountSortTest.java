package ru.mifi.practice.vol3;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

/**
 * Задание слайда 259: сортировка подсчётом на объектах. Реализуйте KeyCountSort.Stable, снимите
 * {@code @Disabled} и добейтесь зелёных тестов. Затем замените обход справа налево на обход слева
 * направо и найдите вход, на котором второй тест покраснеет.
 */
@DisplayName("Сортировка подсчётом по ключу")
final class KeyCountSortTest {

    @Disabled("Учебное задание слайда 259: сортировка подсчётом по ключу")
    @DisplayName("Упорядочивает сотрудников по отделу")
    @Test
    @Timeout(1)
    void ordersEmployeesByDepartment() {
        List<Employee> staff = List.of(new Employee("Анна", 2), new Employee("Борис", 0), new Employee("Вера", 1));
        assertThat("employees dont come out ordered by department",
            names(new KeyCountSort.Stable<Employee>().sort(staff, Employee::department, 3)),
            contains("Борис", "Вера", "Анна"));
    }

    @Disabled("Учебное задание слайда 259: устойчивость сортировки подсчётом")
    @DisplayName("Сохраняет исходный порядок внутри отдела")
    @Test
    @Timeout(1)
    void keepsTheOriginalOrderWithinADepartment() {
        List<Employee> staff = List.of(
            new Employee("Анна", 1), new Employee("Борис", 0), new Employee("Вера", 1),
            new Employee("Глеб", 0), new Employee("Дина", 1)
        );
        assertThat("equal keys lose their original order",
            names(new KeyCountSort.Stable<Employee>().sort(staff, Employee::department, 2)),
            contains("Борис", "Глеб", "Анна", "Вера", "Дина"));
    }

    private static List<String> names(List<Employee> staff) {
        return staff.stream().map(Employee::name).collect(Collectors.toList());
    }

    private record Employee(String name, int department) {
    }
}
