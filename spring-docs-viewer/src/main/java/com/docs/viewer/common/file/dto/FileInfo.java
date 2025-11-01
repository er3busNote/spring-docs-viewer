package com.docs.viewer.common.file.dto;

import com.docs.viewer.common.file.entity.File;
import com.docs.viewer.global.common.utils.FileUtil;
import lombok.*;

import java.util.Optional;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class FileInfo {

    private Integer fileCode;
    private String fileName;
    private String filePath;
    private String fileType;
    private Integer fileSize;
    private String fileExtension;
    private String fileNameWithoutExtension;

    public FileInfo(Long fileCode, String fileName, String filePath, String fileType, Integer fileSize) {
        this.fileCode = Optional.ofNullable(fileCode).map(Long::intValue).orElse(null);
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.fileExtension = Optional.ofNullable(fileName).map(FileUtil::getFileExtension).orElse(null);
        this.fileNameWithoutExtension = Optional.ofNullable(fileName).map(FileUtil::removeFileExtension).orElse(null);
    }

    public static FileInfo of(File file) {
        return FileInfo.builder()
                .fileCode(file.getFileCode().intValue())
                .fileName(file.getFileName())
                .filePath(file.getFilePath())
                .fileType(file.getFileType())
                .fileSize(file.getFileSize())
                .fileExtension(FileUtil.getFileExtension(file.getFileName()))
                .fileNameWithoutExtension(FileUtil.removeFileExtension(file.getFileName()))
                .build();
    }
}
