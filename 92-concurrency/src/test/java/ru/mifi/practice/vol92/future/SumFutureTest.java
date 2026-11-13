package ru.mifi.practice.vol92.future;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Future с блокирующим get")
final class SumFutureTest {

    @DisplayName("Сумма возвращается через get и пул завершается")
    @Test
    @Timeout(30)
    void returnsSumByGetAndFinishes() throws Exception {
        SumFuture sum = new SumFuture();
        long total = sum.sum(100).get();
        assertThat("future returns wrong sum",
            total, is(5050L));
        assertThat("pool doesnt terminate after shutdown",
            sum.finish(), is(true));
    }
}
