package ru.mifi.practice.vol1.bigo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

@DisplayName("Замер роста числа шагов")
final class MeasurementTest {

    @DisplayName("Меряет каждый размер входа")
    @Test
    @Timeout(1)
    void measuresEverySize() {
        assertThat("the measurement skips a size of the input",
            new Measurement(Workload.Type.LINEAR, 8, 16, 32).steps().keySet(), contains(8, 16, 32));
    }

    @DisplayName("Начинает счёт заново на каждом размере")
    @Test
    @Timeout(1)
    void countsEverySizeAnew() {
        assertThat("the steps of the previous size leak into the next one",
            new Measurement(Workload.Type.LINEAR, 8, 16).steps().get(16), is(16));
    }

    @DisplayName("Держит размеры в порядке замера")
    @Test
    @Timeout(1)
    void keepsTheOrderOfSizes() {
        assertThat("the measurement reorders the sizes",
            new Measurement(Workload.Type.CONSTANT, 32, 8, 16).steps().keySet(), contains(32, 8, 16));
    }

    @DisplayName("Печатает строку на каждый размер")
    @Test
    @Timeout(1)
    void printsALineForEverySize() {
        assertThat("the printed table lost a size",
            new Measurement(Workload.Type.QUADRATIC, 4, 8).toString(), containsString("8 |       64"));
    }
}
