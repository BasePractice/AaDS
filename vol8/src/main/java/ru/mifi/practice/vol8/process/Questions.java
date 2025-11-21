package ru.mifi.practice.vol8.process;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static ru.mifi.practice.vol8.process.Information.parseStudents;

public abstract class Questions {
    private static final String[][] QUESTIONS = new String[][]{
        {"Двусвязный циклический", ""},
        {"Двусвязный список", ""},
        {"Односвязный список", ""},
        {"Стек на списке", ""},
        {"Стек на массиве", ""},
        {"Очередь на списке", ""},
        {"Очередь на массиве", ""},
        {"Очередь на куче", ""},
        {"BST", ""},
        {"2-3", ""},
        {"AVL", ""},
        {"Хеш таблица", ""},
        {"Обход в глубину", ""},
        {"Обход в ширину", ""},
        {"Кратчайший путь", ""},
        {"Алгоритм Дейкстры", ""},
        {"Бинарный поиск", ""},
        {"Сортировка слиянием", ""},
        {"Сортировка быстрая", ""},
        {"Джонсона-Троттера", ""},
        {"Алгоритм Хаффмана", ""},
    };

    @SuppressWarnings("checkstyle:OperatorWrap")
    static final String PREAMBLE = "" +
        "[small]#*Нельзя*:\n" +
        "1. Использовать стандартные классы входяще в `JDK` реализующие алгоритмы или структуры данных;\n" +
        "2. Пользоваться помощью `ИИ`;\n" +
        "3. Пользоваться любыми ресурсами из сети интернет.#\n" +
        "[small]#*Необходимо*:\n" +
        "1. Рассказать устно об алгоритме и/или структуре данных в задании. Дать асимптотические характеристики " +
        "алгоритму(-ам) используемым в задании;\n" +
        "2. Реализовать при помощи ЯПВУ `Java`;\n" +
        "3. Привести примеры данных позволяющие проверить работоспособность реализации и рассмотреть граничные характеристики алгоритма;\n" +
        "4. Для структуры данных реализовать набор базовых алгоритмов для нее и дать описание им в соответствии с п.1.#";

    public static void main(String[] args) throws IOException {
        var students = parseStudents("C:\\Users\\Pastor\\Downloads\\courseid_9687_participants.csv");
        var output = new File(".output/questions");
        output.mkdirs();
        for (Map.Entry<String, List<Information.Student>> entry : students.entrySet()) {
            try (BufferedWriter w = new BufferedWriter(new FileWriter(new File(output, entry.getKey() + ".adoc")))) {
                w.append("= `").append(entry.getKey()).append("`").append("\n").append(":hardbreaks-option:").append("\n").append("\n");
                w.append(PREAMBLE).append("\n").append("\n");
                w.append("[cols=\"^5%m,25%m,^40%m,^25%m,^5%m\"]").append("\n");
                w.append("|===").append("\n");
                w.append("|№|Алгоритм/Структура|Ф.И.О.|Примечание|Оц.").append("\n");
                int count = 0;
                for (String[] question : QUESTIONS) {
                    w.append("\n");
                    w.append("|").append(String.valueOf(count)).append("\n");
                    ++count;
                    w.append("|**").append(question[0]).append("**\n");
                    w.append("|").append(question[1]).append("\n");
                    w.append("|").append("\n");
                    w.append("|").append("\n");
                }
                w.append("|===").append("\n");
            }
        }
    }
}
