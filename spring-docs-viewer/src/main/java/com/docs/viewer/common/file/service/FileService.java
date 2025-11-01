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

import jakarta.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private static final String FILE_DIRECTORY = "cmmn";

    private final FileRepository fileRepository;
    private final FileSetting fileSetting;

    @Transactional
    public byte[] findFile(Integer attachFile) throws Exception {
        Optional<File> info = this.fileRepository.findById(attachFile.longValue());
        if (info.isPresent()) {
            File file = info.get();
            if('N' == file.getCloudYn()) {
                String rootPath = fileSetting.getFilePath();
                String filePath = file.getFilePath().replace("..", rootPath);
                return CryptoUtil.decryptFile(filePath);
            }
        }
        throw new IOException("해당되는 파일을 찾을 수 없습니다.");
    }

    @Transactional
    public FileInfo saveFile(MultipartFile file, String targetFolder) throws Exception {
        String rootPath = fileSetting.getFilePath();
        Path uploadDirectory = FileUtil.getUploadDirectory(FileUtil.getDirectory(rootPath, targetFolder));
        String fileName = file.getOriginalFilename();
        String contentType = file.getContentType();
        Integer fileSize = Integer.valueOf(String.valueOf(file.getSize()));
        File fileInfo = this.fileRepository.save(File.of(fileName, FileUtil.getFilePath(uploadDirectory), contentType, fileSize, 'N'));
        CryptoUtil.encryptFile(file.getBytes(), FileUtil.getTargetFile(fileInfo.getFilePath()));  // 파일 저장
        return FileInfo.of(fileInfo);
    }

    @Transactional
    public List<FileAttachInfo> saveFile(List<MultipartFile> files) throws Exception {
        List<FileInfo> fileInfoList = files.stream().map(ExceptionUtil.wrapFunction(file -> this.saveFile(file, FILE_DIRECTORY))).toList();
        return fileInfoList.stream().map(FileAttachInfo::of).toList();
    }
}
