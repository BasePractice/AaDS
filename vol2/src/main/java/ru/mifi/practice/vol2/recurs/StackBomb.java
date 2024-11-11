package ru.mifi.practice.vol2.recurs;

public abstract class StackBomb {

    private static int callFrame = 0;

    private static void call(int n) {
        callFrame++;
        call(n + 1);
    }

    public static void main(String[] args) {
        try {
            call(0);
        } catch (StackOverflowError error) {
            System.out.println("Frame: " + callFrame);
        }
    }
}
