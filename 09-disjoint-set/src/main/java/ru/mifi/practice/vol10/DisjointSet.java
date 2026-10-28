package ru.mifi.practice.vol10;

import ru.mifi.practice.commons.Counter;

/**
 * Система непересекающихся множеств из раздела 10 курса. Отвечает на вопрос «лежат ли два
 * элемента в одном множестве» и умеет объединять множества.
 *
 * <p>Наивное решение хранит номер множества у каждого элемента: проверка стоит O(1), но
 * объединение перекрашивает всё множество за O(n). Здесь множество — дерево, представителем
 * которого служит корень, поэтому объединение сводится к подвешиванию одного корня под другой.
 */
public interface DisjointSet {

    /**
     * Представитель множества, которому принадлежит элемент.
     */
    int find(int element, Counter counter);

    /**
     * Объединяет множества двух элементов. Возвращает false, если они уже были вместе.
     */
    boolean union(int left, int right, Counter counter);

    /**
     * Число оставшихся множеств.
     */
    int sets();

    default boolean connected(int left, int right, Counter counter) {
        return find(left, counter) == find(right, counter);
    }

    /**
     * Две эвристики вместе. Объединение по размеру подвешивает меньшее дерево под большее,
     * поэтому глубина элемента растёт, только когда его дерево входит в вдвое большее, — не
     * чаще log n раз. Сжатие путей переписывает пройденные ссылки сразу на корень, так что
     * следующий поиск для этих элементов стоит один шаг. Вместе они дают O(α(n)) на операцию,
     * а обратная функция Аккермана не превосходит четырёх для любого мыслимого n.
     */
    final class Compressed implements DisjointSet {
        private final int[] parents;
        private final int[] sizes;
        private int sets;

        public Compressed(int size) {
            if (size < 0) {
                throw new IllegalArgumentException("Размер не может быть отрицательным: " + size);
            }
            this.parents = new int[size];
            this.sizes = new int[size];
            this.sets = size;
            for (int i = 0; i < size; i++) {
                parents[i] = i;
                sizes[i] = 1;
            }
        }

        @Override
        public int find(int element, Counter counter) {
            int root = check(element);
            while (parents[root] != root) {
                counter.increment();
                root = parents[root];
            }
            int it = element;
            while (parents[it] != root) {
                int next = parents[it];
                parents[it] = root;
                it = next;
            }
            return root;
        }

        @Override
        public boolean union(int left, int right, Counter counter) {
            int rootLeft = find(left, counter);
            int rootRight = find(right, counter);
            if (rootLeft == rootRight) {
                return false;
            }
            if (sizes[rootLeft] < sizes[rootRight]) {
                int swap = rootLeft;
                rootLeft = rootRight;
                rootRight = swap;
            }
            parents[rootRight] = rootLeft;
            sizes[rootLeft] += sizes[rootRight];
            --sets;
            return true;
        }

        @Override
        public int sets() {
            return sets;
        }

        private int check(int element) {
            if (element < 0 || element >= parents.length) {
                throw new IndexOutOfBoundsException("Элемент вне множества: " + element);
            }
            return element;
        }
    }

    /**
     * Наивное решение из лекции: номер множества хранится у каждого элемента. Оставлено для
     * сравнения — объединение обходит весь массив, поэтому счётчик операций растёт линейно.
     */
    final class Colored implements DisjointSet {
        private final int[] colors;

        public Colored(int size) {
            this.colors = new int[size];
            for (int i = 0; i < size; i++) {
                colors[i] = i;
            }
        }

        @Override
        public int find(int element, Counter counter) {
            counter.increment();
            return colors[element];
        }

        @Override
        public boolean union(int left, int right, Counter counter) {
            int from = colors[right];
            int to = colors[left];
            if (from == to) {
                return false;
            }
            for (int i = 0; i < colors.length; i++) {
                counter.increment();
                if (colors[i] == from) {
                    colors[i] = to;
                }
            }
            return true;
        }

        @Override
        public int sets() {
            int count = 0;
            for (int i = 0; i < colors.length; i++) {
                if (colors[i] == i) {
                    ++count;
                }
            }
            return count;
        }
    }
}
