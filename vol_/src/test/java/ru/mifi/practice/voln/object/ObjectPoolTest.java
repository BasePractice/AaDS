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
