package ru.mifi.practice.voln.object;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;

import java.io.Closeable;
import java.lang.reflect.Field;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static net.bytebuddy.matcher.ElementMatchers.isPublic;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.not;

public interface ObjectPool<T extends Closeable> extends Closeable {
    Optional<T> getObject(long timeout, TimeUnit unit);

    default Optional<T> getObject() {
        return getObject(500, TimeUnit.MILLISECONDS);
    }

    void dispose(T object);

    @SuppressWarnings("PMD.CloseResource")
    @Slf4j
    final class Generic<T extends Closeable> implements ObjectPool<T> {
        private final BlockingQueue<T> pool;
        private final Supplier<T> creator;
        private final Consumer<T> refresh;
        private final Predicate<T> validator;
        private final int maxSize;
        private final AtomicInteger createdCount;
        private final Semaphore semaphore;
        private final Class<T> type;

        public Generic(Supplier<T> creator,
                       Consumer<T> refresh,
                       Predicate<T> validator,
                       int minSize,
                       int maxSize,
                       Class<T> type) {
            this.creator = creator;
            this.refresh = refresh;
            this.validator = validator;
            this.maxSize = maxSize;
            this.type = type;
            this.createdCount = new AtomicInteger(0);
            this.semaphore = new Semaphore(maxSize);
            this.pool = new LinkedBlockingQueue<>(maxSize);
            initializePool(minSize);
        }

        private void initializePool(int minSize) {
            for (int i = 0; i < minSize; i++) {
                try {
                    T obj = creator.get();
                    if (pool.offer(obj)) {
                        createdCount.incrementAndGet();
                    } else {
                        destroyObject(obj);
                    }
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

            try {
                T obj = pool.poll();
                if (obj == null) {
                    synchronized (this) {
                        if (createdCount.get() < maxSize) {
                            obj = creator.get();
                            createdCount.incrementAndGet();
                        }
                    }
                    if (obj == null) {
                        obj = pool.poll(timeout, unit);
                    }
                }

                if (obj != null && !validator.test(obj)) {
                    destroyObject(obj);
                    obj = creator.get();
                    createdCount.incrementAndGet();
                }

                if (obj == null) {
                    semaphore.release();
                    return Optional.empty();
                }

                return Optional.of(Wrapper.proxy(this, obj, type));
            } catch (Exception e) {
                semaphore.release();
                throw e;
            }
        }

        @SuppressWarnings("PMD.AvoidAccessibilityAlteration")
        @Override
        public void dispose(T object) {
            if (object == null) {
                semaphore.release();
                return;
            }
            try {
                Field target = object.getClass().getDeclaredField("target");
                target.setAccessible(true);
                if (target.get(object).getClass().equals(type)) {
                    object = (T) target.get(object);
                }
            } catch (Exception e) {
                if (log.isErrorEnabled()) {
                    log.error("Error fetch field: {}", e.getMessage());
                }
            }

            try {
                if (validator.test(object)) {
                    refresh.accept(object);
                    if (!pool.offer(object)) {
                        destroyObject(object);
                    }
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

        private record Wrapper<T extends Closeable>(T target, ObjectPool<T> pool) {

            @SuppressWarnings("resource")
            @SneakyThrows
            public static <T extends Closeable> T proxy(ObjectPool<T> pool, T target, Class<T> clazz) {
                return new ByteBuddy()
                    .subclass(clazz)
                    .method(isPublic().and(not(named("close"))))
                    .intercept(MethodDelegation.to(target))
                    .method(named("close"))
                    .intercept(MethodDelegation.to(new Wrapper<>(target, pool)))
                    .make()
                    .load(clazz.getClassLoader() != null ? clazz.getClassLoader() : ClassLoader.getSystemClassLoader(),
                        ClassLoadingStrategy.Default.INJECTION)
                    .getLoaded()
                    .getDeclaredConstructor()
                    .newInstance();
            }

            @SuppressWarnings("unused")
            public void close() {
                pool.dispose(target);
            }
        }
    }
}
