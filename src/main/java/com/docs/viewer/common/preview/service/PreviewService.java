package com.docs.viewer.common.preview.service;

import com.docs.viewer.global.common.utils.DocumentUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class PreviewService {

    public ByteArrayResource convertPdfToImage(ByteArrayResource pdfResource) throws Exception {
        List<ByteArrayResource> pdfImages = DocumentUtil.convertPdfToImageResources(pdfResource);
        return DocumentUtil.mergeImagesVertically(pdfImages);

    }

    public ByteArrayResource convertDocxToImage(ByteArrayResource docxResource) throws Exception {
        List<ByteArrayResource> docxImages = DocumentUtil.convertDocxToImageResources(docxResource);
        return DocumentUtil.mergeImagesVertically(docxImages);
    }

    public ByteArrayResource convertPptxToImage(ByteArrayResource pptxResource) throws Exception {
        List<ByteArrayResource> pptxImages = DocumentUtil.convertPptxToImageResources(pptxResource);
        return DocumentUtil.mergeImagesVertically(pptxImages);
    }

    public ByteArrayResource convertXlsxToImage(ByteArrayResource xlsxResource) throws Exception {
        List<ByteArrayResource> xlsxImages = DocumentUtil.convertXlsxToImageResources(xlsxResource);
        return DocumentUtil.mergeImagesVertically(xlsxImages);
    }
}
