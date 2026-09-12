package com.docs.viewer.global.common.converter;

import com.docs.viewer.global.common.utils.DocumentUtil;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class XlsxConverter {

    public ByteArrayResource convertXlsxToImage(ByteArrayResource xlsxResource) throws Exception {
        List<ByteArrayResource> xlsxImages = DocumentUtil.convertXlsxToImageResources(xlsxResource);
        return DocumentUtil.mergeImagesVertically(xlsxImages);
    }

    public List<ByteArrayResource> createXlsxToImages(ByteArrayResource xlsxResource) throws Exception {
        return DocumentUtil.convertXlsxToImageResources(xlsxResource);
    }
}
