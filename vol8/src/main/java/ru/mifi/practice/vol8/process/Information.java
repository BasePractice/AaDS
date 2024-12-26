package ru.mifi.practice.vol8.process;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public abstract class Information {
    private static final Set<String> ACCEPTING = Set.of("КАБО-01-23", "КАБО-02-23", "КВБО-01-23");

    public record Student(String code, String group, String fio) implements Comparable<Student> {
        @Override
        public int compareTo(Student o) {
            return code.compareTo(o.code);
        }
    }

    static Map<String, List<Student>> parseStudents(String fileName) throws IOException {
        return parseStudents(fileName, ACCEPTING);
    }

    static Map<String, List<Student>> parseStudents(String fileName, Set<String> accepting) throws IOException {
        Map<String, List<Student>> students = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                String io = values[0].trim().replaceAll("\"", "");
                String f = values[1].trim().replaceAll("\"", "");
                String code = values[2].trim().toUpperCase(Locale.ROOT).replaceAll("\"", "")
                    .replace('К', 'K').replace('Л', 'L').replace('Р', 'R');
                String group = values[4].trim().toUpperCase(Locale.ROOT).replaceAll("\"", "");
                if (!accepting.contains(group)) {
                    continue;
                }
                if (!code.isEmpty()) {
                    String reduced = String.join(".", io.chars().filter(Character::isUpperCase).mapToObj(Character::toString).reduce("",
                        (a, b) -> a + b, (a, b) -> a + b).split("")) + ".";
                    students.computeIfAbsent(group, k -> new ArrayList<>())
                        .add(new Student(code, group, String.format("%s %s", f, reduced)));
                }
            }
        }
        students.forEach((k, v) -> v.sort(Student::compareTo));
        return students;
    }

    public static void main(String[] args) throws IOException {
        String fileName = "C:\\Users\\Pastor\\Downloads\\courseid_9687_participants.csv";
        String output = ".output";
        Statistics statistics = new Statistics.Default("E:\\GitHub\\algorithms-and-data-structures-2024\\students");
        final Map<String, Statistics.Information> scanned = statistics.scan();
        Map<String, List<Student>> students = parseStudents(fileName, ACCEPTING);
        File outputFile = new File(output);
        outputFile.mkdirs();
        for (Map.Entry<String, List<Student>> entry : students.entrySet()) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File(outputFile, entry.getKey() + ".adoc")))) {
                bw.append(":stem: latexmath").append("\n\n");
                bw.append("= `").append(entry.getKey()).append("`\n").append("\n");
                bw.append("**Данные: общее количество файлов / общее количество уникальных строк / из них дублей**").append("\n").append("\n");
                bw.append("[cols=\"^1m,^2m,3m,^3m,1m,3m\"]").append("\n");
                bw.append("|===").append("\n");
                bw.append("|№|Код|ФИО|Данные|Зачет|Примечание").append("\n");
                int count = 1;
                for (Student student : entry.getValue()) {
                    bw.append("\n");
                    bw.append("|").append(String.valueOf(count++)).append("\n");
                    bw.append("|**").append(student.code).append("**\n");
                    bw.append("|").append(student.fio).append("\n");
                    Statistics.Information information = scanned.get(student.code.toUpperCase(Locale.ROOT));
                    if (information != null) {
                        bw.append("|").append(String.valueOf(information.files())).append("/")
                            .append(String.valueOf(information.lines())).append("/")
                            .append(String.valueOf(information.duplicates()))
                            .append("\n");
                        bw.append("|").append("\n");
                        if (information.lines() / 2 < information.duplicates()) {
                            bw.append("|дубли").append("\n");
                        } else {
                            bw.append("|").append("\n");
                        }
                    } else {
                        bw.append("|").append("\n");
                        bw.append("|").append("\n");
                        bw.append("|").append("\n");
                    }
                }
                bw.append("\n");
                bw.append("|===").append("\n");
            }
        }
        System.out.println("Всего уникальных строк: " + statistics.uniqueLines().size());
    }
}
