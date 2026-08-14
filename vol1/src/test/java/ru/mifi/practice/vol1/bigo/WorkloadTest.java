package ru.mifi.practice.vol1.bigo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import ru.mifi.practice.commons.Counter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;

@DisplayName("Нагрузки известной сложности")
final class WorkloadTest {

    @DisplayName("Постоянная нагрузка не растёт с размером входа")
    @Test
    @Timeout(1)
    void constantDontGrow() {
        assertThat("the constant workload depends on the size of the input",
            steps(Workload.Type.CONSTANT, 1024), is(steps(Workload.Type.CONSTANT, 64)));
    }

    @DisplayName("Логарифмическая нагрузка прибавляет шаг на удвоении")
    @Test
    @Timeout(1)
    void logarithmicAddsAStepOnDoubling() {
        assertThat("the logarithmic workload grows faster than by one step",
            steps(Workload.Type.LOGARITHMIC, 512), is(steps(Workload.Type.LOGARITHMIC, 256) + 1));
    }

    @DisplayName("Линейная нагрузка удваивается на удвоении")
    @Test
    @Timeout(1)
    void linearDoublesOnDoubling() {
        assertThat("the linear workload grows other than twofold",
            steps(Workload.Type.LINEAR, 512), is(steps(Workload.Type.LINEAR, 256) * 2));
    }

    @DisplayName("Линейно-логарифмическая нагрузка растёт быстрее линейной, но медленнее квадратичной")
    @Test
    @Timeout(1)
    void linearithmicGrowsBetweenLinearAndQuadratic() {
        assertThat("the linearithmic workload leaves the gap between n and n squared",
            (double) steps(Workload.Type.LINEARITHMIC, 512) / steps(Workload.Type.LINEARITHMIC, 256),
            both(greaterThan(2.0)).and(lessThan(4.0)));
    }

    @DisplayName("Квадратичная нагрузка вырастает вчетверо на удвоении")
    @Test
    @Timeout(5)
    void quadraticQuadruplesOnDoubling() {
        assertThat("the quadratic workload grows other than fourfold",
            steps(Workload.Type.QUADRATIC, 512), is(steps(Workload.Type.QUADRATIC, 256) * 4));
    }

    private static int steps(Workload workload, int size) {
        Counter counter = Counter.create();
        workload.run(size, counter);
        return counter.count();
    }
}
