package ru.mifi.practice.voln.object;

import lombok.Getter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.io.Closeable;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class ObjectPoolTest {

    @Test
    @Timeout(5)
    @DisplayName("Полученный объект присутствует")
    void obtainedObjectIsPresent() throws IOException {
        AtomicInteger counter = new AtomicInteger(0);
        ObjectPool.Generic<TestResource> pool = new ObjectPool.Generic<>(
            () -> new TestResource(counter.incrementAndGet()),
            r -> {
            },
            r -> !r.isClosed(),
            1,
            2,
            TestResource.class
        );
        try {
            assertThat("obtained object is missing", pool.getObject().isPresent(), is(true));
        } finally {
            pool.close();
        }
    }

    @Test
    @Timeout(5)
    @DisplayName("Полученный объект не закрыт")
    void obtainedObjectIsNotClosed() throws IOException {
        AtomicInteger counter = new AtomicInteger(0);
        ObjectPool.Generic<TestResource> pool = new ObjectPool.Generic<>(
            () -> new TestResource(counter.incrementAndGet()),
            r -> {
            },
            r -> !r.isClosed(),
            1,
            2,
            TestResource.class
        );
        try {
            assertThat("obtained object is already closed", pool.getObject().get().isClosed(), is(false));
        } finally {
            pool.close();
        }
    }

    @Test
    @Timeout(5)
    @DisplayName("Первый объект имеет идентификатор один")
    void firstObjectHasIdOne() throws IOException {
        AtomicInteger counter = new AtomicInteger(0);
        ObjectPool.Generic<TestResource> pool = new ObjectPool.Generic<>(
            () -> new TestResource(counter.incrementAndGet()),
            r -> {
            },
            r -> !r.isClosed(),
            1,
            2,
            TestResource.class
        );
        try {
            assertThat("first object id is wrong", pool.getObject().get().getId(), is(1));
        } finally {
            pool.close();
        }
    }

    @Test
    @Timeout(5)
    @DisplayName("Возвращённый объект переиспользуется")
    void returnedObjectIsReused() throws IOException {
        AtomicInteger counter = new AtomicInteger(0);
        ObjectPool.Generic<TestResource> pool = new ObjectPool.Generic<>(
            () -> new TestResource(counter.incrementAndGet()),
            r -> {
            },
            r -> !r.isClosed(),
            1,
            2,
            TestResource.class
        );
        try {
            pool.getObject().get().close();
            assertThat("returned object was not reused", pool.getObject().get().getId(), is(1));
        } finally {
            pool.close();
        }
    }

    @Test
    @Timeout(5)
    @DisplayName("Переиспользованный объект не закрыт")
    void reusedObjectIsNotClosed() throws IOException {
        AtomicInteger counter = new AtomicInteger(0);
        ObjectPool.Generic<TestResource> pool = new ObjectPool.Generic<>(
            () -> new TestResource(counter.incrementAndGet()),
            r -> {
            },
            r -> !r.isClosed(),
            1,
            2,
            TestResource.class
        );
        try {
            pool.getObject().get().close();
            assertThat("reused object is closed", pool.getObject().get().isClosed(), is(false));
        } finally {
            pool.close();
        }
    }

    @Test
    @Timeout(5)
    @DisplayName("Валидный объект имеет идентификатор один")
    void validObjectHasIdOne() throws IOException {
        AtomicInteger counter = new AtomicInteger(0);
        AtomicBoolean valid = new AtomicBoolean(true);
        ObjectPool.Generic<TestResource> pool = new ObjectPool.Generic<>(
            () -> new TestResource(counter.incrementAndGet()),
            r -> {
            },
            r -> valid.get(),
            1,
            1,
            TestResource.class
        );
        try {
            assertThat("valid object id is wrong", pool.getObject().get().getId(), is(1));
        } finally {
            pool.close();
        }
    }

    @Test
    @Timeout(5)
    @DisplayName("Невалидный возвращённый объект пересоздаётся")
    void invalidReturnedObjectIsRecreated() throws IOException {
        AtomicInteger counter = new AtomicInteger(0);
        AtomicBoolean valid = new AtomicBoolean(true);
        ObjectPool.Generic<TestResource> pool = new ObjectPool.Generic<>(
            () -> new TestResource(counter.incrementAndGet()),
            r -> {
            },
            r -> valid.get(),
            1,
            1,
            TestResource.class
        );
        try {
            TestResource proxy = pool.getObject().get();
            valid.set(false);
            proxy.close();
            valid.set(true);
            assertThat("invalid object was not recreated", pool.getObject().get().getId(), is(2));
        } finally {
            pool.close();
        }
    }

    @Test
    @Timeout(5)
    @DisplayName("Исчерпанный пул возвращает пусто")
    void exhaustedPoolReturnsEmpty() throws InterruptedException, IOException {
        ObjectPool.Generic<TestResource> pool = new ObjectPool.Generic<>(
            TestResource::new,
            r -> {
            },
            r -> true,
            1,
            1,
            TestResource.class
        );
        try {
            pool.getObject().get();
            assertThat("exhausted pool returns an object", pool.getObject(100, TimeUnit.MILLISECONDS).isPresent(), is(false));
        } finally {
            pool.close();
        }
    }

    @Test
    @Timeout(5)
    @DisplayName("Исчерпанный пул ждёт таймаут")
    void exhaustedPoolWaitsForTimeout() throws InterruptedException, IOException {
        ObjectPool.Generic<TestResource> pool = new ObjectPool.Generic<>(
            TestResource::new,
            r -> {
            },
            r -> true,
            1,
            1,
            TestResource.class
        );
        try {
            pool.getObject().get();
            long start = System.currentTimeMillis();
            pool.getObject(100, TimeUnit.MILLISECONDS);
            long elapsed = System.currentTimeMillis() - start;
            assertThat("exhausted pool doesnt wait the whole timeout", elapsed, is(greaterThanOrEqualTo(100L)));
        } finally {
            pool.close();
        }
    }

    @Test
    @Timeout(5)
    @DisplayName("Освобождённый объект снова доступен")
    void releasedObjectBecomesAvailable() throws InterruptedException, IOException {
        ObjectPool.Generic<TestResource> pool = new ObjectPool.Generic<>(
            TestResource::new,
            r -> {
            },
            r -> true,
            1,
            1,
            TestResource.class
        );
        try {
            TestResource proxy = pool.getObject().get();
            proxy.close();
            assertThat("released object is not available again", pool.getObject(10, TimeUnit.MILLISECONDS).isPresent(), is(true));
        } finally {
            pool.close();
        }
    }

    @Test
    @Timeout(5)
    @DisplayName("Пул работает без конструктора по умолчанию")
    void poolWorksWithoutDefaultConstructor() throws IOException {
        ObjectPool.Generic<ResourceNoDefaultConstructor> pool = new ObjectPool.Generic<>(
            () -> new ResourceNoDefaultConstructor(1),
            r -> {
            },
            r -> true,
            1,
            1,
            ResourceNoDefaultConstructor.class
        );
        try {
            assertThat("pool cannot serve object without default constructor", pool.getObject().isPresent(), is(true));
        } finally {
            pool.close();
        }
    }

    @Test
    @Timeout(5)
    @DisplayName("Использование после закрытия бросает исключение")
    void useAfterCloseThrows() throws IOException {
        ObjectPool.Generic<TestResource> pool = new ObjectPool.Generic<>(
            () -> new TestResource(1),
            r -> {
            },
            r -> true,
            1,
            1,
            TestResource.class
        );
        try {
            TestResource proxy = pool.getObject().get();
            proxy.close();
            assertThrows(IllegalStateException.class, proxy::getId, "use after close doesnt throw");
        } finally {
            pool.close();
        }
    }

    @Test
    @Timeout(5)
    @DisplayName("Освобождение null не ломает пул")
    void disposeNullReleasesSemaphore() throws IOException {
        ObjectPool.Generic<TestResource> pool = new ObjectPool.Generic<>(
            TestResource::new,
            r -> {
            },
            r -> true,
            0,
            1,
            TestResource.class
        );
        try {
            pool.dispose(null);
            assertThat("dispose of null blocks further objects", pool.getObject().isPresent(), is(true));
        } finally {
            pool.close();
        }
    }

    @Test
    @Timeout(5)
    @DisplayName("Ожидающий поток получает возвращённый объект")
    void waitingThreadGetsReturnedObject() throws InterruptedException, IOException {
        ObjectPool.Generic<TestResource> pool = new ObjectPool.Generic<>(
            TestResource::new,
            r -> {
            },
            r -> true,
            1,
            1,
            TestResource.class
        );
        try {
            TestResource proxy = pool.getObject().get();
            Thread thread = new Thread(() -> {
                try {
                    Thread.sleep(100);
                    proxy.close();
                } catch (Exception expected) {
                    Thread.currentThread().interrupt();
                }
            });
            thread.start();
            assertThat("waiting thread doesnt receive the returned object",
                pool.getObject(500, TimeUnit.MILLISECONDS).isPresent(), is(true));
        } finally {
            pool.close();
        }
    }

    @Test
    @Timeout(5)
    @DisplayName("Функция обновления вызывается при возврате")
    void refreshIsCalledOnReturn() throws IOException {
        AtomicInteger refreshCount = new AtomicInteger(0);
        ObjectPool.Generic<TestResource> pool = new ObjectPool.Generic<>(
            TestResource::new,
            r -> refreshCount.incrementAndGet(),
            r -> true,
            1,
            1,
            TestResource.class
        );
        try {
            pool.getObject().get().close();
            assertThat("refresh is not called on return", refreshCount.get(), is(1));
        } finally {
            pool.close();
        }
    }

    @Test
    @Timeout(5)
    @DisplayName("Невалидный объект уничтожается при возврате")
    void invalidObjectIsDestroyedOnDispose() throws IOException {
        AtomicInteger closeCount = new AtomicInteger(0);
        ObjectPool.Generic<CloseableResource> pool = new ObjectPool.Generic<>(
            () -> new CloseableResource(closeCount),
            r -> {
            },
            r -> false,
            1,
            1,
            CloseableResource.class
        );
        try {
            pool.getObject().get().close();
            assertThat("invalid object is not destroyed on dispose", closeCount.get(), is(greaterThanOrEqualTo(1)));
        } finally {
            pool.close();
        }
    }

    @Test
    @Timeout(5)
    @DisplayName("Закрытие пула закрывает все объекты")
    void closingPoolClosesAllObjects() throws IOException {
        AtomicInteger closeCount = new AtomicInteger(0);
        ObjectPool.Generic<CloseableResource> pool = new ObjectPool.Generic<>(
            () -> new CloseableResource(closeCount),
            r -> {
            },
            r -> true,
            2,
            2,
            CloseableResource.class
        );
        pool.close();
        assertThat("closing pool doesnt close all objects", closeCount.get(), is(2));
    }

    private static class ResourceNoDefaultConstructor implements Closeable {
        private final int id;

        ResourceNoDefaultConstructor(int id) {
            this.id = id;
        }

        @Override
        public void close() {
        }
    }

    private static class CloseableResource implements Closeable {
        private final AtomicInteger closeCount;

        CloseableResource(AtomicInteger closeCount) {
            this.closeCount = closeCount;
        }

        @Override
        public void close() {
            closeCount.incrementAndGet();
        }
    }

    private static class TestResource implements Closeable {
        private final AtomicBoolean closed = new AtomicBoolean(false);
        @Getter
        private final int id;

        TestResource() {
            this.id = 0;
        }

        TestResource(int id) {
            this.id = id;
        }

        @Override
        public void close() throws IOException {
            closed.set(true);
        }

        public boolean isClosed() {
            return closed.get();
        }
    }
}
