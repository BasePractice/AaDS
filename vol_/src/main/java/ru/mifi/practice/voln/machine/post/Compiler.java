package ru.mifi.practice.voln.machine.post;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface Compiler {

    List<Execute.Code> compile(Reader reader) throws IOException;

    final class Default implements Compiler {
        private final Map<Character, Execute.Information> executes;

        public Default(Execute.Information... executes) {
            this.executes = Stream.of(executes).collect(Collectors.toMap(Execute.Information::getSymbol, e -> e));
        }

        @Override
        public List<Execute.Code> compile(Reader reader) throws IOException {
            BufferedReader bufferedReader = new BufferedReader(reader);
            List<Execute.Code> codes = new ArrayList<>();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                line = line.trim();
                char c = line.charAt(0);
                Execute.Information information = this.executes.get(c);
                if (information == null) {
                    throw new IllegalStateException();
                }
                int[] args = new int[information.getArguments()];
                line = line.substring(1).trim();
                String[] parts = line.isEmpty() ? new String[0] : line.split(";");
                if (parts.length != args.length) {
                    throw new IllegalStateException();
                }
                for (int i = 0; i < parts.length; i++) {
                    args[i] = Integer.parseInt(parts[i]);
                }
                codes.add(new Execute.Code(information.of(), args));
            }
            return codes;
        }
    }
}
