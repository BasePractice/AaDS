package ru.mifi.practice.vol3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * В коде есть ошибка
 *
 * @param <E> сортируемый элемент
 */
public final class QuickSort<E extends Comparable<E>> implements Sort<E> {
    private final Strategy<E> strategy;

    public QuickSort(Strategy<E> strategy) {
        this.strategy = strategy;
    }

    public QuickSort() {
        this(new Strategy.Halfway<>());
    }


    @Override
    public List<E> sort(List<E> array, Counter counter, boolean debug) {
        if (debug) {
            System.out.println("Input  : " + Arrays.toString(array.toArray()));
        }
        List<E> sortable = new ArrayList<>(array);
        quickSort(sortable, 0, sortable.size(), counter, debug);
        if (debug) {
            System.out.println("Output : " + Arrays.toString(sortable.toArray()));
        }
        return sortable;
    }

    private void quickSort(List<E> array, int low, int high, Counter counter, boolean debug) {
        if (low >= high - 1) {
            return;
        }
        int middle = part(array, low, high, counter, debug);
        quickSort(array, low, middle, counter, debug);
        quickSort(array, middle + 1, high, counter, debug);
    }

    private int part(List<E> array, int low, int high, Counter counter, boolean debug) {
        return strategy.part(array, low, high, counter, debug);
    }

    public sealed interface Strategy<E extends Comparable<E>> {
        int part(List<E> array, int low, int high, Counter counter, boolean debug);

        final class Randomly<E extends Comparable<E>> implements Strategy<E> {
            private final Random random = new Random(new Date().getTime());

            @Override
            public int part(List<E> array, int low, int high, Counter counter, boolean debug) {
                int index = random.nextInt(high - low) + low;
                E baseElement = array.get(index);
                Collections.swap(array, index, high - 1);
                if (debug) {
                    System.out.println("Index  : [" + index + "] = " + baseElement);
                    System.out.println("Range  : " + low + " - " + high);
                    System.out.println(Arrays.toString(array.toArray()));
                }
                int i = low;
                int j = high - 1;
                while (i < j) {
                    while (i < j && array.get(i).compareTo(baseElement) < 0) {
                        i++;
                        counter.increment();
                        if (debug) {
                            System.out.println("Equals : array[" + i + "] < " + baseElement + "; "
                                + array.get(i) + " < " + baseElement + " -> " + (array.get(i).compareTo(baseElement) < 0));
                        }
                    }
                    while (i < j && array.get(j).compareTo(baseElement) >= 0) {
                        j--;
                        counter.increment();
                        if (debug) {
                            System.out.println("Equals : array[" + j + "] > " + baseElement + "; "
                                + array.get(j) + " > " + baseElement + " -> " + (array.get(j).compareTo(baseElement) > 0));
                        }
                    }
                    if (i < j) {
                        Collections.swap(array, i, j);
                        if (debug) {
                            System.out.println(Arrays.toString(array.toArray()));
                        }
                    }
                }
                Collections.swap(array, i, high - 1);
                if (debug) {
                    System.out.println(Arrays.toString(array.toArray()));
                }
                if (debug) {
                    System.out.println("Middle : " + i);
                }
                return i;
            }
        }

        final class Halfway<E extends Comparable<E>> implements Strategy<E> {

            @Override
            public int part(List<E> array, int low, int high, Counter counter, boolean debug) {
                int index = low + (high - low) / 2;
                E baseElement = array.get(index);
                if (debug) {
                    System.out.println("Index  : [" + index + "] = " + baseElement);
                    System.out.println("Range  : " + low + " - " + high);
                    System.out.println(Arrays.toString(array.toArray()));
                }
                int i = low - 1;
                int j = high;
                while (true) {
                    do {
                        ++i;
                        counter.increment();
                        if (debug) {
                            System.out.println("Equals : array[" + i + "] < " + baseElement + "; "
                                + array.get(i) + " < " + baseElement + " -> " + (array.get(i).compareTo(baseElement) < 0));
                        }
                    } while (array.get(i).compareTo(baseElement) < 0);
                    do {
                        --j;
                        counter.increment();
                        if (debug) {
                            System.out.println("Equals : array[" + j + "] > " + baseElement + "; "
                                + array.get(j) + " > " + baseElement + " -> " + (array.get(j).compareTo(baseElement) > 0));
                        }
                    } while (array.get(j).compareTo(baseElement) > 0);
                    if (i >= j) {
                        if (debug) {
                            System.out.println("Middle : " + i);
                        }
                        return j;
                    }
                    Collections.swap(array, i, j);
                    if (debug) {
                        System.out.println(Arrays.toString(array.toArray()));
                    }
                }
            }
        }
    }
}
