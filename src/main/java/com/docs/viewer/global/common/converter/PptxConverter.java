package com.docs.viewer.global.common.converter;

import com.docs.viewer.global.common.utils.DocumentUtil;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PptxConverter {

    public ByteArrayResource convertPptxToImage(ByteArrayResource pptxResource) throws Exception {
        List<ByteArrayResource> pptxImages = DocumentUtil.convertPptxToImageResources(pptxResource);
        return DocumentUtil.mergeImagesVertically(pptxImages);
    }

    public List<ByteArrayResource> createPptxToImages(ByteArrayResource pptxResource) throws Exception {
        return DocumentUtil.convertPptxToImageResources(pptxResource);
    }
}
