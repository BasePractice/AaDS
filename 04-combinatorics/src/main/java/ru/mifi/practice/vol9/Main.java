package ru.mifi.practice.vol9;

import ru.mifi.practice.commons.Counter;
import ru.mifi.practice.commons.ObjectCounter;
import ru.mifi.practice.vol9.yasymb.SimpleGen1;
import ru.mifi.practice.vol9.yasymb.YaSymbol;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Примеры запуска алгоритмов раздела 4 с выводом числа операций и выделенных объектов. Похожие
 * алгоритмы получают одинаковые входные данные, чтобы счётчики можно было сравнивать между собой.
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
        printGrowth();
        solvePuzzle();
    }

    private static void comparePermutations() {
        System.out.printf("Перестановки, n = %d%n", PERMUTATION_SIZE);
        Counter combinatorics = Counter.create();
        ObjectCounter objects = ObjectCounter.create();
        int generated = consume(Combinatorics.permutationsOf(PERMUTATION_SIZE, combinatorics, objects));
        print("Combinatorics", generated, combinatorics.count(), objects.count());

        Counter heap = Counter.create();
        objects = ObjectCounter.create();
        generated = consume(HeapPermutations.of(PERMUTATION_SIZE, heap, objects));
        print("HeapPermutations", generated, heap.count(), objects.count());

        Counter johnson = Counter.create();
        objects = ObjectCounter.create();
        generated = consume(JohnsonTrotter.permutations(PERMUTATION_SIZE, johnson, objects));
        print("JohnsonTrotter", generated, johnson.count(), objects.count());
        checkSamePermutations();
        System.out.println();
    }

    private static void checkSamePermutations() {
        Set<String> combinatorics =
            normalized(Combinatorics.permutationsOf(PERMUTATION_SIZE, Counter.create()));
        Set<String> heap = normalized(HeapPermutations.of(PERMUTATION_SIZE, Counter.create()));
        Set<String> johnson = normalized(JohnsonTrotter.permutations(PERMUTATION_SIZE, Counter.create()));
        boolean same = combinatorics.equals(heap) && heap.equals(johnson);
        System.out.printf("Множества перестановок совпадают: %s, элементов: %d%n",
            same ? "да" : "нет", combinatorics.size());
    }

    private static Set<String> normalized(Iterable<int[]> permutations) {
        Set<String> result = new HashSet<>();
        for (int[] permutation : permutations) {
            int[] normalized = permutation.clone();
            int min = Arrays.stream(normalized).min().orElse(0);
            for (int i = 0; i < normalized.length; i++) {
                normalized[i] -= min;
            }
            result.add(Arrays.toString(normalized));
        }
        return result;
    }

    private static void compareSubsets() {
        System.out.printf("Подмножества, n = %d%n", SUBSET_SIZE);
        Counter subsets = Counter.create();
        ObjectCounter objects = ObjectCounter.create();
        int generated = consume(Subsets.of(SUBSET_SIZE, subsets, objects));
        print("Subsets", generated, subsets.count(), objects.count());

        Counter gray = Counter.create();
        objects = ObjectCounter.create();
        generated = consume(GrayCode.of(SUBSET_SIZE, gray, objects));
        print("GrayCode", generated, gray.count(), objects.count());

        Counter tuples = Counter.create();
        objects = ObjectCounter.create();
        generated = consume(Combinatorics.arrangementsWithRepetitionOf(2, SUBSET_SIZE, tuples, objects));
        print("ArrangementsWithRepetition", generated, tuples.count(), objects.count());
        System.out.println();
    }

    private static void compareCounting() {
        System.out.printf("Комбинаторные числа, n = %d, k = %d%n", N, K);
        Counter permutations = Counter.create();
        ObjectCounter objects = ObjectCounter.create();
        BigInteger value = Combinatorics.permutations(N, permutations, objects);
        print("permutations", value, permutations.count(), objects.count());

        Counter arrangements = Counter.create();
        objects = ObjectCounter.create();
        value = Combinatorics.arrangements(N, K, arrangements, objects);
        print("arrangements", value, arrangements.count(), objects.count());

        Counter combinations = Counter.create();
        objects = ObjectCounter.create();
        value = Combinatorics.combinations(N, K, combinations, objects);
        print("combinations", value, combinations.count(), objects.count());

        Counter arrangementsWithRepetition = Counter.create();
        objects = ObjectCounter.create();
        value = Combinatorics.arrangementsWithRepetition(N, K, arrangementsWithRepetition, objects);
        print("arrangementsWithRepetition", value, arrangementsWithRepetition.count(), objects.count());

        Counter combinationsWithRepetition = Counter.create();
        objects = ObjectCounter.create();
        value = Combinatorics.combinationsWithRepetition(N, K, combinationsWithRepetition, objects);
        print("combinationsWithRepetition", value, combinationsWithRepetition.count(), objects.count());
        System.out.println();
    }

    private static void compareNumbers() {
        System.out.printf("Числовые последовательности, n = %d, k = %d%n", N, K);
        Counter bell = Counter.create();
        ObjectCounter objects = ObjectCounter.create();
        BigInteger value = Bell.number(N, bell, objects);
        print("Bell", value, bell.count(), objects.count());

        Counter catalan = Counter.create();
        objects = ObjectCounter.create();
        value = Catalan.number(N, catalan, objects);
        print("Catalan", value, catalan.count(), objects.count());

        Counter derangements = Counter.create();
        objects = ObjectCounter.create();
        value = Derangements.number(N, derangements, objects);
        print("Derangements", value, derangements.count(), objects.count());

        Counter second = Counter.create();
        objects = ObjectCounter.create();
        value = Stirling.second(N, K, second, objects);
        print("Stirling.second", value, second.count(), objects.count());

        Counter first = Counter.create();
        objects = ObjectCounter.create();
        value = Stirling.first(N, K, first, objects);
        print("Stirling.first", value, first.count(), objects.count());

        Counter starsAndBars = Counter.create();
        objects = ObjectCounter.create();
        value = StarsAndBars.number(N, K, starsAndBars, objects);
        print("StarsAndBars", value, starsAndBars.count(), objects.count());
        System.out.println();
    }

    private static void compareGeneration() {
        System.out.printf("Порождение наборов, n = %d%n", N);
        Counter partitions = Counter.create();
        ObjectCounter objects = ObjectCounter.create();
        int generated = consume(Partitions.of(N, partitions, objects));
        print("Partitions", generated, partitions.count(), objects.count());

        Counter pascal = Counter.create();
        objects = ObjectCounter.create();
        List<List<BigInteger>> triangle = PascalTriangle.of(N, pascal, objects);
        print("PascalTriangle", triangle.size(), pascal.count(), objects.count());
        System.out.println();
    }

    private static void printGrowth() {
        System.out.println("Рост числа операций и объектов по n");
        System.out.printf("%-4s %-27s %-12s %-12s%n", "n", "алгоритм", "операций", "объектов");
        for (int n = 1; n <= PERMUTATION_SIZE; n++) {
            Counter counter = Counter.create();
            ObjectCounter objects = ObjectCounter.create();
            consume(Combinatorics.permutationsOf(n, counter, objects));
            printGrowthRow(n, "Combinatorics", counter.count(), objects.count());

            counter = Counter.create();
            objects = ObjectCounter.create();
            consume(HeapPermutations.of(n, counter, objects));
            printGrowthRow(n, "HeapPermutations", counter.count(), objects.count());

            counter = Counter.create();
            objects = ObjectCounter.create();
            consume(JohnsonTrotter.permutations(n, counter, objects));
            printGrowthRow(n, "JohnsonTrotter", counter.count(), objects.count());
        }
        for (int n = 1; n <= SUBSET_SIZE; n++) {
            Counter counter = Counter.create();
            ObjectCounter objects = ObjectCounter.create();
            consume(Subsets.of(n, counter, objects));
            printGrowthRow(n, "Subsets", counter.count(), objects.count());

            counter = Counter.create();
            objects = ObjectCounter.create();
            consume(GrayCode.of(n, counter, objects));
            printGrowthRow(n, "GrayCode", counter.count(), objects.count());
        }
        for (int n = 1; n <= N; n++) {
            Counter counter = Counter.create();
            ObjectCounter objects = ObjectCounter.create();
            Bell.number(n, counter, objects);
            printGrowthRow(n, "Bell", counter.count(), objects.count());

            counter = Counter.create();
            objects = ObjectCounter.create();
            Stirling.second(n, Math.max(1, n / 2), counter, objects);
            printGrowthRow(n, "Stirling.second", counter.count(), objects.count());

            counter = Counter.create();
            objects = ObjectCounter.create();
            PascalTriangle.of(n, counter, objects);
            printGrowthRow(n, "PascalTriangle", counter.count(), objects.count());

            counter = Counter.create();
            objects = ObjectCounter.create();
            consume(Partitions.of(n, counter, objects));
            printGrowthRow(n, "Partitions", counter.count(), objects.count());
        }
        System.out.println();
    }

    private static void printGrowthRow(int n, String algorithm, int operations, int objects) {
        System.out.printf("%-4d %-27s %-12d %-12d%n", n, algorithm, operations, objects);
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

    private static void print(String name, Object value, int operations, int objects) {
        System.out.printf("%-27s значение = %-12s операций = %-10d объектов = %d%n",
            name, value, operations, objects);
    }
}
