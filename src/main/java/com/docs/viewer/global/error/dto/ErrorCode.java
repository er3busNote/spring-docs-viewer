package com.docs.viewer.global.error.dto;

import lombok.Getter;

@Getter
public enum ErrorCode {

    FILETYPE_MAPPING_INVALID("CM_007", "지원되지 않는 파일타입입니다.", 400),
    INTERNAL_SERVER_ERROR("CM_100", "서버 에러.", 500);

    private final String code;
    private final String message;
    private final int status;

    ErrorCode(String code, String message, int status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}
