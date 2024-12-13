package ru.mifi.practice.vol7.backpack;

import java.util.ArrayList;
import java.util.List;

public interface Backpack {

    int maxWeight();

    List<Item> putting(List<Item> items);

    record Item(String name, int weight, int needed) {
        @Override
        public String toString() {
            return name;
        }
    }

    record Classic(int maxWeight) implements Backpack {

        @Override
        public List<Item> putting(List<Item> items) {
            State[][] states = new State[items.size() + 1][maxWeight + 1];
            for (int i = 0; i < states.length; i++) {
                for (int j = 0; j < states[i].length; j++) {
                    if (i == 0 || j == 0) {
                        states[i][j] = new State(List.of());
                    } else if (i == 1) {
                        /* NOTICE: Кладем в зависимости от веса */
                        states[i][j] = items.get(0).weight() < j ? new State(List.of(items.get(0))) : new State(List.of());
                    } else {
                        Item item = items.get(i - 1);
                        if (item.weight() > j) {
                            //NOTICE: Если предмет не влезает, записываем предыдущий
                            states[i][j] = states[i - 1][j];
                        } else {
                            int needed = item.needed() + states[i - 1][j - item.weight()].needed();
                            if (states[i - 1][j].needed() > needed) {
                                //NOTICE: Если предыдущий максимум больше, оставляем его
                                states[i][j] = states[i - 1][j];
                            } else {
                                //NOTICE: Записываем новый максимум
                                List<Item> list = new ArrayList<>(states[i - 1][j - item.weight()].items());
                                list.add(item);
                                states[i][j] = new State(list);
                            }
                        }
                    }
                }
            }
            return states[states.length - 1][states[states.length - 1].length - 1].items();
        }

        private record State(List<Item> items) {
            public int needed() {
                return items.stream().mapToInt(Item::needed).sum();
            }
        }
    }
}
