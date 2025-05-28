package com.docs.viewer.common.file.api;

import com.docs.viewer.common.file.dto.response.FileResponse;
import com.docs.viewer.common.file.service.FileService;
import com.docs.viewer.global.error.dto.ErrorCode;
import com.docs.viewer.global.error.dto.ErrorInfo;
import com.docs.viewer.global.error.dto.response.ErrorHandler;
import com.docs.viewer.global.error.dto.response.ErrorResponse;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/file")
public class FileController {

    private final FileService fileService;
    private final ErrorHandler errorHandler;

    @GetMapping
    public ResponseEntity fileImage(@RequestParam Integer attachFile) {
        try {
            FileResponse fileResponse = this.findFile(attachFile);
            return ResponseEntity.ok().contentType(fileResponse.getMediaType())
                    .body(fileResponse.getResource());
        } catch (Exception e) {
            return unprocessableEntity(errorHandler.buildError(ErrorCode.INTERNAL_SERVER_ERROR, ErrorInfo.builder()
                    .message(e.getMessage())
                    .build()));
        }
    }

    private FileResponse findFile(Integer attachFile) throws Exception {
        byte[] fileBytes = fileService.findFile(attachFile);
        ByteArrayResource resource = new ByteArrayResource(fileBytes);
        MediaType mediaType = this.findMediaType(fileBytes);
        return FileResponse.of(resource, mediaType);
    }

    private MediaType findMediaType(byte[] fileBytes) {
        Tika tika = new Tika();
        String tikaMimeType = tika.detect(fileBytes);
        return MediaType.parseMediaType(tikaMimeType);
    }

    private ResponseEntity<ErrorResponse> unprocessableEntity(ErrorResponse response) {
        return ResponseEntity.unprocessableEntity().body(response);
    }
}
