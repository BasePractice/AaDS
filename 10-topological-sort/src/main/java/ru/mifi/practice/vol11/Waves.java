package ru.mifi.practice.vol11;

import java.util.List;
import java.util.Set;

/**
 * Заготовка к заданию слайда 540: порядок сборки модулей «волнами». Волна — это модули, все
 * зависимости которых собраны в предыдущих волнах, поэтому внутри волны модули собираются
 * параллельно. Это алгоритм Кана, который снимает с очереди не по одной вершине, а весь слой
 * вершин с нулевой входящей степенью сразу. Цикл зависимостей делает сборку невозможной.
 */
public interface Waves {

    /**
     * Модули по волнам сборки, от первой к последней.
     */
    List<Set<String>> waves(List<Dependency> dependencies);

    /**
     * «Модуль module зависит от модуля requirement».
     */
    record Dependency(String module, String requirement) {
    }

    /**
     * Место для решения студента.
     */
    final class Layered implements Waves {

        @Override
        public List<Set<String>> waves(List<Dependency> dependencies) {
            throw new UnsupportedOperationException("Задание слайда 540 ещё не выполнено");
        }
    }
}
