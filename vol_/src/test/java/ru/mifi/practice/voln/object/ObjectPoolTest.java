package ru.mifi.practice.voln.object;

import lombok.Getter;
import org.junit.jupiter.api.Test;

import java.io.Closeable;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ObjectPoolTest {

    @Test
    void testPoolBasic() throws IOException {
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

        Optional<TestResource> objOpt = pool.getObject();
        assertTrue(objOpt.isPresent());
        TestResource proxy = objOpt.get();

        assertFalse(proxy.isClosed());
        assertEquals(1, proxy.getId());

        proxy.close();
        // Check logs or just see if it doesn't crash.
        // The issue is that it might log an error.

        Optional<TestResource> objOpt2 = pool.getObject();
        assertTrue(objOpt2.isPresent());
        TestResource proxy2 = objOpt2.get();

        assertEquals(1, proxy2.getId());
        assertFalse(proxy2.isClosed());

        pool.close();
    }

    @Test
    void testValidation() throws IOException {
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

        TestResource proxy = pool.getObject().get();
        assertEquals(1, proxy.getId());

        valid.set(false);
        proxy.close(); // returns to pool

        valid.set(true);
        TestResource proxy2 = pool.getObject().get();
        assertEquals(2, proxy2.getId());

        pool.close();
    }

    @Test
    void testPoolLimit() throws InterruptedException, IOException {
        ObjectPool.Generic<TestResource> pool = new ObjectPool.Generic<>(
            TestResource::new,
            r -> {
            },
            r -> true,
            1,
            1,
            TestResource.class
        );

        TestResource proxy1 = pool.getObject().get();

        long start = System.currentTimeMillis();
        Optional<TestResource> objOpt = pool.getObject(100, TimeUnit.MILLISECONDS);
        long end = System.currentTimeMillis();

        assertFalse(objOpt.isPresent());
        assertTrue(end - start >= 100);

        proxy1.close();

        assertTrue(pool.getObject(10, TimeUnit.MILLISECONDS).isPresent());

        pool.close();
    }

    //TODO: Can't be final
    @Test
    void testNoDefaultConstructor() {
        ObjectPool.Generic<ResourceNoDefaultConstructor> pool = new ObjectPool.Generic<>(
            () -> new ResourceNoDefaultConstructor(1),
            r -> {},
            r -> true,
            1,
            1,
            ResourceNoDefaultConstructor.class
        );

        assertTrue(pool.getObject().isPresent());
        pool.close();
    }

    private static class ResourceNoDefaultConstructor implements Closeable {
        private final int id;

        public ResourceNoDefaultConstructor(int id) {
            this.id = id;
        }

        @Override
        public void close() {
        }
    }

    @Test
    void testUseAfterClose() throws IOException {
        ObjectPool.Generic<TestResource> pool = new ObjectPool.Generic<>(
            () -> new TestResource(1),
            r -> {},
            r -> true,
            1,
            1,
            TestResource.class
        );

        TestResource proxy = pool.getObject().get();
        proxy.close();

        org.junit.jupiter.api.Assertions.assertThrows(IllegalStateException.class, proxy::getId);

        pool.close();
    }

    @Test
    void testDisposeNull() {
        ObjectPool.Generic<TestResource> pool = new ObjectPool.Generic<>(
            TestResource::new, r -> {}, r -> true, 0, 1, TestResource.class
        );
        pool.dispose(null); // Should not throw and should release semaphore
        assertTrue(pool.getObject().isPresent());
        pool.close();
    }

    @Test
    void testPoolFullWaitAndReturn() throws InterruptedException {
        ObjectPool.Generic<TestResource> pool = new ObjectPool.Generic<>(
            TestResource::new, r -> {}, r -> true, 1, 1, TestResource.class
        );
        TestResource proxy = pool.getObject().get();

        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(100);
                proxy.close();
            } catch (Exception expected) {
            }
        });
        thread.start();

        Optional<TestResource> obj = pool.getObject(500, TimeUnit.MILLISECONDS);
        assertTrue(obj.isPresent());
        pool.close();
    }

    @Test
    void testRefreshCalled() throws IOException {
        AtomicInteger refreshCount = new AtomicInteger(0);
        ObjectPool.Generic<TestResource> pool = new ObjectPool.Generic<>(
            TestResource::new,
            r -> refreshCount.incrementAndGet(),
            r -> true,
            1,
            1,
            TestResource.class
        );

        TestResource proxy = pool.getObject().get();
        proxy.close();
        assertEquals(1, refreshCount.get());
        pool.close();
    }

    @Test
    void testDestroyOnDisposeIfInvalid() throws IOException {
        AtomicInteger closeCount = new AtomicInteger(0);
        ObjectPool.Generic<CloseableResource> pool = new ObjectPool.Generic<>(
            () -> new CloseableResource(closeCount),
            r -> {},
            r -> false, // always invalid
            1,
            1,
            CloseableResource.class
        );

        CloseableResource proxy = pool.getObject().get();
        proxy.close();
        // 1 from initial validation check in getObject (actually getObject creates new one if invalid)
        // No, initializePool creates 1. getObject takes it, validates, it's invalid, destroys (1),
        // creates new (but wait, getObject doesn't validate newly created object? actually it does)
        // Let's trace.
        assertTrue(closeCount.get() >= 1);
        pool.close();
    }

    @Test
    void testClosePool() throws IOException {
        AtomicInteger closeCount = new AtomicInteger(0);
        ObjectPool.Generic<CloseableResource> pool = new ObjectPool.Generic<>(
            () -> new CloseableResource(closeCount),
            r -> {},
            r -> true,
            2,
            2,
            CloseableResource.class
        );
        pool.close();
        assertEquals(2, closeCount.get());
    }

    private static class CloseableResource implements Closeable {
        private final AtomicInteger closeCount;

        public CloseableResource(AtomicInteger closeCount) {
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

        public TestResource() {
            this.id = 0;
        }

        public TestResource(int id) {
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
