package com.docs.viewer.common.file.service;

import com.docs.viewer.common.file.entity.File;
import com.docs.viewer.common.file.repository.FileRepository;
import com.docs.viewer.global.common.properties.FileProperties;
import com.docs.viewer.global.common.utils.CryptoUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;
    private final FileProperties fileProperties;

    @Transactional
    public byte[] findFile(Integer attachFile) throws Exception {
        Optional<File> info = this.fileRepository.findById(attachFile.longValue());
        if (info.isPresent()) {
            File file = info.get();
            if('Y' != file.getCloudYn()){
                String filePath = file.getFilePath().replace("..", fileProperties.getFilepath());
                return CryptoUtil.decryptFile(filePath);
            }
        }
        throw new IOException("해당되는 파일을 찾을 수 없습니다.");
    }
}
