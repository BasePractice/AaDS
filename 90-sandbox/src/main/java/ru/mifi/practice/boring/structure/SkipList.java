package ru.mifi.practice.boring.structure;

import java.util.OptionalInt;
import java.util.Random;

/**
 * Список с пропусками: обычный упорядоченный связный список, над которым надстроены этажи-скорые
 * линии. На нулевом этаже лежат все числа, на первом — примерно половина, на втором — четверть,
 * и так далее. Поиск идёт сверху вниз: пока следующий на этаже меньше искомого — шагаем по
 * этажу, иначе спускаемся. Пропуская половину списка за шаг, приходим к O(log n).
 *
 * <p>Этаж нового числа выбирается броском монеты: пока выпадает орёл — растём. Никакой
 * перестройки при вставке нет, ни поворотов, ни перевешивания поддеревьев, — за счёт этого
 * структура и любима в конкурентных реализациях, где перестройка потребовала бы блокировок.
 * Балансировка здесь вероятностная: O(log n) выполняется в среднем, а не всегда.
 */
public final class SkipList implements Ordered {
    private static final int FLOORS = 16;
    private final Random random;
    private final Node head = new Node(0, FLOORS, true);
    private int size;

    public SkipList() {
        this(new Random());
    }

    public SkipList(Random random) {
        this.random = random;
    }

    @Override
    public void add(int value) {
        if (contains(value)) {
            return;
        }
        Node[] before = before(value);
        Node added = new Node(value, floors());
        for (int floor = 0; floor < added.next.length; floor++) {
            added.next[floor] = before[floor].next[floor];
            before[floor].next[floor] = added;
        }
        ++size;
    }

    @Override
    public void remove(int value) {
        Node[] before = before(value);
        Node target = before[0].next[0];
        if (target == null || target.key != value) {
            return;
        }
        for (int floor = 0; floor < target.next.length; floor++) {
            before[floor].next[floor] = target.next[floor];
        }
        --size;
    }

    @Override
    public boolean contains(int value) {
        Node found = before(value)[0].next[0];
        return found != null && found.key == value;
    }

    @Override
    public OptionalInt next(int value) {
        Node found = before(value)[0].next[0];
        if (found != null && found.key == value) {
            found = found.next[0];
        }
        return found == null ? OptionalInt.empty() : OptionalInt.of(found.key);
    }

    @Override
    public OptionalInt previous(int value) {
        Node found = before(value)[0];
        return found.head ? OptionalInt.empty() : OptionalInt.of(found.key);
    }

    @Override
    public int size() {
        return size;
    }

    /** Последний узел на каждом этаже, ключ которого строго меньше заданного. */
    private Node[] before(int value) {
        Node[] before = new Node[FLOORS];
        Node current = head;
        for (int floor = FLOORS - 1; floor >= 0; floor--) {
            while (current.next[floor] != null && current.next[floor].key < value) {
                current = current.next[floor];
            }
            before[floor] = current;
        }
        return before;
    }

    private int floors() {
        int floors = 1;
        while (floors < FLOORS && random.nextBoolean()) {
            ++floors;
        }
        return floors;
    }

    @Override
    public String toString() {
        return "список с пропусками из " + size + " чисел";
    }

    /** Узел: ключ, ссылки на следующий узел на каждом своём этаже и признак головы списка. */
    private static final class Node {
        private final int key;
        private final Node[] next;
        private final boolean head;

        Node(int key, int floors) {
            this(key, floors, false);
        }

        Node(int key, int floors, boolean head) {
            this.key = key;
            this.next = new Node[floors];
            this.head = head;
        }
    }
}
