package com.docs.viewer.common.preview.service;

import lombok.extern.slf4j.Slf4j;
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
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

@Slf4j
@Service
public class PreviewService {

    public ByteArrayResource convertPdfToImage(ByteArrayResource pdfResource) throws Exception {
        try (InputStream pdfInputStream = pdfResource.getInputStream();
             PDDocument pdfDocument = Loader.loadPDF(new RandomAccessReadBuffer(pdfInputStream))) {

            PDFRenderer pdfRenderer = new PDFRenderer(pdfDocument);
            int pageCount = pdfDocument.getNumberOfPages();

            BufferedImage[] pageImages = new BufferedImage[pageCount];
            int totalHeight = 0;
            int maxWidth = 0;

            for (int page = 0; page < pageCount; page++) {
                BufferedImage pageImage = pdfRenderer.renderImageWithDPI(page, 200);
                pageImages[page] = pageImage;
                totalHeight += pageImage.getHeight();
                maxWidth = Math.max(maxWidth, pageImage.getWidth());
            }

            BufferedImage fullImage = new BufferedImage(maxWidth, totalHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = fullImage.createGraphics();
            g2d.setPaint(Color.WHITE);
            g2d.fillRect(0, 0, maxWidth, totalHeight);

            int yOffset = 0;
            for (BufferedImage pageImage : pageImages) {
                g2d.drawImage(pageImage, 0, yOffset, null);
                yOffset += pageImage.getHeight();
            }

            ByteArrayOutputStream imageOutput = new ByteArrayOutputStream();
            ImageIO.write(fullImage, "png", imageOutput);
            return new ByteArrayResource(imageOutput.toByteArray());
        }
    }

    public ByteArrayResource convertDocxToImage(ByteArrayResource docxResource) throws Exception {
        // DOCX → PDF 변환
        try (InputStream docxInputStream = docxResource.getInputStream();
             ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream()) {

            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(docxInputStream);
            Docx4J.toPDF(wordMLPackage, pdfOutputStream);

            // PDF → 이미지 변환 (모든 페이지 이어붙이기)
            try (InputStream pdfInputStream = new ByteArrayInputStream(pdfOutputStream.toByteArray());
                 PDDocument pdfDocument = Loader.loadPDF(new RandomAccessReadBuffer(pdfInputStream))) {

                PDFRenderer pdfRenderer = new PDFRenderer(pdfDocument);
                int pageCount = pdfDocument.getNumberOfPages();

                // 각 페이지 이미지 준비
                BufferedImage[] pageImages = new BufferedImage[pageCount];
                int totalHeight = 0;
                int maxWidth = 0;

                for (int page = 0; page < pageCount; page++) {
                    BufferedImage pageImage = pdfRenderer.renderImageWithDPI(page, 200);  // DPI는 200 정도 추천
                    pageImages[page] = pageImage;
                    totalHeight += pageImage.getHeight();
                    maxWidth = Math.max(maxWidth, pageImage.getWidth());
                }

                // 최종 이미지 생성
                BufferedImage fullImage = new BufferedImage(maxWidth, totalHeight, BufferedImage.TYPE_INT_RGB);
                Graphics2D g2d = fullImage.createGraphics();
                g2d.setPaint(Color.WHITE);
                g2d.fillRect(0, 0, maxWidth, totalHeight);

                int yOffset = 0;
                for (BufferedImage pageImage : pageImages) {
                    g2d.drawImage(pageImage, 0, yOffset, null);
                    yOffset += pageImage.getHeight();
                }

                ByteArrayOutputStream imageOutput = new ByteArrayOutputStream();
                ImageIO.write(fullImage, "png", imageOutput);
                return new ByteArrayResource(imageOutput.toByteArray());
            }
        }
    }

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

    public ByteArrayResource convertXlsxToImage(ByteArrayResource xlsxResource) throws Exception {
        try (InputStream inputStream = xlsxResource.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0); // 첫 번째 시트
            int rows = sheet.getLastRowNum() + 1;
            int cols = 0;

            for (Row row : sheet) {
                cols = Math.max(cols, row.getLastCellNum());
            }

            // 기본 셀당 크기 지정 (px 단위)
            int cellWidth = 100;
            int cellHeight = 30;

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

                    // 테두리
                    g2d.drawRect(x, y, cellWidth, cellHeight);

                    // 텍스트 출력
                    g2d.drawString(cellValue, x + 5, y + 20);
                }
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            return new ByteArrayResource(baos.toByteArray());
        }
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
}
