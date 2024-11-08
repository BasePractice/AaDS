package ru.mifi.practice.vol2.vm;

import java.io.IOException;

public abstract class Main {
    public static void main(String[] args) throws IOException {
        VirtualMachine machine = new VirtualMachine.Default();
        String input = "1 10 - abs";
        VirtualMachine.Binary binary = machine.compile(input);
        System.out.println(binary);
        VirtualMachine.Value compiled = machine.eval(binary, VirtualMachine.Context.newContext());
        System.out.println(compiled);
        VirtualMachine.Value interpret = machine.eval(input, VirtualMachine.Context.newContext());
        System.out.println(interpret);

    }
}
