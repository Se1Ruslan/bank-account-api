package com.bank.bankaccountapi.errorhandler;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
public class ApiError {
    private String status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private LocalDateTime timestamp;
    private String message;
    private String requestInfo;

    private ApiError() {
        timestamp = LocalDateTime.now();
    }

    public ApiError(HttpStatus status, Throwable ex, String requestInfo) {
        this();
        this.status = status.toString();
        this.message = ex.getMessage();
        this.requestInfo = requestInfo;
    }

    public ApiError(HttpStatus status, String message, String requestInfo) {
        this();
        this.status = status.toString();
        this.message = message;
        this.requestInfo = requestInfo;
    }
}
