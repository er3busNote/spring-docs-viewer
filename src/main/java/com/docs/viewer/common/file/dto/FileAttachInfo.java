package com.docs.viewer.common.file.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FileAttachInfo {

    private Integer fileAttachCode;
    private String fileName;
    private String fileType;
    private Integer fileSize;
    private String fileExtension;

    public static FileAttachInfo from(Integer fileAttachCode, String fileName, String fileType, Integer fileSize, String fileExtension) {
        return new FileAttachInfo(fileAttachCode, fileName, fileType, fileSize, fileExtension);
    }

    public static FileAttachInfo of(FileInfo fileInfo) {
        return FileAttachInfo.builder()
                .fileAttachCode(fileInfo.getFileCode())
                .fileName(fileInfo.getFileName())
                .fileType(fileInfo.getFileType())
                .fileSize(fileInfo.getFileSize())
                .fileExtension(fileInfo.getFileExtension())
                .build();
    }
}
