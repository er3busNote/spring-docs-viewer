package com.docs.viewer.global.common.utils;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class ImageUtil {

    /**
     * Image 수직으로 이어 붙이기 메서드
     */
    public static ByteArrayResource mergeImagesVertically(List<ByteArrayResource> imageResources) throws Exception {
        List<BufferedImage> bufferedImages = new ArrayList<>();
        int totalHeight = 0;
        int maxWidth = 0;

        for (ByteArrayResource resource : imageResources) {
            try (InputStream imageInputStream = resource.getInputStream()) {
                BufferedImage image = ImageIO.read(imageInputStream);
                bufferedImages.add(image);
                totalHeight += image.getHeight();
                maxWidth = Math.max(maxWidth, image.getWidth());
            }
        }

        BufferedImage combinedImage = new BufferedImage(maxWidth, totalHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = combinedImage.createGraphics();
        g2d.setPaint(Color.WHITE);
        g2d.fillRect(0, 0, maxWidth, totalHeight);

        int yOffset = 0;
        for (BufferedImage image : bufferedImages) {
            g2d.drawImage(image, 0, yOffset, null);
            yOffset += image.getHeight();
        }
        g2d.dispose();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(combinedImage, "png", outputStream);
        return new ByteArrayResource(outputStream.toByteArray());
    }
}
