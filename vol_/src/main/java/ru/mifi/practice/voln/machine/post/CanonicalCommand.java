package ru.mifi.practice.voln.machine.post;

import lombok.Getter;

@Getter
public enum CanonicalCommand implements Execute, Execute.Information {
    LABEL_PUT('V', 1),
    LABEL_DELETE('X', 1),
    MOVE_LEFT('<', 1),
    MOVE_RIGHT('>', 1),
    IF('?', 2),
    STOP('!', 0);
    final char symbol;
    final int arguments;

    CanonicalCommand(char symbol, int arguments) {
        this.symbol = symbol;
        this.arguments = arguments;
    }

    @Override
    public int execute(int index, MechanicalHead mechanicalHead, int... args) {
        return switch (this) {
            case LABEL_PUT -> mechanicalHead.doLabel(MechanicalHead.LabelOperation.PUT, args[0]);
            case LABEL_DELETE -> mechanicalHead.doLabel(MechanicalHead.LabelOperation.DELETE, args[0]);
            case MOVE_LEFT -> mechanicalHead.step(MechanicalHead.Step.LEFT, args[0]);
            case MOVE_RIGHT -> mechanicalHead.step(MechanicalHead.Step.RIGHT, args[0]);
            case IF -> {
                boolean label = mechanicalHead.hasLabel();
                int next;
                if (label) {
                    next = mechanicalHead.gotoLine(args[1]);
                } else {
                    next = mechanicalHead.gotoLine(args[0]);
                }
                yield next;
            }
            case STOP -> mechanicalHead.stop();
        };
    }

    @Override
    public Execute of() {
        return this;
    }
}
