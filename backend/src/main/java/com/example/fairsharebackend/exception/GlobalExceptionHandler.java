package com.example.fairsharebackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final String KEY_TIMESTAMP = "timestamp";
    private static final String KEY_STATUS = "status";
    private static final String KEY_ERROR = "error";
    private static final String KEY_MESSAGE = "message";

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAllExceptions(Exception ex) {
        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put(KEY_TIMESTAMP, LocalDateTime.now());
        errorBody.put(KEY_STATUS, HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorBody.put(KEY_ERROR, "Internal Server Error");
        errorBody.put(KEY_MESSAGE, ex.getMessage());
        return new ResponseEntity<>(errorBody, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put(KEY_TIMESTAMP, LocalDateTime.now());
        errorBody.put(KEY_STATUS, HttpStatus.CONFLICT.value());
        errorBody.put(KEY_ERROR, "Invalid Payload");


        String errorMessage = ex.getBindingResult().getAllErrors()
                .stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining(". "));

        errorBody.put(KEY_MESSAGE, errorMessage);
        return new ResponseEntity<>(errorBody, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentialsExceptions(BadCredentialsException ex) {
        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put(KEY_TIMESTAMP, LocalDateTime.now());
        errorBody.put(KEY_STATUS, HttpStatus.CONFLICT.value());
        errorBody.put(KEY_ERROR, "Bad Credentials");
        errorBody.put(KEY_MESSAGE, ex.getMessage());
        return new ResponseEntity<>(errorBody, HttpStatus.CONFLICT);
    }
}
