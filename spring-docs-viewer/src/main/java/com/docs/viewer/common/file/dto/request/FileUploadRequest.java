package com.docs.viewer.common.file.dto.request;

import com.docs.viewer.common.file.validation.ValidFileList;
import jakarta.validation.Valid;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class FileUploadRequest {

    @Valid
    @ValidFileList
    private List<MultipartFile> files;
}
