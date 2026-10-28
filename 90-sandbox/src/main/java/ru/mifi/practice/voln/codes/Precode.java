package ru.mifi.practice.voln.codes;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Правила разрежённого precode над GF(2).
 *
 * <p>Каждый проверочный символ равен сумме по модулю два нескольких исходных, то есть
 * их XOR вместе с ним самим равен нулю. Это готовое уравнение, которое декодер знает
 * заранее, не получая ни одного символа по каналу.
 *
 * <p>Правила лежат отдельно от кодировщика именно поэтому: раньше их знал только он,
 * и проверочные символы добавляли декодеру неизвестные, не добавляя ни одного уравнения —
 * precode не ускорял восстановление, а замедлял его ровно на число проверочных символов.
 */
public final class Precode {
    private static final int MIN_DEGREE = 2;

    private Precode() {
    }

    /**
     * Уравнения precode для k исходных символов. Каждое возвращается набором индексов
     * промежуточных символов, XOR которых равен нулю: сам проверочный символ и его слагаемые.
     *
     * @param k      число исходных символов
     * @param config конфигурация кода
     * @return список уравнений
     */
    public static List<int[]> relations(int k, RaptorConfiguration config) {
        List<int[]> relations = new ArrayList<>(config.parityCount());
        int baseDegree = Math.max(MIN_DEGREE, (int) Math.round(Math.log(Math.max(2, k))));
        for (int j = 0; j < config.parityCount(); j++) {
            Set<Integer> sources = sources(k, Math.min(baseDegree + (j % 2), k), config.seed(), j);
            int[] relation = new int[sources.size() + 1];
            relation[0] = k + j;
            int at = 1;
            for (int source : sources) {
                relation[at] = source;
                ++at;
            }
            relations.add(relation);
        }
        return relations;
    }

    private static Set<Integer> sources(int k, int degree, long seed, int parity) {
        if (degree > k) {
            throw new IllegalArgumentException("Степень " + degree + " больше числа исходных символов " + k);
        }
        DeterministicRandom random = new DeterministicRandom(seed ^ (0x5EEDL + parity));
        Set<Integer> chosen = new LinkedHashSet<>();
        while (chosen.size() < degree) {
            chosen.add(random.nextInt(k));
        }
        return chosen;
    }
}
