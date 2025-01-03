package ru.mifi.practice.voln.transmit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Transmit")
class TransmitTest {
    @DisplayName("Standard.readText")
    @Test
    void standardReadText() {
        InputStream in = System.in;
        try {
            System.setIn(new ByteArrayInputStream(("TEST\n").getBytes()));
            Transmit.Standard standard = new Transmit.Standard();
            assertEquals("TEST", standard.readText());
            assertThrows(NoSuchElementException.class, standard::readText);
        } finally {
            System.setIn(in);
        }
    }

    @DisplayName("Standard.readInt")
    @Test
    void standardReadInt() {
        InputStream in = System.in;
        try {
            System.setIn(new ByteArrayInputStream(("10\n").getBytes()));
            Transmit.Standard standard = new Transmit.Standard();
            assertEquals(10, standard.readInt().orElseThrow());
            System.setIn(new ByteArrayInputStream(("100\n101\n102\r\n\r\n\r\n").getBytes()));
            standard = new Transmit.Standard();
            assertEquals(100, standard.readInt().orElseThrow());
            assertEquals(101, standard.readInt().orElseThrow());
            assertEquals(102, standard.readInt().orElseThrow());
            assertFalse(standard.readInt().isPresent());
        } finally {
            System.setIn(in);
        }
    }

    @DisplayName("Standard.print")
    @Test
    void standardPrint() {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream output = new PrintStream(buffer);
        Transmit.Standard standard = new Transmit.Standard(output, null);
        standard.print("TEST");
        assertEquals("TEST", buffer.toString());
        buffer.reset();
        standard.print("Hello %s", "World");
        assertEquals("Hello World", buffer.toString());
        buffer.reset();
        standard.println("Hello %s", "World");
        assertEquals(String.format("Hello World%n"), buffer.toString());
    }
}
