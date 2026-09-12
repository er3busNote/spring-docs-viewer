package com.docs.viewer.global.common.converter;

import com.docs.viewer.global.common.utils.ImageUtil;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.docx4j.Docx4J;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class DocxConverter {

    public ByteArrayResource convertDocxToImage(ByteArrayResource docxResource) throws Exception {
        List<ByteArrayResource> docxImages = convertDocxToImageResources(docxResource);
        return ImageUtil.mergeImagesVertically(docxImages);
    }

    public List<ByteArrayResource> createDocxToImages(ByteArrayResource docxResource) throws Exception {
        return convertDocxToImageResources(docxResource);
    }

    /**
     * DOCX → PDF → Image 리스트
     */
    private static List<ByteArrayResource> convertDocxToImageResources(ByteArrayResource docxResource) throws Exception {
        List<ByteArrayResource> imageResources = new ArrayList<>();

        try (InputStream docxInputStream = docxResource.getInputStream();
             ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream()) {

            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(docxInputStream);
            Docx4J.toPDF(wordMLPackage, pdfOutputStream);

            try (InputStream pdfInputStream = new ByteArrayInputStream(pdfOutputStream.toByteArray());
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
        }
        return imageResources;
    }
}
