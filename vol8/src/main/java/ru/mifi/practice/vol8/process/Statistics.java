package ru.mifi.practice.vol8.process;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

public interface Statistics {
    Map<String, Information> scan() throws IOException;

    Set<String> uniqueLines();

    record Information(int files, long lines, long duplicates) {

    }

    final class Default implements Statistics {
        private final Set<String> lines = new HashSet<>();
        private final File root;

        public Default(String filePath) {
            this.root = new File(filePath);
        }

        @Override
        public Set<String> uniqueLines() {
            return lines;
        }

        @Override
        public Map<String, Information> scan() throws IOException {
            File[] files = root.listFiles(File::isDirectory);
            if (files == null) {
                return Map.of();
            }
            Map<String, Information> map = new HashMap<>();
            for (File file : files) {
                String name = file.getName();
                var information = scan(file);
                map.put(name.toUpperCase(Locale.ROOT), information);
            }
            return map;
        }

        private Information scan(File file) throws IOException {
            AtomicInteger files = new AtomicInteger(0);
            AtomicLong duplicates = new AtomicLong(0);
            Set<String> uniqueLines = new HashSet<>();
            try (Stream<Path> stream = Files.walk(file.toPath())) {
                stream.filter(Files::isRegularFile).filter(path -> path.toString().toLowerCase(Locale.ROOT).endsWith(".java"))
                    .forEach(path -> {
                        files.getAndIncrement();
                        parse(path, duplicates, uniqueLines);
                    });
            }
            return new Information(files.get(), uniqueLines.size(), duplicates.get());
        }

        private void parse(Path path, AtomicLong duplicates, Set<String> uniqueLines) {
            try (var reader = Files.newBufferedReader(path)) {
                reader.lines().map(String::trim).map(line -> line.toLowerCase(Locale.ROOT))
                    .filter(line -> line.length() > 2)
                    .filter(line -> !line.startsWith("import") && !line.startsWith("class") && !line.contains("main("))
                    .forEach(line -> {
                        HashCode hashCode = Hashing.sha512().hashString(line, StandardCharsets.UTF_8);
                        String hash = hashCode.toString();
                        if (uniqueLines.contains(hash)) {
                            duplicates.incrementAndGet();
                        } else {
                            uniqueLines.add(hash);
                            this.lines.add(hash);
                        }
                    });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
