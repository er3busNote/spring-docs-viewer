package com.docs.viewer.global.common.utils;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class FileTypeUtil {

    private static final MediaType PDF = MediaType.APPLICATION_PDF;
    private static final MediaType DOCX = MediaType.valueOf("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
    private static final MediaType PPTX = MediaType.valueOf("application/vnd.openxmlformats-officedocument.presentationml.presentation");
    private static final MediaType XLSX = MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    private static final MediaType JPEG = MediaType.IMAGE_JPEG;
    private static final MediaType PNG = MediaType.IMAGE_PNG;

    public static boolean isNotAllowType(String mimeType) {
        return !isAllowType(mimeType);
    }

    public static boolean isAllowType(String mimeType) {
        MediaType type = MediaType.valueOf(mimeType);
        return Stream.of(
                PDF, DOCX, PPTX, XLSX, JPEG, PNG
        ).anyMatch(allowedType -> allowedType.includes(type));
    }

    public static boolean isPdf(String contentType) {
        return PDF.includes(MediaType.valueOf(contentType));
    }

    private static boolean isDocx(String contentType) {
        return DOCX.includes(MediaType.valueOf(contentType));
    }

    private static boolean isPptx(String contentType) {
        return PPTX.includes(MediaType.valueOf(contentType));
    }

    private static boolean isXlsx(String contentType) {
        return XLSX.includes(MediaType.valueOf(contentType));
    }
}
