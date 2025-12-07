package ru.mifi.practice.voln.fsm;

import lombok.Builder;
import lombok.experimental.UtilityClass;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.util.function.Function;

@UtilityClass
public class BytesMatrixImage {

    public BufferedImage createImage(int tick, BytesMatrix matrix, ByteMatrixConfiguration configuration) {
        BufferedImage result = new BufferedImage(matrix.cols() * configuration.weight(),
            matrix.rows() * configuration.weight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = result.createGraphics();
//        g.setComposite(AlphaComposite.Clear);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, result.getWidth(), result.getHeight());
//        g.setComposite(AlphaComposite.SrcOver);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        final Stroke lastStroke = g.getStroke();
        g.setStroke(new BasicStroke(configuration.thickness()));
        final Color lastColor = g.getColor();
        g.setColor(configuration.gridColor());
        for (int y = 0; y < matrix.rows(); y++) {
            for (int x = 0; x < matrix.cols(); x++) {
                byte v = matrix.get(x, y);
                if (v != 0) {
                    Color c = g.getColor();
                    g.setColor(configuration.btc().apply(v));
                    g.fillRect(x * configuration.weight(), y * configuration.weight(),
                        configuration.weight(), configuration.weight());
                    g.setColor(c);
                }
                g.drawRect(x * configuration.weight(), y * configuration.weight(),
                    configuration.weight(), configuration.weight());
            }
        }
        g.drawRect(0, 0, matrix.cols() * configuration.weight() - configuration.thickness(),
            matrix.rows() * configuration.weight() - configuration.thickness());
        g.setStroke(lastStroke);
        g.setColor(lastColor);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Fira Code", Font.BOLD, 24));
        g.drawString(String.format("%04d", tick), configuration.thickness(), configuration.thickness() + g.getFontMetrics().getHeight());
        return result;
    }

    @Builder(toBuilder = true)
    public record ByteMatrixConfiguration(int weight, int thickness, Color gridColor, Function<Byte, Color> btc) {
        public ByteMatrixConfiguration() {
            this(10, 1, Color.DARK_GRAY, v -> v == 1 ? Color.RED : Color.WHITE);
        }
    }
}
