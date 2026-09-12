package com.docs.viewer.common.preview.service;

import com.docs.viewer.common.file.dto.response.FileResponse;
import com.docs.viewer.common.file.entity.File;
import com.docs.viewer.common.file.repository.FileRepository;
import com.docs.viewer.common.preview.entity.Preview;
import com.docs.viewer.common.preview.repository.PreviewRepository;
import com.docs.viewer.common.preview.type.PreviewType;
import com.docs.viewer.global.common.converter.DocxConverter;
import com.docs.viewer.global.common.converter.PdfConverter;
import com.docs.viewer.global.common.converter.PptxConverter;
import com.docs.viewer.global.common.converter.XlsxConverter;
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
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PreviewService {

    private static final String FILE_DIRECTORY = "cmmn";

    private final FileRepository fileRepository;
    private final PreviewRepository previewRepository;
    private final PptxConverter pptxConverter;
    private final XlsxConverter xlsxConverter;
    private final DocxConverter docxConverter;
    private final PdfConverter pdfConverter;
    private final FileSetting fileSetting;

    public ByteArrayResource findFile(FileResponse fileResponse) throws Exception {
        ByteArrayResource resource = fileResponse.getResource();
        String mimeType = fileResponse.getMimeType();
        PreviewType previewType = this.getPreviewType(mimeType);
        return switch (previewType) {
            case PPTX -> pptxConverter.convertPptxToImage(resource);
            case XLSX -> xlsxConverter.convertXlsxToImage(resource);
            case DOCX -> docxConverter.convertDocxToImage(resource);
            case PDF -> pdfConverter.convertPdfToImage(resource);
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
            case PPTX -> this.saveFile(attachFile, pptxConverter.createPptxToImages(resource));
            case XLSX -> this.saveFile(attachFile, xlsxConverter.createXlsxToImages(resource));
            case DOCX -> this.saveFile(attachFile, docxConverter.createDocxToImages(resource));
            case PDF -> this.saveFile(attachFile, pdfConverter.createPdfToImages(resource));
            default -> {
            }
        }
    }

    public void saveFile(Integer attachFile, List<ByteArrayResource> images) throws Exception {
        int index = 1;
        for (ByteArrayResource image : images) {
            this.saveFile(attachFile, image, FILE_DIRECTORY, index++);
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
}
