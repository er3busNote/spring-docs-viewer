package com.docs.viewer.global.common.converter;

import com.docs.viewer.global.common.utils.DocumentUtil;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PdfConverter {

    public ByteArrayResource convertPdfToImage(ByteArrayResource pdfResource) throws Exception {
        List<ByteArrayResource> pdfImages = DocumentUtil.convertPdfToImageResources(pdfResource);
        return DocumentUtil.mergeImagesVertically(pdfImages);
    }

    public List<ByteArrayResource> createPdfToImages(ByteArrayResource pdfResource) throws Exception {
        return DocumentUtil.convertPdfToImageResources(pdfResource);
    }
}
