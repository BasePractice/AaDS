package ru.mifi.practice.vol1.bigo;

/**
 * Стенд сложности раздела 1: одна и та же цепочка размеров прогоняется через нагрузки известных
 * классов, и рядом печатается число шагов. Разница между O(1) и O(n²) на растущем входе видна
 * глазами, а не выводится из формулы.
 */
public abstract class Main {

    public static void main(String[] args) {
        for (Workload.Type type : Workload.Type.values()) {
            System.out.println(type);
            System.out.print(new Measurement(type, 64, 128, 256, 512, 1024));
        }
    }
}
