package ru.mifi.practice.vol2.vm;

import ru.mifi.practice.vol2.vm.impl.PolskaMachine;

import java.io.IOException;

public abstract class Main {
    public static void main(String[] args) throws IOException {
        VirtualMachine machine = new PolskaMachine();
        String input = "1 10 - abs";
        System.out.println("Input    : \"" + input + "\"");
        VirtualMachine.Binary binary = machine.compile(input);
        System.out.println("Binary   : 0x" + binary);
        VirtualMachine.Value compiled = machine.eval(binary, VirtualMachine.Context.newContext());
        System.out.println("Compiled : " + compiled);
        VirtualMachine.Value interpret = machine.eval(input, VirtualMachine.Context.newContext());
        System.out.println("Interpret: " + interpret);

    }
}
