package ru.mifi.practice.boring.misc;

import java.util.ArrayList;
import java.util.List;

/**
 * Алгоритм банкира Дейкстры: не даёт операционной системе загнать процессы во взаимоблокировку.
 * Банкир выдаёт кредиты, зная, сколько каждый клиент может попросить в худшем случае, и выдаёт
 * только те, после которых он ещё способен обслужить всех — пусть и по очереди.
 *
 * <p>Состояние называется безопасным, если существует порядок, в котором процессы можно
 * завершить: находим процесс, чьи оставшиеся потребности покрываются свободными ресурсами,
 * «завершаем» его, возвращаем занятое им в общий котёл и повторяем. Если так удаётся завершить
 * всех — порядок найден, а значит тупика не будет ни при каком развитии событий.
 *
 * <p>Отсюда и его цена: алгоритм требует знать максимальные запросы заранее, а это в реальных
 * системах известно редко. Поэтому его чаще изучают, чем применяют, — но идея «проверяй, что
 * из состояния есть выход» осталась и работает далеко за пределами операционных систем.
 */
public final class Banker {
    private final int[][] maximum;
    private final int[][] allocated;
    private final int[] available;

    public Banker(int[][] maximum, int[][] allocated, int[] available) {
        this.maximum = maximum.clone();
        this.allocated = allocated.clone();
        this.available = available.clone();
    }

    /** Безопасен ли текущий расклад — то есть найдётся ли порядок завершения процессов. */
    public boolean safe() {
        return !sequence().isEmpty() || maximum.length == 0;
    }

    /** Порядок завершения процессов; пустой список, если состояние небезопасно. */
    public List<Integer> sequence() {
        int[] free = available.clone();
        boolean[] finished = new boolean[maximum.length];
        List<Integer> order = new ArrayList<>();
        for (int[] ignored : maximum) {
            int next = affordable(free, finished);
            if (next < 0) {
                return List.of();
            }
            finished[next] = true;
            order.add(next);
            for (int resource = 0; resource < free.length; resource++) {
                free[resource] += allocated[next][resource];
            }
        }
        return order;
    }

    /** Процесс, чьи оставшиеся потребности покрываются свободными ресурсами. */
    private int affordable(int[] free, boolean[] finished) {
        for (int process = 0; process < maximum.length; process++) {
            if (!finished[process] && covered(process, free)) {
                return process;
            }
        }
        return -1;
    }

    private boolean covered(int process, int[] free) {
        for (int resource = 0; resource < free.length; resource++) {
            if (maximum[process][resource] - allocated[process][resource] > free[resource]) {
                return false;
            }
        }
        return true;
    }
}
