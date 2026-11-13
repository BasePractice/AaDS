package ru.mifi.practice.vol92.collection;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

@DisplayName("Конвейер через ArrayBlockingQueue")
final class QueuePipelineTest {

    @DisplayName("Стадии обмениваются числами через очередь со стоп-значением")
    @Test
    @Timeout(30)
    void passesItemsBetweenStages() throws InterruptedException {
        QueuePipeline pipeline = new QueuePipeline(2, -1);
        Thread stage = new Thread(() -> {
            try {
                pipeline.stage(5);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        stage.start();
        assertThat("pipeline loses items between stages",
            pipeline.drain(), contains(0, 1, 2, 3, 4));
        stage.join(5000);
    }
}
