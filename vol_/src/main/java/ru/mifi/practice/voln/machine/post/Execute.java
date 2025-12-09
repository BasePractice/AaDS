package ru.mifi.practice.voln.machine.post;

import lombok.NonNull;

public interface Execute {
    int execute(int index, MechanicalHead mechanicalHead, int... args);

    interface Information {

        char getSymbol();

        int getArguments();

        Execute of();
    }

    record Code(Execute execute, int[] args) {
        public int execute(int index, MechanicalHead head) {
            return execute.execute(index, head, args);
        }

        @NonNull
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < args.length; i++) {
                if (i > 0) {
                    builder.append(", ");
                }
                builder.append(args[i]);
            }
            return execute + (builder.isEmpty() ? "" : "(" + builder + ")");
        }
    }
}
