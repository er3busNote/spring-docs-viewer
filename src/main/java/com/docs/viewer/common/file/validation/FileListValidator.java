package com.docs.viewer.common.file.validation;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class FileListValidator implements ConstraintValidator<ValidFileList, List<MultipartFile>> {

    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg",
            "image/png"
    );

    private static final long ALLOWED_FILE_SIZE = 5 * 1024 * 1024;  // 최대 5MB

    @Override
    public void initialize(ValidFileList constraintAnnotation) {
        // 초기화 작업 (필요시 사용)
    }

    @Override
    public boolean isValid(List<MultipartFile> files, ConstraintValidatorContext context) {
        if (files == null || files.isEmpty()) {
            return false; // 파일이 없으면 유효하지 않음
        }

        // 각 파일에 대해 유효성 검사
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                return false; // 파일이 비어있으면 유효하지 않음
            }

            // 파일 크기 검사
            if (file.getSize() > ALLOWED_FILE_SIZE) {
                return false; // 파일 크기 초과
            }

            // 파일 형식 검사 (예: 이미지 파일만 허용)
            if (isImageContentType(file)) {
                return false; // 이미지가 아닌 파일은 유효하지 않음
            }

            // 추가적인 이미지 유효성 검사
            try {
                BufferedImage image = ImageIO.read(file.getInputStream());
                if (image == null) {
                    return false; // 손상된 이미지 파일
                }
            } catch (IOException e) {
                return false; // 이미지 파일 읽기 오류
            }
        }

        return true; // 모든 파일이 유효하면 true
    }

    private boolean isImageContentType(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && ALLOWED_CONTENT_TYPES.contains(contentType);
    }

    private String getAllowedContentTypesAsString() {
        return ALLOWED_CONTENT_TYPES.stream().collect(Collectors.joining(", "));
    }

}
