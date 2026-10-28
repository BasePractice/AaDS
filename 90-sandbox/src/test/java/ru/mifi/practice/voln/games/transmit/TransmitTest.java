package ru.mifi.practice.voln.games.transmit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Transmit")
final class TransmitTest {

    @DisplayName("Standard.readText возвращает строку")
    @Test
    @Timeout(5)
    void readTextReturnsLine() {
        InputStream in = System.in;
        try {
            System.setIn(new ByteArrayInputStream("TEST\n".getBytes()));
            Transmit.Standard standard = new Transmit.Standard();
            assertThat("readText doesnt return the input line", standard.readText(), is("TEST"));
        } finally {
            System.setIn(in);
        }
    }

    @DisplayName("Standard.readText бросает исключение на исчерпании")
    @Test
    @Timeout(5)
    void readTextThrowsWhenExhausted() {
        InputStream in = System.in;
        try {
            System.setIn(new ByteArrayInputStream("TEST\n".getBytes()));
            Transmit.Standard standard = new Transmit.Standard();
            standard.readText();
            assertThrows(NoSuchElementException.class, standard::readText, "exhausted readText doesnt throw");
        } finally {
            System.setIn(in);
        }
    }

    @DisplayName("Standard.readInt возвращает число")
    @Test
    @Timeout(5)
    void readIntReturnsSingleValue() {
        InputStream in = System.in;
        try {
            System.setIn(new ByteArrayInputStream("10\n".getBytes()));
            Transmit.Standard standard = new Transmit.Standard();
            assertThat("readInt doesnt return the single value", standard.readInt().orElseThrow(), is(10));
        } finally {
            System.setIn(in);
        }
    }

    @DisplayName("Standard.readInt читает последовательные числа")
    @Test
    @Timeout(5)
    void readIntReadsConsecutiveValues() {
        InputStream in = System.in;
        try {
            System.setIn(new ByteArrayInputStream("100\n101\n102\r\n\r\n\r\n".getBytes()));
            Transmit.Standard standard = new Transmit.Standard();
            List<Integer> read = new ArrayList<>();
            for (Optional<Integer> value = standard.readInt(); value.isPresent(); value = standard.readInt()) {
                read.add(value.get());
            }
            assertThat("readInt doesnt read consecutive values until empty", read, is(List.of(100, 101, 102)));
        } finally {
            System.setIn(in);
        }
    }

    @DisplayName("Standard.print выводит текст")
    @Test
    @Timeout(5)
    void printWritesText() {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        Transmit.Standard standard = new Transmit.Standard(new PrintStream(buffer), null);
        standard.print("TEST");
        assertThat("print doesnt write the raw text", buffer.toString(), is("TEST"));
    }

    @DisplayName("Standard.print подставляет аргументы")
    @Test
    @Timeout(5)
    void printFormatsArguments() {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        Transmit.Standard standard = new Transmit.Standard(new PrintStream(buffer), null);
        standard.print("Hello %s", "World");
        assertThat("print doesnt format the arguments", buffer.toString(), is("Hello World"));
    }

    @DisplayName("Standard.println подставляет аргументы и переносит строку")
    @Test
    @Timeout(5)
    void printlnFormatsAndAppendsNewline() {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        Transmit.Standard standard = new Transmit.Standard(new PrintStream(buffer), null);
        standard.println("Hello %s", "World");
        assertThat("println doesnt append the newline", buffer.toString(), is(String.format("Hello World%n")));
    }
}
