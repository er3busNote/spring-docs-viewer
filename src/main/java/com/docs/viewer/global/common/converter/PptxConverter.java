package com.docs.viewer.global.common.converter;

import com.docs.viewer.global.common.utils.ImageUtil;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
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
public class PptxConverter {

    public ByteArrayResource convertPptxToImage(ByteArrayResource pptxResource) throws Exception {
        List<ByteArrayResource> pptxImages = convertPptxToImageResources(pptxResource);
        return ImageUtil.mergeImagesVertically(pptxImages);
    }

    public List<ByteArrayResource> createPptxToImages(ByteArrayResource pptxResource) throws Exception {
        return convertPptxToImageResources(pptxResource);
    }

    /**
     * PPTX → 슬라이드 Image 리스트
     */
    private static List<ByteArrayResource> convertPptxToImageResources(ByteArrayResource pptxResource) throws Exception {
        List<ByteArrayResource> imageResources = new ArrayList<>();

        try (InputStream inputStream = pptxResource.getInputStream();
             XMLSlideShow ppt = new XMLSlideShow(inputStream)) {

            Dimension slideSize = ppt.getPageSize();
            int slideWidth = slideSize.width;
            int slideHeight = slideSize.height;

            for (XSLFSlide slide : ppt.getSlides()) {
                BufferedImage slideImage = new BufferedImage(slideWidth, slideHeight, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = slideImage.createGraphics();
                g2d.setPaint(Color.white);
                g2d.fillRect(0, 0, slideWidth, slideHeight);
                slide.draw(g2d);
                g2d.dispose();

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(slideImage, "png", baos);
                imageResources.add(new ByteArrayResource(baos.toByteArray()));
            }
        }
        return imageResources;
    }
}
