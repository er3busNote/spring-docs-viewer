package com.docs.viewer.global.common.dto.response;

import com.docs.viewer.global.error.dto.response.ErrorResponse;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseHandler {

    private int status;

    private String message;

    public ResponseHandler(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public static ResponseHandler error(ErrorResponse response) {
        return ResponseHandler.builder()
                .status(response.getStatus())
                .message(response.getMessage())
                .build();
    }
}
