package com.docs.viewer.common.preview.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

@Slf4j
@Service
public class PreviewService {

    public ByteArrayResource convertPptxToImage(ByteArrayResource pptxResource) throws Exception {
        try (InputStream inputStream = pptxResource.getInputStream();
             XMLSlideShow ppt = new XMLSlideShow(inputStream)) {

            Dimension slideSize = ppt.getPageSize();
            int slideWidth = slideSize.width;
            int slideHeight = slideSize.height;
            int totalHeight = slideHeight * ppt.getSlides().size();

            BufferedImage fullImage = new BufferedImage(slideWidth, totalHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = fullImage.createGraphics();
            g2d.setPaint(Color.white);
            g2d.fillRect(0, 0, slideWidth, totalHeight);

            int yOffset = 0;
            for (XSLFSlide slide : ppt.getSlides()) {
                BufferedImage slideImage = new BufferedImage(slideWidth, slideHeight, BufferedImage.TYPE_INT_ARGB);
                Graphics2D slideGraphics = slideImage.createGraphics();
                slideGraphics.setPaint(Color.white);
                slideGraphics.fillRect(0, 0, slideWidth, slideHeight);
                slide.draw(slideGraphics);
                g2d.drawImage(slideImage, 0, yOffset, null);
                yOffset += slideHeight;
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(fullImage, "png", baos);
            return new ByteArrayResource(baos.toByteArray());
        }
    }
}
