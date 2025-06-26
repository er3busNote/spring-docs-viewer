package com.docs.viewer.common.preview.service;

import com.docs.viewer.common.file.dto.response.FileResponse;
import com.docs.viewer.common.preview.type.PreviewType;
import com.docs.viewer.global.common.setting.FileSetting;
import com.docs.viewer.global.common.utils.CryptoUtil;
import com.docs.viewer.global.common.utils.DocumentUtil;
import com.docs.viewer.global.common.utils.FileTypeUtil;
import com.docs.viewer.global.common.utils.FileUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PreviewService {

    private static final String FILE_DIRECTORY = "cmmn";

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
    public void saveFile(Integer attachFile, ByteArrayResource image,  String targetFolder) throws Exception {
        String rootPath = fileSetting.getImagePath();
        Path uploadDirectory = FileUtil.getUploadDirectory(FileUtil.getDirectory(rootPath, targetFolder));
        String uploadPath = FileUtil.getFilePath(uploadDirectory);
        CryptoUtil.encryptFile(image.getByteArray(), FileUtil.getTargetFile(uploadPath));  // 파일 저장
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
        List<ByteArrayResource> pdfImages = DocumentUtil.convertPdfToImageResources(pdfResource);
        return DocumentUtil.mergeImagesVertically(pdfImages);
    }

    private void createPdfToImage(Integer attachFile, ByteArrayResource pdfResource) throws Exception {
        List<ByteArrayResource> pdfImages = DocumentUtil.convertPdfToImageResources(pdfResource);
        for (ByteArrayResource pdfImage : pdfImages) {
            this.saveFile(attachFile, pdfImage, FILE_DIRECTORY);
        }
    }

    private ByteArrayResource convertDocxToImage(ByteArrayResource docxResource) throws Exception {
        List<ByteArrayResource> docxImages = DocumentUtil.convertDocxToImageResources(docxResource);
        return DocumentUtil.mergeImagesVertically(docxImages);
    }

    private void createDocxToImage(Integer attachFile, ByteArrayResource docxResource) throws Exception {
        List<ByteArrayResource> docxImages = DocumentUtil.convertDocxToImageResources(docxResource);
        for (ByteArrayResource docxImage : docxImages) {
            this.saveFile(attachFile, docxImage, FILE_DIRECTORY);
        }
    }

    private ByteArrayResource convertPptxToImage(ByteArrayResource pptxResource) throws Exception {
        List<ByteArrayResource> pptxImages = DocumentUtil.convertPptxToImageResources(pptxResource);
        return DocumentUtil.mergeImagesVertically(pptxImages);
    }

    private void createPptxToImage(Integer attachFile, ByteArrayResource pptxResource) throws Exception {
        List<ByteArrayResource> pptxImages = DocumentUtil.convertPptxToImageResources(pptxResource);
        for (ByteArrayResource pptxImage : pptxImages) {
            this.saveFile(attachFile, pptxImage, FILE_DIRECTORY);
        }
    }

    private ByteArrayResource convertXlsxToImage(ByteArrayResource xlsxResource) throws Exception {
        List<ByteArrayResource> xlsxImages = DocumentUtil.convertXlsxToImageResources(xlsxResource);
        return DocumentUtil.mergeImagesVertically(xlsxImages);
    }

    private void createXlsxToImage(Integer attachFile, ByteArrayResource xlsxResource) throws Exception {
        List<ByteArrayResource> xlsxImages = DocumentUtil.convertXlsxToImageResources(xlsxResource);
        for (ByteArrayResource xlsxImage : xlsxImages) {
            this.saveFile(attachFile, xlsxImage, FILE_DIRECTORY);
        }
    }
}
