package ru.mifi.practice.voln.heroes;

/**
 * Канал реплик к сопернику.
 *
 * <p>Локальной партии говорить некому, поэтому вместо проверки на отсутствие канала здесь стоит
 * молчание: интерфейс спрашивает, нужна ли панель чата, и не разбирается, откуда взялся ход.
 */
public interface Talk {

    /** Нужна ли интерфейсу панель реплик. */
    boolean present();

    /** Отправить реплику сопернику. */
    void say(String text);

    /** Молчание: за одним экраном разговаривать не с кем. */
    final class Silent implements Talk {

        @Override
        public boolean present() {
            return false;
        }

        @Override
        public void say(String text) {
            throw new IllegalStateException("Локальной партии некому отправить реплику: " + text);
        }
    }
}
