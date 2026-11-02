package ru.mifi.practice.vol9;

import ru.mifi.practice.commons.Counter;
import ru.mifi.practice.vol9.yasymb.SimpleGen1;
import ru.mifi.practice.vol9.yasymb.YaSymbol;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * Примеры запуска алгоритмов раздела 4 с выводом фактического числа операций. Похожие алгоритмы
 * получают одинаковые входные данные, чтобы счётчики можно было сравнивать между собой.
 */
public abstract class Main {
    private static final int PERMUTATION_SIZE = 6;
    private static final int SUBSET_SIZE = 10;
    private static final int N = 10;
    private static final int K = 4;

    public static void main(String[] args) {
        comparePermutations();
        compareSubsets();
        compareCounting();
        compareNumbers();
        compareGeneration();
        solvePuzzle();
    }

    private static void comparePermutations() {
        System.out.printf("Перестановки, n = %d%n", PERMUTATION_SIZE);
        Counter combinatorics = Counter.create();
        int generated = consume(Combinatorics.permutationsOf(PERMUTATION_SIZE, combinatorics));
        print("Combinatorics", generated, combinatorics.count());

        Counter heap = Counter.create();
        generated = consume(HeapPermutations.of(PERMUTATION_SIZE, heap));
        print("HeapPermutations", generated, heap.count());

        Counter johnson = Counter.create();
        generated = consume(JohnsonTrotter.permutations(PERMUTATION_SIZE, johnson));
        print("JohnsonTrotter", generated, johnson.count());
        System.out.println();
    }

    private static void compareSubsets() {
        System.out.printf("Подмножества, n = %d%n", SUBSET_SIZE);
        Counter subsets = Counter.create();
        int generated = consume(Subsets.of(SUBSET_SIZE, subsets));
        print("Subsets", generated, subsets.count());

        Counter gray = Counter.create();
        generated = consume(GrayCode.of(SUBSET_SIZE, gray));
        print("GrayCode", generated, gray.count());

        Counter tuples = Counter.create();
        generated = consume(Combinatorics.arrangementsWithRepetitionOf(2, SUBSET_SIZE, tuples));
        print("ArrangementsWithRepetition", generated, tuples.count());
        System.out.println();
    }

    private static void compareCounting() {
        System.out.printf("Комбинаторные числа, n = %d, k = %d%n", N, K);
        Counter permutations = Counter.create();
        BigInteger value = Combinatorics.permutations(N, permutations);
        print("permutations", value, permutations.count());

        Counter arrangements = Counter.create();
        value = Combinatorics.arrangements(N, K, arrangements);
        print("arrangements", value, arrangements.count());

        Counter combinations = Counter.create();
        value = Combinatorics.combinations(N, K, combinations);
        print("combinations", value, combinations.count());

        Counter arrangementsWithRepetition = Counter.create();
        value = Combinatorics.arrangementsWithRepetition(N, K, arrangementsWithRepetition);
        print("arrangementsWithRepetition", value, arrangementsWithRepetition.count());

        Counter combinationsWithRepetition = Counter.create();
        value = Combinatorics.combinationsWithRepetition(N, K, combinationsWithRepetition);
        print("combinationsWithRepetition", value, combinationsWithRepetition.count());
        System.out.println();
    }

    private static void compareNumbers() {
        System.out.printf("Числовые последовательности, n = %d, k = %d%n", N, K);
        Counter bell = Counter.create();
        BigInteger value = Bell.number(N, bell);
        print("Bell", value, bell.count());

        Counter catalan = Counter.create();
        value = Catalan.number(N, catalan);
        print("Catalan", value, catalan.count());

        Counter derangements = Counter.create();
        value = Derangements.number(N, derangements);
        print("Derangements", value, derangements.count());

        Counter second = Counter.create();
        value = Stirling.second(N, K, second);
        print("Stirling.second", value, second.count());

        Counter first = Counter.create();
        value = Stirling.first(N, K, first);
        print("Stirling.first", value, first.count());

        Counter starsAndBars = Counter.create();
        value = StarsAndBars.number(N, K, starsAndBars);
        print("StarsAndBars", value, starsAndBars.count());
        System.out.println();
    }

    private static void compareGeneration() {
        System.out.printf("Порождение наборов, n = %d%n", N);
        Counter partitions = Counter.create();
        int generated = consume(Partitions.of(N, partitions));
        print("Partitions", generated, partitions.count());

        Counter pascal = Counter.create();
        List<List<BigInteger>> triangle = PascalTriangle.of(N, pascal);
        print("PascalTriangle", triangle.size(), pascal.count());
        System.out.println();
    }

    private static void solvePuzzle() {
        String x = "odin";
        String y = "odin";
        String z = "mnogo";
        System.out.printf("Ребус: %s + %s = %s%n", x, y, z);
        Counter counter = Counter.create();
        Optional<YaSymbol.Equation> equation = new SimpleGen1().process(x, y, z, counter);
        if (equation.isPresent()) {
            YaSymbol.Equation solved = equation.get();
            System.out.printf("%s + %s = %s, операций: %d%n",
                solved.x(), solved.y(), solved.z(), counter.count());
        } else {
            System.out.printf("решений нет, операций: %d%n", counter.count());
        }
    }

    private static int consume(Iterable<int[]> iterable) {
        int generated = 0;
        Iterator<int[]> iterator = iterable.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            ++generated;
        }
        return generated;
    }

    private static void print(String name, Object value, int operations) {
        System.out.printf("%-27s значение = %-12s операций = %d%n", name, value, operations);
    }
}
