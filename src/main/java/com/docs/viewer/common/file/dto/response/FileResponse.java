package com.docs.viewer.common.file.dto.response;

import lombok.*;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class FileResponse {

    private ByteArrayResource resource;
    private MediaType mediaType;

    public static FileResponse of(ByteArrayResource resource, MediaType mediaType) {
        return FileResponse.builder()
                .resource(resource)
                .mediaType(mediaType)
                .build();
    }
}
