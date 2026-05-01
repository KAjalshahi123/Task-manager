package com.taskmanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ✅ 1. Validation errors (DTO @Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        for (var error : ex.getBindingResult().getAllErrors()) {
            String field = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(field, message);
        }

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // ✅ 2. Bad Request (custom)
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // ✅ 3. Not Found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // ✅ 4. Unauthorized
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    // ✅ 5. Runtime exception (IMPORTANT ADD)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntime(RuntimeException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // ✅ 6. Generic fallback (last)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    // 🔧 Common response builder
    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message) {

        ErrorResponse error = ErrorResponse.builder()
                .status(status.value())
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(error, status);
    }
}