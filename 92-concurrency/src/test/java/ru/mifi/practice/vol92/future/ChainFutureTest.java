package ru.mifi.practice.vol92.future;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Цепочка CompletableFuture")
final class ChainFutureTest {

    @DisplayName("Конвейер обрезает, снижает регистр и сливает пробелы")
    @Test
    @Timeout(30)
    void trimsLowercasesAndDashes() throws Exception {
        ChainFuture chain = new ChainFuture();
        assertThat("chain produces wrong result",
            chain.pipeline("  Java Threads ").get(), is("java-threads"));
    }
}
