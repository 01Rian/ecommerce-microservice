package com.ecommerce.userapi.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorDto(
        int status,
        String message,
        String errorCode,
        @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
        LocalDateTime timestamp,
        Map<String, String> errors
) {
    public ErrorDto(int status, String message, String errorCode, LocalDateTime timestamp) {
        this(status, message, errorCode, timestamp, null);
    }
}
