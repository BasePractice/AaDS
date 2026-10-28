package ru.mifi.practice.voln.fsm;

import ru.mifi.practice.voln.images.Gif;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public abstract class Main {
    public static void main(String[] args) throws IOException {
        BytesMatrix matrix = BytesMatrix.random(50, 50);
        BytesMatrix.Formatter formatter = new BytesMatrixFormatter();
        try (Writer writer = new BufferedWriter(new FileWriter("matrix.txt"))) {
            formatter.write(matrix, writer);
        }
        try (Reader reader = new FileReader("matrix.txt")) {
            matrix = formatter.read(reader).orElseThrow();
        }
        GoL g = new GoL(matrix);
        List<BufferedImage> frames = new ArrayList<>();
        BytesMatrixImage.ByteMatrixConfiguration configuration = new BytesMatrixImage.ByteMatrixConfiguration();
        frames.add(BytesMatrixImage.createImage(0, matrix, configuration));
        for (int i = 0; i < 300; i++) {
            g.tick((tick, bytesMatrix) -> frames.add(BytesMatrixImage.createImage(tick, bytesMatrix, configuration)));
        }
        Gif.create2(frames, "GoL.gif", 300, true);
    }
}
