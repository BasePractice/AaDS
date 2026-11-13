package ru.mifi.practice.vol92.sync;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Комната с tryLock")
final class TryLockRoomTest {

    @DisplayName("Занятая дверь разворачивает поток, свободная — пускает")
    @Test
    @Timeout(30)
    void rejectsBusyDoorAndAcceptsFreeDoor() throws InterruptedException {
        TryLockRoom room = new TryLockRoom();
        Thread holder = new Thread(() -> room.visit(100, TimeUnit.MILLISECONDS));
        holder.start();
        Thread.sleep(20);
        assertThat("busy door lets the visitor in",
            room.visit(10, TimeUnit.MILLISECONDS), is(false));
        holder.join(5000);
        assertThat("free door rejects the visitor",
            room.visit(10, TimeUnit.MILLISECONDS), is(true));
        assertThat("room loses track of visits",
            room.visits(), is(2));
        assertThat("room loses track of denials",
            room.denials(), is(1));
    }
}
