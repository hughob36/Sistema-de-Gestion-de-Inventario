package com.auth.service.auth_service.exception;

import com.auth.service.auth_service.dto.ErrorResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GlobalHandlerException Unit Tests")
class GlobalHandlerExceptionTest {

    private GlobalHandlerException exceptionHandler;

    @Mock
    private BindingResult bindingResult;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalHandlerException();
    }

    // =========================================================================
    // ResourceNotFoundException (404 NOT_FOUND)
    // =========================================================================
    @Nested
    @DisplayName("Tests for ResourceNotFoundException")
    class ResourceNotFoundExceptionTests {

        @Test
        @DisplayName("Should return 404 NOT_FOUND with exception message and null details")
        void shouldReturnNotFoundWithCorrectMessage() {
            // Arrange
            String errorMessage = "User with id 10 not found.";
            ResourceNotFoundException ex = new ResourceNotFoundException(errorMessage);

            // Act
            ResponseEntity<ErrorResponseDTO> response = exceptionHandler.handleNotFoundException(ex);

            // Assert
            assertAll("Verify 404 response payload and status",
                    () -> assertNotNull(response, "Response entity should not be null"),
                    () -> assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Status code must be 404"),
                    () -> assertNotNull(response.getBody(), "Response body should not be null"),
                    () -> assertEquals(errorMessage, response.getBody().message(), "Message should match exception message"),
                    () -> assertNull(response.getBody().details(), "Details should be null")
            );
        }
    }

    // =========================================================================
    // DataIntegrityViolationException (409 CONFLICT)
    // =========================================================================
    @Nested
    @DisplayName("Tests for DataIntegrityViolationException")
    class DataIntegrityViolationExceptionTests {

        @Test
        @DisplayName("Should return 409 CONFLICT with predefined friendly message")
        void shouldReturnConflictWithFixedMessage() {
            // Arrange
            DataIntegrityViolationException ex = new DataIntegrityViolationException("Duplicate key error: unique constraint violated");

            // Act
            ResponseEntity<ErrorResponseDTO> response = exceptionHandler.handleDataIntegrityViolationException(ex);

            // Assert
            assertAll("Verify 409 response payload and status",
                    () -> assertNotNull(response),
                    () -> assertEquals(HttpStatus.CONFLICT, response.getStatusCode(), "Status code must be 409"),
                    () -> assertNotNull(response.getBody()),
                    () -> assertEquals("The resource already exists or cannot be created/updated.", response.getBody().message()),
                    () -> assertNull(response.getBody().details(), "Details should be null")
            );
        }
    }

    // =========================================================================
    // BadCredentialsException (401 UNAUTHORIZED)
    // =========================================================================
    @Nested
    @DisplayName("Tests for BadCredentialsException")
    class BadCredentialsExceptionTests {

        @Test
        @DisplayName("Should return 401 UNAUTHORIZED with generic invalid credentials message")
        void shouldReturnUnauthorizedWithGenericMessage() {
            // Arrange
            BadCredentialsException ex = new BadCredentialsException("Bad credentials provided");

            // Act
            ResponseEntity<ErrorResponseDTO> response = exceptionHandler.handleBadCredentialsException(ex);

            // Assert
            assertAll("Verify 401 response payload and status",
                    () -> assertNotNull(response),
                    () -> assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode(), "Status code must be 401"),
                    () -> assertNotNull(response.getBody()),
                    () -> assertEquals("Invalid username or password.", response.getBody().message()),
                    () -> assertNull(response.getBody().details(), "Details should be null to avoid leaking internals")
            );
        }
    }

    // =========================================================================
    // UsernameNotFoundException (401 UNAUTHORIZED)
    // =========================================================================
    @Nested
    @DisplayName("Tests for UsernameNotFoundException")
    class UsernameNotFoundExceptionTests {

        @Test
        @DisplayName("Should return 401 UNAUTHORIZED and mask user non-existence with generic credentials message")
        void shouldReturnUnauthorizedAndMaskUserAbsence() {
            // Arrange
            UsernameNotFoundException ex = new UsernameNotFoundException("User 'admin' does not exist");

            // Act
            ResponseEntity<ErrorResponseDTO> response = exceptionHandler.handleUsernameNotFoundException(ex);

            // Assert
            assertAll("Verify 401 response masking username existence",
                    () -> assertNotNull(response),
                    () -> assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode(), "Status code must be 401"),
                    () -> assertNotNull(response.getBody()),
                    () -> assertEquals("Invalid username or password.", response.getBody().message()),
                    () -> assertNull(response.getBody().details())
            );
        }
    }

    // =========================================================================
    // MethodArgumentNotValidException (400 BAD_REQUEST)
    // =========================================================================
    @Nested
    @DisplayName("Tests for MethodArgumentNotValidException")
    class ValidationExceptionsTests {

        // Dummy method used purely to obtain a valid MethodParameter via reflection
        void sampleMethod(String param) {}

        @Test
        @DisplayName("Should return 400 BAD_REQUEST with field error map when validation fails")
        void shouldReturnBadRequestWithFieldErrorsMap() throws NoSuchMethodException {
            // Arrange
            Method method = this.getClass().getDeclaredMethod("sampleMethod", String.class);
            MethodParameter methodParameter = new MethodParameter(method, 0);

            FieldError fieldError1 = new FieldError("permissionRequestDTO", "permissionName", "must not be blank");
            FieldError fieldError2 = new FieldError("permissionRequestDTO", "role", "must be specified");

            when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));

            MethodArgumentNotValidException ex = new MethodArgumentNotValidException(methodParameter, bindingResult);

            // Act
            ResponseEntity<ErrorResponseDTO> response = exceptionHandler.handleValidationExceptions(ex);

            // Assert
            assertAll("Verify 400 validation response payload",
                    () -> assertNotNull(response),
                    () -> assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Status code must be 400"),
                    () -> assertNotNull(response.getBody()),
                    () -> assertEquals("Validation failed", response.getBody().message()),
                    () -> assertNotNull(response.getBody().details(), "Field errors details map must not be null"),
                    () -> assertEquals(2, response.getBody().details().size(), "Map should contain exactly 2 errors"),
                    () -> assertEquals("must not be blank", response.getBody().details().get("permissionName")),
                    () -> assertEquals("must be specified", response.getBody().details().get("role"))
            );
        }
    }

    // =========================================================================
    // General Exception (500 INTERNAL_SERVER_ERROR)
    // =========================================================================
    @Nested
    @DisplayName("Tests for unhandled Exception fallback")
    class UnhandledExceptionTests {

        @Test
        @DisplayName("Should return 500 INTERNAL_SERVER_ERROR with generic message and null details")
        @SuppressWarnings("unchecked")
        void shouldReturnInternalServerErrorWithSanitizedBody() {
            // Arrange
            Exception ex = new NullPointerException("Fatal pointer dereference in internal logic");

            // Act
            ResponseEntity<?> response = exceptionHandler.handleException(ex);

            // Assert
            assertAll("Verify 500 generic fallback response",
                    () -> assertNotNull(response),
                    () -> assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode(), "Status code must be 500"),
                    () -> assertNotNull(response.getBody()),
                    () -> assertTrue(response.getBody() instanceof Map, "Body must be a Map"),
                    () -> {
                        Map<String, Object> body = (Map<String, Object>) response.getBody();
                        assertEquals("An unexpected error occurred on the server.", body.get("message"));
                        assertNull(body.get("details"), "Details must be null to prevent internal leakage");
                    }
            );
        }
    }
}