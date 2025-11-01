package com.docs.viewer.common.file.dto.response;

import lombok.*;
import org.springframework.core.io.ByteArrayResource;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class FileResponse {

    private ByteArrayResource resource;
    private String mimeType;

    public static FileResponse of(ByteArrayResource resource, String mimeType) {
        return FileResponse.builder()
                .resource(resource)
                .mimeType(mimeType)
                .build();
    }
}
