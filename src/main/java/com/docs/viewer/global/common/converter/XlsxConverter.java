package com.docs.viewer.global.common.converter;

import com.docs.viewer.global.common.utils.ImageUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
public class XlsxConverter {

    public ByteArrayResource convertXlsxToImage(ByteArrayResource xlsxResource) throws Exception {
        List<ByteArrayResource> xlsxImages = convertXlsxToImageResources(xlsxResource);
        return ImageUtil.mergeImagesVertically(xlsxImages);
    }

    public List<ByteArrayResource> createXlsxToImages(ByteArrayResource xlsxResource) throws Exception {
        return convertXlsxToImageResources(xlsxResource);
    }

    /**
     * XLSX → 시트 Image 리스트
     */
    private static List<ByteArrayResource> convertXlsxToImageResources(ByteArrayResource xlsxResource) throws Exception {
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
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            default -> "";
        };
    }
}
