package com.bank.bankaccountapi.errorhandler;

import com.bank.bankaccountapi.expection.AccountApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import static java.util.Objects.nonNull;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(AccountApiException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    protected ApiError handleAccountApiException(AccountApiException ex, WebRequest request) {
        log.error("Handled account Api exception", ex);
        return new ApiError(HttpStatus.BAD_REQUEST, ex, request.getDescription(false));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    protected ApiError handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest request) {
        log.error("Handled MethodArgumentNotValidException", ex);

        String defaultMessage = nonNull(ex.getBindingResult().getFieldError())
                ? ex.getBindingResult().getFieldError().getDefaultMessage()
                : null;
        String message = nonNull(defaultMessage) ? defaultMessage : ex.getMessage();
        return new ApiError(HttpStatus.BAD_REQUEST, message, request.getDescription(false));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    protected ApiError handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, WebRequest request) {
        log.error("Handled HttpMessageNotReadableException", ex);
        return new ApiError(HttpStatus.BAD_REQUEST, ex, request.getDescription(false));
    }


    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError globalExceptionHandler(Exception ex, WebRequest request) {
        log.error("Handled by globalExceptionHandler", ex);
        return new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, ex, request.getDescription(false));
    }


}
