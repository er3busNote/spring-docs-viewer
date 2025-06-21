package com.docs.viewer.common.file.service;

import com.docs.viewer.common.file.dto.FileAttachInfo;
import com.docs.viewer.common.file.dto.FileInfo;
import com.docs.viewer.common.file.entity.File;
import com.docs.viewer.common.file.repository.FileRepository;
import com.docs.viewer.global.common.setting.FileSetting;
import com.docs.viewer.global.common.utils.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private static final String FILE_DIRECTORY = "cmmn";

    private final FileRepository fileRepository;
    private final ObjectStorageUtil objectStorageUtil;
    private final FileSetting fileSetting;

    @Transactional
    public byte[] findFile(Integer attachFile) throws Exception {
        Optional<File> info = this.fileRepository.findById(attachFile.longValue());
        if (info.isPresent()) {
            File file = info.get();
            if('Y' == file.getCloudYn()) {
                return this.objectStorageUtil.downloadFile(file.getFilePath());
            } else {
                String rootPath = fileSetting.getFilepath();
                String filePath = file.getFilePath().replace("..", rootPath);
                return CryptoUtil.decryptFile(filePath);
            }
        }
        throw new IOException("해당되는 파일을 찾을 수 없습니다.");
    }

    @Transactional
    public FileInfo saveFile(MultipartFile file, String targetFolder) throws Exception {
        Path uploadDirectory = FileUtil.getUploadDirectory(this.getDirectory(targetFolder));
        String fileName = file.getOriginalFilename();
        String contentType = file.getContentType();
        Integer fileSize = Integer.valueOf(String.valueOf(file.getSize()));
        File fileInfo = this.fileRepository.save(File.of(fileName, this.getFilePath(uploadDirectory), contentType, fileSize, 'N'));
        CryptoUtil.encryptFile(file, FileUtil.getTargetFile(fileInfo.getFilePath()));  // 파일 저장
        return FileInfo.of(fileInfo);
    }

    @Transactional
    public FileInfo saveBucketFile(MultipartFile file) throws IOException {
        String bucketPath = this.getBucketPath();
        String fileName = file.getOriginalFilename();
        String contentType = file.getContentType();
        Integer fileSize = Integer.valueOf(String.valueOf(file.getSize()));
        File fileInfo = this.fileRepository.save(File.of(fileName, bucketPath, contentType, fileSize, 'Y'));
        this.objectStorageUtil.uploadFile(file, bucketPath);
        return FileInfo.of(fileInfo);
    }

    @Transactional
    public List<FileAttachInfo> saveFile(List<MultipartFile> files) throws Exception {
        List<FileInfo> fileInfoList = files.stream().map(ExceptionUtil.wrapFunction(file -> this.saveFile(file, FILE_DIRECTORY))).collect(Collectors.toList());
        return fileInfoList.stream().map(FileAttachInfo::of).collect(Collectors.toList());
    }

    @Transactional
    public List<FileAttachInfo> saveBucketFile(List<MultipartFile> files) throws Exception {
        List<FileInfo> fileInfoList = files.stream().map(ExceptionUtil.wrapFunction(file -> this.saveBucketFile(file))).collect(Collectors.toList());
        return fileInfoList.stream().map(FileAttachInfo::of).collect(Collectors.toList());
    }

    private String getDirectory(String targetFolder) {
        return targetFolder + "/" + DateUtil.getCurrentDate();
    }

    private String getFilePath(Path uploadDirectory) {
        return uploadDirectory.toString() + "/" + UUID.randomUUID();
    }

    private String getBucketPath() {
        return DateUtil.getCurrentDate() + "/" + UUID.randomUUID();
    }
}
