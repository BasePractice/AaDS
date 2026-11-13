package ru.mifi.practice.vol92.race;

/** Записи до join видны после join: happens-before без volatile. */
public final class HappensBefore implements Runnable {
    private int answer;
    private boolean ready;

    @Override
    public void run() {
        answer = 42;
        ready = true;
    }

    public int answer() {
        return answer;
    }

    public boolean ready() {
        return ready;
    }
}
