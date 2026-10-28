package ru.mifi.practice.boring.dynamic;

/**
 * Алгоритм Витерби: восстанавливает наиболее вероятную цепочку скрытых состояний по цепочке
 * наблюдений. Модель такая: система переходит между состояниями, которых мы не видим, а видим
 * только сигналы, которые она подаёт. Классический пример — распознавание речи: состояния это
 * произносимые звуки, наблюдения — куски звукового спектра.
 *
 * <p>Перебор всех цепочек состояний стоил бы k^n. Витерби замечает, что наилучшая цепочка,
 * кончающаяся в состоянии s на шаге t, продолжает одну из наилучших цепочек шага t−1, — и хранит
 * по одной такой цепочке на состояние. Получается таблица k×n и цена O(n·k²).
 *
 * <p>Вероятности перемножаются, поэтому на длинных цепочках произведение уходит в машинный ноль.
 * Здесь считаются логарифмы: произведение превращается в сумму, точность сохраняется, а порядок
 * сравнения не меняется — логарифм монотонен.
 */
public final class Viterbi {
    private final double[] starts;
    private final double[][] transitions;
    private final double[][] emissions;

    public Viterbi(double[] starts, double[][] transitions, double[][] emissions) {
        this.starts = starts.clone();
        this.transitions = transitions.clone();
        this.emissions = emissions.clone();
    }

    /** Наиболее вероятная последовательность состояний для наблюдений. */
    public int[] path(int[] observations) {
        if (observations.length == 0) {
            return new int[0];
        }
        double[][] best = new double[observations.length][starts.length];
        int[][] previous = new int[observations.length][starts.length];
        for (int state = 0; state < starts.length; state++) {
            best[0][state] = log(starts[state]) + log(emissions[state][observations[0]]);
        }
        for (int step = 1; step < observations.length; step++) {
            for (int state = 0; state < starts.length; state++) {
                previous[step][state] = ancestor(best[step - 1], state);
                best[step][state] = best[step - 1][previous[step][state]]
                    + log(transitions[previous[step][state]][state])
                    + log(emissions[state][observations[step]]);
            }
        }
        return restored(best, previous);
    }

    /** Состояние предыдущего шага, из которого выгоднее всего прийти в заданное. */
    private int ancestor(double[] previous, int state) {
        int ancestor = 0;
        for (int candidate = 1; candidate < previous.length; candidate++) {
            if (previous[candidate] + log(transitions[candidate][state])
                > previous[ancestor] + log(transitions[ancestor][state])) {
                ancestor = candidate;
            }
        }
        return ancestor;
    }

    /** Обратный ход: от лучшего состояния последнего шага по запомненным предкам. */
    private int[] restored(double[][] best, int[][] previous) {
        int[] path = new int[best.length];
        int last = 0;
        for (int state = 1; state < starts.length; state++) {
            if (best[best.length - 1][state] > best[best.length - 1][last]) {
                last = state;
            }
        }
        path[path.length - 1] = last;
        for (int step = path.length - 1; step > 0; step--) {
            path[step - 1] = previous[step][path[step]];
        }
        return path;
    }

    /** Логарифм с оговорённым ответом на ноль: невозможное событие бесконечно невыгодно. */
    private double log(double probability) {
        return probability <= 0 ? Double.NEGATIVE_INFINITY : Math.log(probability);
    }
}
