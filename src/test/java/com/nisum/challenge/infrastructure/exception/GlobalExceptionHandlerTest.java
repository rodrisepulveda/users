package com.nisum.challenge.infrastructure.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.nisum.challenge.domain.exception.AuthenticationException;
import com.nisum.challenge.domain.exception.EmailAlreadyExistsException;
import com.nisum.challenge.domain.exception.NotFoundException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;

public class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleEmailAlreadyExists_debeRetornar409() {
        EmailAlreadyExistsException ex = new EmailAlreadyExistsException("Correo ya registrado");
        ResponseEntity<Map<String, String>> response = handler.handleEmailAlreadyExists(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Correo ya registrado", response.getBody().get("message"));
    }

    @Test
    void handleGeneric_debeRetornar500() {
        Exception ex = new Exception("Error interno");
        ResponseEntity<Map<String, String>> response = handler.handleGeneric(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An unexpected error has occurred.", response.getBody().get("mensaje"));
    }

    @Test
    void handleValidationExceptions_debeRetornar400ConErrores() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError nameError = new FieldError("obj", "name", "El nombre es obligatorio");
        FieldError emailError = new FieldError("obj", "email", "El correo no tiene un formato válido");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(nameError, emailError));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<Map<String, String>> response = handler.handleValidationExceptions(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("El nombre es obligatorio", response.getBody().get("name"));
        assertEquals("El correo no tiene un formato válido", response.getBody().get("email"));
    }

    @Test
    void handleAuthException_debeRetornar401() {
        AuthenticationException ex = new AuthenticationException("Token inválido");
        ResponseEntity<Map<String, String>> response = handler.handleAuthException(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Token inválido", response.getBody().get("message"));
    }

    @Test
    void handleNotFound_debeRetornar404() {
        NotFoundException ex = new NotFoundException("Usuario no encontrado");
        ResponseEntity<Map<String, String>> response = handler.handleNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Usuario no encontrado", response.getBody().get("message"));
    }

    @Test
    void handleInvalidUUID_debeRetornar400() {
        MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);
        ResponseEntity<Map<String, String>> response = handler.handleInvalidUUID(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid user ID format", response.getBody().get("message"));
    }

    @Test
    void handleConstraintViolation_debeRetornar400() {
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        Path path = mock(Path.class); // crear un mock de Path

        when(path.toString()).thenReturn("email"); // cuando se convierta a string, retorna "email"
        when(violation.getPropertyPath()).thenReturn(path); // retornar el mock
        when(violation.getMessage()).thenReturn("formato inválido");

        ConstraintViolationException ex = new ConstraintViolationException(Set.of(violation));
        ResponseEntity<Map<String, String>> response = handler.handleConstraintViolation(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().get("message").contains("email: formato inválido"));
    }

}