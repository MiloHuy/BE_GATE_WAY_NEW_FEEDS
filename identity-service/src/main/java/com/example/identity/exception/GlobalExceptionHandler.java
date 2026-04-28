package com.example.identity.exception;

import com.example.identity.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = AppException.class)
    public ResponseEntity<ApiResponse<Object>> handlingAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        ApiResponse<Object> apiResponse = new ApiResponse<>();

        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());
        apiResponse.setStatus("error");

        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<ApiResponse<Object>> handlingRuntimeException(RuntimeException exception) {
        ApiResponse<Object> apiResponse = new ApiResponse<>();

        apiResponse.setCode(9999);
        apiResponse.setMessage(exception.getMessage());
        apiResponse.setStatus("error");

        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ApiResponse<Object>> handlingException(Exception exception) {
        ApiResponse<Object> apiResponse = new ApiResponse<>();

        apiResponse.setCode(5000);
        apiResponse.setMessage("Internal server error: " + exception.getMessage());
        apiResponse.setStatus("error");

        return ResponseEntity.status(500).body(apiResponse);
    }
}
