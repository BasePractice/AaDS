package ru.mifi.practice.voln.fsm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Optional;

public final class BytesMatrixFormatter implements BytesMatrix.Formatter {
    static String byteToHex(byte num) {
        char[] hexDigits = new char[2];
        hexDigits[0] = Character.forDigit((num >> 4) & 0xF, 16);
        hexDigits[1] = Character.forDigit(num & 0xF, 16);
        return new String(hexDigits);
    }

    static String hexString(byte[] byteArray) {
        StringBuilder buffer = new StringBuilder();
        for (byte b : byteArray) {
            buffer.append(byteToHex(b));
        }
        return buffer.toString();
    }

    private static byte[] stringHex(String hexString) {
        if (hexString.length() % 2 == 1) {
            throw new IllegalArgumentException(
                "Invalid hexadecimal String supplied.");
        }

        byte[] bytes = new byte[hexString.length() / 2];
        for (int i = 0; i < hexString.length(); i += 2) {
            bytes[i / 2] = hexToByte(hexString.substring(i, i + 2));
        }
        return bytes;
    }

    private static byte hexToByte(String hexString) {
        int firstDigit = toDigit(hexString.charAt(0));
        int secondDigit = toDigit(hexString.charAt(1));
        return (byte) ((firstDigit << 4) + secondDigit);
    }

    private static int toDigit(char hexChar) {
        int digit = Character.digit(hexChar, 16);
        if (digit == -1) {
            throw new IllegalArgumentException(
                "Invalid Hexadecimal Character: " + hexChar);
        }
        return digit;
    }

    @Override
    public void write(BytesMatrix matrix, Writer writer) throws IOException {
        writer.append(String.format("%d,%d%n", matrix.rows(), matrix.cols()));
        byte[] line = new byte[matrix.cols()];
        for (int row = 0; row < matrix.rows(); row++) {
            matrix.readAt(row, line);
            writer.append(String.format("%s%n", hexString(line)));
        }
        writer.flush();
    }

    @Override
    public Optional<BytesMatrix> read(Reader reader) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(reader);
        String line = bufferedReader.readLine();
        if (line == null) {
            return Optional.empty();
        }
        String[] parts = line.split(",");
        int rows = Integer.parseInt(parts[0]);
        int cols = Integer.parseInt(parts[1]);
        BytesMatrix matrix = BytesMatrix.defaults(rows, cols);
        for (int row = 0; row < rows; row++) {
            line = bufferedReader.readLine();
            byte[] bytes = stringHex(line);
            if (bytes.length != cols) {
                throw new IllegalArgumentException();
            }
            matrix.writeAt(row, bytes);
        }
        return Optional.of(matrix);
    }
}
