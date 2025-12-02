package ru.mifi.practice.vol8;

import java.lang.reflect.Constructor;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public abstract class Machine {
    protected static final Key MACHINE_CLASS = () -> "machine_class";
    protected final Context context;

    protected Machine() {
        this(new Context.Standard());
    }

    protected Machine(Context context) {
        this.context = context;
    }

    @SuppressWarnings("PMD.AvoidAccessibilityAlteration")
    public static Machine of(Context context) {
        try {
            String machineClass = context.get(MACHINE_CLASS, String.class).orElse(Standard.class.getSimpleName());
            Class<?> klass = Class.forName(machineClass);
            Constructor<?> constructor = klass.getDeclaredConstructor(Context.class);
            constructor.setAccessible(true);
            return (Machine) constructor.newInstance(context);
        } catch (Exception ex) {
            return new Standard(context);
        }
    }

    public State execute(Handler handler) {
        return execute(context, handler);
    }

    private State execute(Context context, Handler handler) {
        State state = context.currentState();
        if (state == null) {
            throw new IllegalStateException("No state available");
        }
        handler.debugf("[%10s] start%n", state);
        State next = state.next(context, handler);
        handler.debugf("[%10s] next%n", next);
        while (!next.isTerminated()) {
            next = next.next(context, handler);
            handler.debugf("[%10s] next%n", next);
        }
        next.handle(handler);
        context.setCurrentState(next);
        if (context.isModified()) {
            handler.persist(context);
        }
        return next;
    }

    @FunctionalInterface
    public interface Key {
        String key();
    }

    public interface State {
        State next(Context context, Handler handler);

        boolean isTerminated();

        boolean isSuccessful();

        String title();

        void handle(Handler handler);
    }

    public interface Context {
        State currentState();

        void setCurrentState(State state);

        default boolean isModified() {
            return true;
        }

        void clear();

        void clear(Key... key);

        <T> void set(Key key, T value);

        <T> Optional<T> get(Key key, Class<T> valueClass);

        final class Standard implements Context {
            private static final DateTimeFormatter DATE_TIME_FORMATTER =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ROOT);
            private static final Key STATE_KEY = () -> "state";
            private final Map<String, Object> values = new HashMap<>();

            @Override
            public State currentState() {
                return get(STATE_KEY, State.class).orElse(null);
            }

            @Override
            public void setCurrentState(State state) {
                set(STATE_KEY, state);
            }

            @Override
            public void clear() {
                //FIXME:
                values.clear();
            }

            @Override
            public void clear(Key... key) {
                for (Key value : key) {
                    values.remove(value.key());
                }
            }

            @Override
            public <T> void set(Key key, T value) {
                values.put(key.key(), value);
            }

            @SuppressWarnings("unchecked")
            @Override
            public <T> Optional<T> get(Key key, Class<T> valueClass) {
                return Optional.ofNullable(Optional.ofNullable((T) values.get(key.key())).orElseGet(() -> {
                    String v = System.getenv(key.key().toUpperCase(Locale.ROOT));
                    if (v == null || v.isEmpty()) {
                        v = System.getProperty(key.key().toLowerCase(Locale.ROOT));
                    }
                    if (v != null && !v.isEmpty()) {
                        if (valueClass.isAssignableFrom(Integer.class)) {
                            return (T) Integer.valueOf(v);
                        } else if (valueClass.isAssignableFrom(LocalDateTime.class)) {
                            return (T) DATE_TIME_FORMATTER.parse(v, LocalDateTime::from);
                        } else if (valueClass.isAssignableFrom(String.class)) {
                            return (T) v;
                        } else {
                            throw new RuntimeException("Unsupported value type: " + valueClass);
                        }
                    }
                    return null;
                }));
            }
        }
    }

    public interface Handler {
        void printf(String format, Object... args);

        default void debugf(String format, Object... args) {
            printf(format, args);
        }

        boolean sendNextCode(Context context);

        boolean isCodeEquals(Context context, Key codeKey);

        void persist(Context context);
    }

    private static final class Standard extends Machine {
        private Standard(Context context) {
            super(context);
        }
    }
}
