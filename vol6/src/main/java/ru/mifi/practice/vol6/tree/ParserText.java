package ru.mifi.practice.vol6.tree;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.function.Function;

@SuppressWarnings({"PMD.EmptyControlStatement", "PMD.CompareObjectsWithEquals"})
public final class ParserText<T> implements Tree.Loader<T> {

    public Tree<T> parse(String text, Function<String, T> value, Comparator<T> comparator) throws IOException {
        return parse(new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8)), value, comparator);
    }

    @Override
    public Tree<T> parse(InputStream stream, Function<String, T> value, Comparator<T> comparator) throws IOException {
        Tree.Standard<T> tree = new Tree.Standard<>(comparator);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                String[] parts = line.split(":");
                String owner = parts[0].trim();
                String left = "";
                String right = "";
                String description = parts[1].trim();
                if (description.isEmpty()) {
                    continue;
                } else if (description.charAt(0) == '{' && description.charAt(description.length() - 1) == '}') {
                    description = description.substring(1, description.length() - 1);
                    parts = description.split(",");
                    left = parts[0].trim();
                    right = parts[1].trim();
                }
                tree.add(value.apply(owner),
                    left.isEmpty() ? null : value.apply(left), right.isEmpty() ? null : value.apply(right));
            }
        }
        return tree;
    }
}
