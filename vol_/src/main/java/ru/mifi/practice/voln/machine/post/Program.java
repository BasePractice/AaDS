package ru.mifi.practice.voln.machine.post;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

public interface Program {

    void execute(MechanicalHead head);

    final class Default implements Program {
        private final List<Execute.Code> codes;

        public Default(String codeText, Execute.Information... executes) throws IOException {
            this.codes = new Compiler.Default(executes).compile(new StringReader(codeText));
        }

        @Override
        public void execute(MechanicalHead head) {
            for (int i = 0; i < codes.size() && !head.isStopped(); ) {
                Execute.Code code = codes.get(i);
                i = code.execute(i, head);
            }
        }

        @Override
        public String toString() {
            return String.valueOf(codes);
        }
    }
}
