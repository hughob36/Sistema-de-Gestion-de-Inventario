package com.auth.service.auth_service.exception;

import com.auth.service.auth_service.dto.ErrorResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalHandlerException {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotFoundException(ResourceNotFoundException ex) {
        // WARN level: expected business flow exception, not an application crash
        log.warn("Resource not found: {}", ex.getMessage());
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponseDTO);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDTO> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        // ERROR level: persistence/database constraint violation
        log.error("Data integrity violation: ", ex);
        ErrorResponseDTO errorResponse = new ErrorResponseDTO("The resource already exists or cannot be created/updated.");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponseDTO> handleBadCredentialsException(BadCredentialsException ex) {
        // WARN level: invalid authentication attempt
        log.warn("Authentication failed - Invalid credentials: {}", ex.getMessage());
        ErrorResponseDTO errorResponse = new ErrorResponseDTO("Invalid username or password.");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        // WARN level: useful for monitoring potential brute force attacks
        log.warn("Authentication failed - Username not found: {}", ex.getMessage());
        ErrorResponseDTO errorResponse = new ErrorResponseDTO("Invalid username or password.");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception ex) {
        // ERROR level: log full stack trace for unhandled errors
        log.error("Unexpected internal server error: ", ex);
        Map<String, Object> body = new HashMap<>();
        body.put("message", "An unexpected error occurred on the server.");
        body.put("details", null); // never leak internal details to the client
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        log.warn("Validation failed for input data: {} error(s) found", errors.size());
        ErrorResponseDTO errorResponse = new ErrorResponseDTO("Validation failed", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}