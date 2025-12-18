package ru.mifi.practice.voln.object;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.Pipe;

import java.io.Closeable;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static net.bytebuddy.matcher.ElementMatchers.isPublic;
import static net.bytebuddy.matcher.ElementMatchers.named;

public interface ObjectPool<T extends Closeable> extends Closeable {
    Optional<T> getObject(long timeout, TimeUnit unit);

    default Optional<T> getObject() {
        return getObject(500, TimeUnit.MILLISECONDS);
    }

    void dispose(T object);

    @Slf4j
    final class Generic<T extends Closeable> implements ObjectPool<T> {
        private final BlockingQueue<T> pool;
        private final Supplier<T> creator;
        private final Consumer<T> refresh;
        private final Predicate<T> validator;
        private final int maxSize;
        private final AtomicInteger createdCount;
        private final Semaphore semaphore;

        public Generic(Supplier<T> creator,
                       Consumer<T> refresh,
                       Predicate<T> validator,
                       int minSize,
                       int maxSize) {
            this.creator = creator;
            this.refresh = refresh;
            this.validator = validator;
            this.maxSize = maxSize;
            this.createdCount = new AtomicInteger(0);
            this.semaphore = new Semaphore(maxSize);
            this.pool = new LinkedBlockingQueue<>(maxSize);
            initializePool(minSize);
        }

        private void initializePool(int minSize) {
            for (int i = 0; i < minSize; i++) {
                try {
                    T obj = creator.get();
                    if (!pool.offer(obj)) {
                        throw new IllegalStateException("Can't put object to pool");
                    }
                    createdCount.incrementAndGet();
                } catch (Exception e) {
                    if (log.isErrorEnabled()) {
                        log.error("[INIT] Error: {}", e.getMessage());
                    }
                }
            }
        }

        @SneakyThrows
        @Override
        public Optional<T> getObject(long timeout, TimeUnit unit) {
            if (!semaphore.tryAcquire(timeout, unit)) {
                return Optional.empty();
            }

            T obj = pool.poll();
            if (obj == null) {
                synchronized (this) {
                    if (createdCount.get() < maxSize) {
                        obj = creator.get();
                        createdCount.incrementAndGet();
                        return Optional.of(obj);
                    }
                }
                obj = pool.take();
            }

            if (!validator.test(obj)) {
                destroyObject(obj);
                obj = creator.get();
            }

            return Optional.of(obj);
        }

        @Override
        public void dispose(T object) {
            if (object == null) {
                semaphore.release();
                return;
            }
            try {
                if (validator.test(object)) {
                    refresh.accept(object);
                    pool.offer(object);
                } else {
                    destroyObject(object);
                }
            } catch (Exception e) {
                destroyObject(object);
            } finally {
                semaphore.release();
            }
        }

        @SneakyThrows
        private void destroyObject(T object) {
            try {
                object.close();
            } finally {
                createdCount.decrementAndGet();
            }
        }

        @Override
        public void close() {
            T object;
            while ((object = pool.poll()) != null) {
                destroyObject(object);
            }
            createdCount.set(0);
        }

        private record Wrapper<T extends Closeable>(T delegate, ObjectPool<T> pool) {
            public static <T extends Closeable> T proxy(ObjectPool<T> pool, T delegate, Class<T> clazz) throws IllegalAccessException, InstantiationException,
                NoSuchMethodException, InvocationTargetException {
                return new ByteBuddy()
                    .subclass(clazz)
                    .method(isPublic().and(named("close")))
                    .intercept(MethodDelegation.withDefaultConfiguration()
                        .withBinders(Pipe.Binder.install(Function.class))
                        .to(new Wrapper<>(delegate, pool)))
                    .make()
                    .load(ClassLoader.getSystemClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                    .getLoaded()
                    .getDeclaredConstructor().newInstance();
            }

            public Void intercept(@Pipe Function<Object, Void> pipe) {
                // custom logic
                Void result = pipe.apply(delegate);
                // custom logic
                return result;
            }
        }
    }
}
