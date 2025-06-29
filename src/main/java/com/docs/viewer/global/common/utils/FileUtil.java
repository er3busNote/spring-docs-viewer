package com.docs.viewer.global.common.utils;

import com.docs.viewer.global.common.setting.FileSetting;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FileUtil {

    private static final String PROJECT_DIRECTORY = System.getProperty("user.dir");


    public static Path getDirectory(String filepath, String targetFolder) {
        String directoryPath =  targetFolder + "/" + DateUtil.getCurrentDate();
        Path folderPath = Paths.get(filepath);
        return folderPath.resolve(directoryPath);
    }

    public static Path getUploadDirectory(Path uploadDirectory) {
        createDirectory(uploadDirectory.toString()); // 업로드할 디렉토리가 없으면 생성
        return uploadDirectory;
    }

    public static File getTargetFile(String targetFilePath) {
        return new File(PROJECT_DIRECTORY + "/" + targetFilePath);
    }

    public static String getFilePath(Path uploadDirectory) {
        return uploadDirectory.toString() + "/" + UUID.randomUUID();
    }

    public static String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex > 0) {
            return fileName.substring(lastDotIndex + 1);
        }
        return "";
    }

    public static String removeFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex > 0) {
            return fileName.substring(0, lastDotIndex);
        }
        return fileName;
    }

    private static void createDirectory(String uploadDirectory) {
        File directory = new File(uploadDirectory);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }
}
