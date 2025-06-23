package com.docs.viewer.common.file.api;

import com.docs.viewer.common.file.dto.request.FileUploadRequest;
import com.docs.viewer.common.file.dto.response.FileResponse;
import com.docs.viewer.common.file.service.FileService;
import com.docs.viewer.common.preview.service.PreviewService;
import com.docs.viewer.global.common.dto.response.ResponseHandler;
import com.docs.viewer.global.common.utils.FileTypeUtil;
import com.docs.viewer.global.error.dto.ErrorCode;
import com.docs.viewer.global.error.dto.ErrorInfo;
import com.docs.viewer.global.error.dto.response.ErrorHandler;
import com.docs.viewer.global.error.dto.response.ErrorResponse;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/file")
public class FileController {

    private final FileService fileService;
    private final PreviewService previewService;
    private final ErrorHandler errorHandler;

    @GetMapping
    public ResponseEntity<?> fileImage(@RequestParam Integer attachFile) {
        try {
            FileResponse fileResponse = this.findFile(attachFile);
            String mimeType = fileResponse.getMimeType();
            if (FileTypeUtil.isAllowType(mimeType)) {
                ByteArrayResource resource = fileResponse.getResource();
                MediaType mediaType = MediaType.parseMediaType(mimeType);
                if(FileTypeUtil.isPptx(mimeType)) {
                    return ResponseEntity.ok().contentType(mediaType)
                            .body(previewService.convertPptxToImage(resource));
                } else if (FileTypeUtil.isXlsx(mimeType)) {
                    return ResponseEntity.ok().contentType(mediaType)
                            .body(previewService.convertXlsxToImage(resource));
                } else if (FileTypeUtil.isDocx(mimeType)) {
                    return ResponseEntity.ok().contentType(mediaType)
                            .body(previewService.convertDocxToImage(resource));
                } else if (FileTypeUtil.isPdf(mimeType)) {
                    return ResponseEntity.ok().contentType(mediaType)
                            .body(previewService.convertPdfToImage(resource));
                }
                return ResponseEntity.ok().contentType(mediaType)
                        .body(resource);
            }
            return badRequest(errorHandler.buildError(ErrorCode.FILETYPE_MAPPING_INVALID, ErrorInfo.builder()
                    .errors(mimeType)
                    .build()));
        } catch (Exception e) {
            return unprocessableEntity(errorHandler.buildError(ErrorCode.INTERNAL_SERVER_ERROR, ErrorInfo.builder()
                    .message(e.getMessage())
                    .build()));
        }
    }

    @PostMapping(value = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> uploadFiles(@ModelAttribute @Valid FileUploadRequest request) {
        try {
            List<MultipartFile> files = request.getFiles();
            return ResponseEntity.ok().body(fileService.saveFile(files));
        } catch (Exception e) {
            return unprocessableEntity(errorHandler.buildError(ErrorCode.INTERNAL_SERVER_ERROR, ErrorInfo.builder()
                    .message(e.getMessage())
                    .build()));
        }
    }

    private FileResponse findFile(Integer attachFile) throws Exception {
        byte[] fileBytes = fileService.findFile(attachFile);
        ByteArrayResource resource = new ByteArrayResource(fileBytes);
        String mimeType = this.findMimeType(fileBytes);
        return FileResponse.of(resource, mimeType);
    }

    private String findMimeType(byte[] fileBytes) {
        Tika tika = new Tika();
        return tika.detect(fileBytes);
    }

    private ResponseEntity<ResponseHandler> badRequest(ErrorResponse response) {
        return ResponseEntity.badRequest().body(ResponseHandler.error(response));
    }

    private ResponseEntity<ErrorResponse> unprocessableEntity(ErrorResponse response) {
        return ResponseEntity.unprocessableEntity().body(response);
    }
}
