package com.docs.viewer.global.common.utils;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.docx4j.Docx4J;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class DocumentUtil {

    // 1. PDF → Image 리스트
    public static List<ByteArrayResource> convertPdfToImageResources(ByteArrayResource pdfResource) throws Exception {
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

    // 2. DOCX → PDF → Image 리스트
    public static List<ByteArrayResource> convertDocxToImageResources(ByteArrayResource docxResource) throws Exception {
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

    // 3. PPTX → 슬라이드 이미지 리스트
    public static List<ByteArrayResource> convertPptxToImageResources(ByteArrayResource pptxResource) throws Exception {
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

    // 4. XLSX → 시트 이미지 리스트
    public static List<ByteArrayResource> convertXlsxToImageResources(ByteArrayResource xlsxResource) throws Exception {
        List<ByteArrayResource> imageResources = new ArrayList<>();

        try (InputStream inputStream = xlsxResource.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            int numberOfSheets = workbook.getNumberOfSheets();
            int cellWidth = 100;
            int cellHeight = 30;

            for (int sheetIndex = 0; sheetIndex < numberOfSheets; sheetIndex++) {
                Sheet sheet = workbook.getSheetAt(sheetIndex);
                int rows = sheet.getLastRowNum() + 1;
                int cols = 0;

                for (Row row : sheet) {
                    cols = Math.max(cols, row.getLastCellNum());
                }

                BufferedImage image = new BufferedImage(cellWidth * cols, cellHeight * rows, BufferedImage.TYPE_INT_RGB);
                Graphics2D g2d = image.createGraphics();

                g2d.setColor(Color.WHITE);
                g2d.fillRect(0, 0, image.getWidth(), image.getHeight());
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Arial", Font.PLAIN, 12));

                for (int rowIdx = 0; rowIdx < rows; rowIdx++) {
                    Row row = sheet.getRow(rowIdx);
                    for (int colIdx = 0; colIdx < cols; colIdx++) {
                        String cellValue = "";
                        if (row != null) {
                            Cell cell = row.getCell(colIdx);
                            if (cell != null) {
                                cellValue = getCellValue(cell);
                            }
                        }
                        int x = colIdx * cellWidth;
                        int y = rowIdx * cellHeight;
                        g2d.drawRect(x, y, cellWidth, cellHeight);
                        g2d.drawString(cellValue, x + 5, y + 20);
                    }
                }

                g2d.dispose();

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(image, "png", baos);
                imageResources.add(new ByteArrayResource(baos.toByteArray()));
            }
        }
        return imageResources;
    }

    private static String getCellValue(Cell cell) {
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue();
            case NUMERIC: return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            case FORMULA: return cell.getCellFormula();
            default: return "";
        }
    }

    // 공통 이어 붙이기 메서드
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
