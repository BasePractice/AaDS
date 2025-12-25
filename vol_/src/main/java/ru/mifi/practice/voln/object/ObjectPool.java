package ru.mifi.practice.voln.object;

import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static net.bytebuddy.matcher.ElementMatchers.isPublic;

public interface ObjectPool<T extends Closeable> extends Closeable {
    Optional<T> getObject(long timeout, TimeUnit unit);

    default Optional<T> getObject() {
        return getObject(500, TimeUnit.MILLISECONDS);
    }

    void dispose(T object);

    interface ProxyMarker {
        Object getTarget();
    }

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
        private final Object lock = new Object();

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

        @Override
        public Optional<T> getObject(long timeout, TimeUnit unit) {
            try {
                if (!semaphore.tryAcquire(timeout, unit)) {
                    return Optional.empty();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return Optional.empty();
            }

            try {
                T obj = pool.poll();
                if (obj == null) {
                    synchronized (lock) {
                        if (createdCount.get() < maxSize) {
                            obj = creator.get();
                            createdCount.incrementAndGet();
                        }
                    }
                    if (obj == null) {
                        try {
                            obj = pool.poll(timeout, unit);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
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
            } catch (RuntimeException e) {
                semaphore.release();
                throw e;
            } catch (Exception e) {
                semaphore.release();
                throw new RuntimeException(e);
            }
        }

        @Override
        public void dispose(T object) {
            if (object == null) {
                semaphore.release();
                return;
            }

            T target = object;
            if (object instanceof ProxyMarker) {
                target = (T) ((ProxyMarker) object).getTarget();
            }

            try {
                if (validator.test(target)) {
                    refresh.accept(target);
                    if (!pool.offer(target)) {
                        destroyObject(target);
                    }
                } else {
                    destroyObject(target);
                }
            } catch (Exception e) {
                destroyObject(target);
            } finally {
                semaphore.release();
            }
        }

        private void destroyObject(T object) {
            try {
                object.close();
            } catch (IOException e) {
                if (log.isErrorEnabled()) {
                    log.error("Error closing object: {}", e.getMessage());
                }
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

        private static final class Wrapper<T extends Closeable> {
            private final T target;
            private final ObjectPool<T> pool;
            private final AtomicBoolean closed = new AtomicBoolean(false);

            private Wrapper(T target, ObjectPool<T> pool) {
                this.target = target;
                this.pool = pool;
            }

            @SuppressWarnings("unchecked")
            public static <T extends Closeable> T proxy(ObjectPool<T> pool, T target, Class<T> clazz) {
                Wrapper<T> wrapper = new Wrapper<>(target, pool);
                Class<? extends T> proxyClass = new ByteBuddy()
                    .subclass(clazz, ConstructorStrategy.Default.NO_CONSTRUCTORS)
                    .implement(ProxyMarker.class)
                    .method(isPublic())
                    .intercept(MethodDelegation.to(wrapper))
                    .make()
                    .load(clazz.getClassLoader() != null ? clazz.getClassLoader() : ClassLoader.getSystemClassLoader(),
                        ClassLoadingStrategy.Default.INJECTION)
                    .getLoaded();

                try {
                    try {
                        return proxyClass.getDeclaredConstructor().newInstance();
                    } catch (NoSuchMethodException e) {
                        Constructor<?> objCtx = Object.class.getDeclaredConstructor();
                        Constructor<?> cons = sun.reflect.ReflectionFactory.getReflectionFactory()
                            .newConstructorForSerialization(proxyClass, objCtx);
                        return (T) cons.newInstance();
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Failed to create proxy", e);
                }
            }

            @RuntimeType
            public Object intercept(@Origin Method method, @AllArguments Object[] args) throws Throwable {
                String name = method.getName();
                int params = method.getParameterCount();

                if ("close".equals(name) && params == 0) {
                    if (closed.compareAndSet(false, true)) {
                        pool.dispose(target);
                    }
                    return null;
                }
                if ("getTarget".equals(name) && params == 0) {
                    return target;
                }

                if (closed.get()) {
                    throw new IllegalStateException("Object pool proxy is closed");
                }

                try {
                    return method.invoke(target, args);
                } catch (InvocationTargetException e) {
                    throw e.getCause();
                }
            }
        }
    }
}
