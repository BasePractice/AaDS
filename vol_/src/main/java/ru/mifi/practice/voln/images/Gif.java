package ru.mifi.practice.voln.images;

import com.squareup.gifencoder.GifEncoder;
import com.squareup.gifencoder.ImageOptions;
import lombok.experimental.UtilityClass;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("PMD.EmptyCatchBlock")
@UtilityClass
public class Gif {

    public void create2(Iterable<BufferedImage> frames, String outputPath, int delayMs, boolean loop) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(outputPath)) {
            GifEncoder encoder = null;
            ImageOptions options = new ImageOptions();
            options.setDelay(delayMs, TimeUnit.MILLISECONDS);
            for (BufferedImage frame : frames) {
                if (encoder == null) {
                    encoder = new GifEncoder(fos, frame.getWidth(), frame.getHeight(), loop ? 0 : 1);
                }
                int[] data = new int[frame.getWidth() * frame.getHeight()];
                frame.getRGB(0, 0, frame.getWidth(), frame.getHeight(), data, 0, frame.getWidth());
                encoder.addImage(data, frame.getWidth(), options);
            }
            if (encoder != null) {
                encoder.finishEncoding();
            }
        }
    }

    public void create(Iterable<BufferedImage> frames, String outputPath, int delayMs, boolean loop) throws IOException {
        ImageWriter writer = ImageIO.getImageWritersByFormatName("gif").next();

        try (ImageOutputStream output = ImageIO.createImageOutputStream(new File(outputPath))) {
            writer.setOutput(output);
            writer.prepareWriteSequence(null);
            ImageWriteParam params = writer.getDefaultWriteParam();
            ImageTypeSpecifier imageType = ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_ARGB);
            for (BufferedImage frame : frames) {
                IIOMetadata frameMetadata = createFrameMetadata(writer, imageType, delayMs);
                IIOImage frameImage = new IIOImage(frame, null, frameMetadata);
                writer.writeToSequence(frameImage, params);
            }

            writer.endWriteSequence();
        } finally {
            try {
                writer.dispose();
            } catch (Exception ex) {
                //Nothing
            }
        }
        if (loop) {
            addLoopExtension(outputPath);
        }
    }

    private IIOMetadata createFrameMetadata(ImageWriter writer, ImageTypeSpecifier imageType, int delayMs)
        throws IIOInvalidTreeException {
        IIOMetadata metadata = writer.getDefaultImageMetadata(imageType, null);
        String formatName = metadata.getNativeMetadataFormatName();
        IIOMetadataNode root = (IIOMetadataNode) metadata.getAsTree(formatName);
        IIOMetadataNode graphicsControlExtension = getNode(root, "GraphicControlExtension");
        graphicsControlExtension.setAttribute("disposalMethod", "none");
        graphicsControlExtension.setAttribute("userInputFlag", "FALSE");
        int delayInHundredths = Math.max(delayMs / 10, 1);
        graphicsControlExtension.setAttribute("delayTime", String.valueOf(delayInHundredths));
        graphicsControlExtension.setAttribute("transparentColorFlag", "FALSE");
        metadata.setFromTree(formatName, root);
        return metadata;
    }

    static void addLoopExtension(String outputPath) throws IOException {
        byte[] gifData = Files.readAllBytes(Paths.get(outputPath));

        try (FileOutputStream fos = new FileOutputStream(outputPath)) {
            fos.write(gifData);
            // Добавляем Netscape Extension (бесконечное повторение)
            fos.write(0x21); // Extension introducer
            fos.write(0xFF); // Application extension label
            fos.write(0x0B); // Block size
            fos.write("NETSCAPE2.0".getBytes()); // Application identifier
            fos.write(0x03); // Sub-block size
            fos.write(0x01); // Sub-block ID
            fos.write(0x00); // Loop count (0 = бесконечно)
            fos.write(0x00); // Loop count продолжение
            fos.write(0x00); // Block terminator
            fos.write(0x3B); // GIF trailer
        }
    }

    private IIOMetadataNode getNode(IIOMetadataNode rootNode, String nodeName) {
        for (int i = 0; i < rootNode.getLength(); i++) {
            if (rootNode.item(i).getNodeName().equalsIgnoreCase(nodeName)) {
                return (IIOMetadataNode) rootNode.item(i);
            }
        }
        IIOMetadataNode node = new IIOMetadataNode(nodeName);
        rootNode.appendChild(node);
        return node;
    }
}
