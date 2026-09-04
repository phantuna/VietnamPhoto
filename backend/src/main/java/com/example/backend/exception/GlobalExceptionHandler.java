package com.example.backend.exception;

import com.example.backend.dto.response.ApiResponse;
import com.example.backend.dto.response.ErrorItemResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.springframework.security.access.AccessDeniedException;
import java.util.List;
import java.util.Locale;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception e) {
        log.error("Unhandled Exception: ", e);
        ApiResponse<Object> response = new ApiResponse<>();
        response.setCode(ErrorCode.INTERNAL_SERVER_ERROR.getCode());
        response.setMessage(ErrorCode.INTERNAL_SERVER_ERROR.getMessage());
        return ResponseEntity.internalServerError().body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<?>> handleAccessDeniedException(AccessDeniedException e) {
        log.warn("Access Denied: {}", e.getMessage());
        ApiResponse<Object> response = new ApiResponse<>();
        response.setCode(ErrorCode.UNAUTHENTICATED.getCode());
        response.setMessage("Bạn không có quyền truy cập chức năng này.");
        return ResponseEntity.status(403).body(response);
    }
    @Autowired
    private MessageSource messageSource;

//    @ExceptionHandler(value = AppException.class)
//    ResponseEntity<ApiResponse> handlingAppException(AppException e){
//        ErrorCode errorCode = e.getErrorCode();
//
//        ApiResponse response = new ApiResponse<>();
//        response.setCode(errorCode.getCode());
//        response.setMessage(errorCode.getMessage());
//        return ResponseEntity.badRequest().body(response);
//    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidation(MethodArgumentNotValidException e) {
        Locale locale = LocaleContextHolder.getLocale();

        List<ErrorItemResponse> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> {
                    String messageKey = err.getDefaultMessage(); 

                    String localizedMessage = messageSource.getMessage(
                            messageKey,
                            null,
                            locale
                    );

                    return new ErrorItemResponse(
                            ErrorCode.VALIDATION_FAILED.getCode(),
                            localizedMessage
                    );
                })
                .toList();

        ApiResponse<Object> response = new ApiResponse<>();
        response.setCode(ErrorCode.VALIDATION_FAILED.getCode());
        response.setMessage(ErrorCode.VALIDATION_FAILED.getMessage());
        response.setResult(errors);

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse> handlingAppLangException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();

        Locale locale = LocaleContextHolder.getLocale();
        String localizedMessage;
        try {
            localizedMessage = messageSource.getMessage(
                    errorCode.getMessage(),
                    null,
                    locale
            );
        } catch (Exception e) {
            localizedMessage = errorCode.getMessage();
        }

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(localizedMessage);
        apiResponse.setResult(exception.getData());

        return ResponseEntity.badRequest().body(apiResponse);
    }
}

