package com.example.backend.utils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

public final class ImageUtil {

    private ImageUtil() {
    }

    public static BufferedImage readImage(MultipartFile file) {
        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null) {
                throw new RuntimeException("Invalid image file");
            }
            return image;
        } catch (IOException e) {
            throw new RuntimeException("Cannot read image", e);
        }
    }

    public static BufferedImage resize(BufferedImage original, int maxWidth) {
        if (original.getWidth() <= maxWidth) {
            return original;
        }

        int newHeight = (int) ((double) maxWidth / original.getWidth() * original.getHeight());

        BufferedImage resized = new BufferedImage(
                maxWidth,
                newHeight,
                BufferedImage.TYPE_INT_RGB
        );

        Graphics2D g = resized.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(original, 0, 0, maxWidth, newHeight, Color.WHITE, null);
        g.dispose();

        return resized;
    }

    private static BufferedImage toRgb(BufferedImage input) {
        if (input.getType() == BufferedImage.TYPE_INT_RGB) {
            return input;
        }

        BufferedImage rgb = new BufferedImage(
                input.getWidth(),
                input.getHeight(),
                BufferedImage.TYPE_INT_RGB
        );

        Graphics2D g = rgb.createGraphics();
        g.drawImage(input, 0, 0, Color.WHITE, null);
        g.dispose();

        return rgb;
    }

    public static byte[] compressJpeg(BufferedImage image, float quality) {
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
        if (!writers.hasNext()) {
            throw new RuntimeException("No JPEG writer available");
        }

        ImageWriter writer = writers.next();

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ImageOutputStream ios = ImageIO.createImageOutputStream(baos)) {

            BufferedImage rgbImage = toRgb(image);

            ImageWriteParam param = writer.getDefaultWriteParam();
            if (param.canWriteCompressed()) {
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(quality);
            }

            writer.setOutput(ios);
            writer.write(null, new IIOImage(rgbImage, null, null), param);
            ios.flush();

            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("JPEG compression failed", e);
        } finally {
            writer.dispose();
        }
    }
}