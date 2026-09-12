package com.docs.viewer.global.common.converter;

import com.docs.viewer.global.common.utils.DocumentUtil;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DocxConverter {

    public ByteArrayResource convertDocxToImage(ByteArrayResource docxResource) throws Exception {
        List<ByteArrayResource> docxImages = DocumentUtil.convertDocxToImageResources(docxResource);
        return DocumentUtil.mergeImagesVertically(docxImages);
    }

    public List<ByteArrayResource> createDocxToImages(ByteArrayResource docxResource) throws Exception {
        return DocumentUtil.convertDocxToImageResources(docxResource);
    }
}
