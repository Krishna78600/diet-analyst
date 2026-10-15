package com.example.diet.exception;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    public record ApiError(int status, String error, String message) {}

    private ResponseEntity<ApiError> build(HttpStatus s, String error, String msg) {
        return ResponseEntity.status(s).body(new ApiError(s.value(), error, msg));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> invalid(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return build(HttpStatus.BAD_REQUEST, "INVALID_INPUT", msg);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> unreadable(HttpMessageNotReadableException ex) {
                return build(HttpStatus.BAD_REQUEST, "MALFORMED_JSON", ex.getMostSpecificCause().getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> ai(Exception ex) {
        String m = String.valueOf(ex.getMessage());
        if (m.contains("429") || m.toLowerCase().contains("rate limit")) {
            return build(HttpStatus.TOO_MANY_REQUESTS, "RATE_LIMITED",
                    "Groq rate limit reached. Retry in a minute.");
        }
        if (ex.getClass().getName().contains("Json") || m.contains("JSON")) {
            return build(HttpStatus.BAD_GATEWAY, "LLM_PARSE_ERROR",
                    "AI returned an unreadable response. Please retry.");
        }
        return build(HttpStatus.BAD_GATEWAY, "AI_UNAVAILABLE", "AI analysis failed. Please retry.");
    }
}