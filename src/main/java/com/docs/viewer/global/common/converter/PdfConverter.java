package com.docs.viewer.global.common.converter;

import com.docs.viewer.global.common.utils.ImageUtil;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class PdfConverter {

    public ByteArrayResource convertPdfToImage(ByteArrayResource pdfResource) throws Exception {
        List<ByteArrayResource> pdfImages = convertPdfToImageResources(pdfResource);
        return ImageUtil.mergeImagesVertically(pdfImages);
    }

    public List<ByteArrayResource> createPdfToImages(ByteArrayResource pdfResource) throws Exception {
        return convertPdfToImageResources(pdfResource);
    }

    /**
     * PDF → Image 리스트
     */
    private static List<ByteArrayResource> convertPdfToImageResources(ByteArrayResource pdfResource) throws Exception {
        List<ByteArrayResource> imageResources = new ArrayList<>();
        try (InputStream pdfInputStream = pdfResource.getInputStream();
             PDDocument pdfDocument = Loader.loadPDF(new RandomAccessReadBuffer(pdfInputStream))) {

            PDFRenderer pdfRenderer = new PDFRenderer(pdfDocument);
            int pageCount = pdfDocument.getNumberOfPages();

            for (int page = 0; page < pageCount; page++) {
                BufferedImage pageImage = pdfRenderer.renderImageWithDPI(page, 200);
                ByteArrayOutputStream imageOutput = new ByteArrayOutputStream();
                ImageIO.write(pageImage, "png", imageOutput);
                imageResources.add(new ByteArrayResource(imageOutput.toByteArray()));
            }
        }
        return imageResources;
    }
}
