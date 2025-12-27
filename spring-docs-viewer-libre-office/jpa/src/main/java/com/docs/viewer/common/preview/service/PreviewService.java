package com.docs.viewer.common.preview.service;

import com.docs.viewer.common.file.dto.response.FileResponse;
import com.docs.viewer.common.file.entity.File;
import com.docs.viewer.common.file.repository.FileRepository;
import com.docs.viewer.common.preview.entity.Preview;
import com.docs.viewer.common.preview.repository.PreviewRepository;
import com.docs.viewer.common.preview.type.PreviewType;
import com.docs.viewer.global.common.setting.FileSetting;
import com.docs.viewer.global.common.utils.CryptoUtil;
import com.docs.viewer.global.common.utils.FileTypeUtil;
import com.docs.viewer.global.common.utils.FileUtil;
import com.docs.viewer.global.error.exception.NotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Slf4j
@Service
@RequiredArgsConstructor
public class PreviewService {

    private static final String FILE_DIRECTORY = "cmmn";

    private final FileRepository fileRepository;
    private final PreviewRepository previewRepository;
    private final FileSetting fileSetting;

    public ByteArrayResource findFile(FileResponse fileResponse) throws Exception {
        ByteArrayResource resource = fileResponse.getResource();
        String mimeType = fileResponse.getMimeType();
        PreviewType previewType = this.getPreviewType(mimeType);
        return switch (previewType) {
            case PPTX -> convertPptxToImage(resource);
            case XLSX -> convertXlsxToImage(resource);
            case DOCX -> convertDocxToImage(resource);
            case PDF -> convertPdfToImage(resource);
            default -> resource;
        };
    }

    @Transactional
    public void saveFile(Integer attachFile, ByteArrayResource image, String targetFolder, int index) throws Exception {
        File fileInfo = this.findFileById(attachFile);
        String rootPath = fileSetting.getImagePath();
        Path uploadDirectory = FileUtil.getUploadDirectory(FileUtil.getDirectory(rootPath, targetFolder));
        String uploadPath = FileUtil.getFilePath(uploadDirectory);
        String mimeType = this.findMimeType(image.getByteArray());
        long fileSize = image.contentLength();
        Preview previewInfo = this.previewRepository.save(Preview.of(fileInfo, uploadPath, mimeType, fileSize));
        CryptoUtil.encryptFile(image.getByteArray(), FileUtil.getTargetFile(previewInfo.getFilePath()));  // 파일 저장
    }

    @Transactional
    public void saveFile(Integer attachFile, FileResponse fileResponse) throws Exception {
        ByteArrayResource resource = fileResponse.getResource();
        String mimeType = fileResponse.getMimeType();
        PreviewType previewType = this.getPreviewType(mimeType);
        switch (previewType) {
            case PPTX -> createPptxToImage(attachFile, resource);
            case XLSX -> createXlsxToImage(attachFile, resource);
            case DOCX -> createDocxToImage(attachFile, resource);
            case PDF -> createPdfToImage(attachFile, resource);
            default -> {
            }
        }
    }

    private File findFileById(Integer attachFile) {
        return this.fileRepository.findById(attachFile.longValue())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 상품코드 입니다."));
    }

    private String findMimeType(byte[] fileBytes) {
        Tika tika = new Tika();
        return tika.detect(fileBytes);
    }

    private PreviewType getPreviewType(String mimeType) {
        if(FileTypeUtil.isPptx(mimeType)) {
            return PreviewType.PPTX;
        } else if (FileTypeUtil.isXlsx(mimeType)) {
            return PreviewType.XLSX;
        } else if (FileTypeUtil.isDocx(mimeType)) {
            return PreviewType.DOCX;
        } else if (FileTypeUtil.isPdf(mimeType)) {
            return PreviewType.PDF;
        } else {
            return PreviewType.NONE;
        }
    }

    private ByteArrayResource convertPdfToImage(ByteArrayResource pdfResource) throws Exception {
        return null;
    }

    private void createPdfToImage(Integer attachFile, ByteArrayResource pdfResource) throws Exception {

    }

    private ByteArrayResource convertDocxToImage(ByteArrayResource docxResource) throws Exception {
        return null;
    }

    private void createDocxToImage(Integer attachFile, ByteArrayResource docxResource) throws Exception {

    }

    private ByteArrayResource convertPptxToImage(ByteArrayResource pptxResource) throws Exception {
        return null;
    }

    private void createPptxToImage(Integer attachFile, ByteArrayResource pptxResource) throws Exception {

    }

    private ByteArrayResource convertXlsxToImage(ByteArrayResource xlsxResource) throws Exception {
        return null;
    }

    private void createXlsxToImage(Integer attachFile, ByteArrayResource xlsxResource) throws Exception {

    }
}
