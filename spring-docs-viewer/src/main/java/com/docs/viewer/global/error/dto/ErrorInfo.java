package com.docs.viewer.global.error.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorInfo {

    private Object unsupported;
    private Object supported;
    private Object errors;
    private String errcode;
    private String message;
    private String exceptionName;
}
