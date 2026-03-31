package com.nisum.challenge.infrastructure.exception;

import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.nisum.challenge.adapter.in.controller.UserController;
import com.nisum.challenge.domain.exception.AuthenticationException;
import com.nisum.challenge.domain.exception.EmailAlreadyExistsException;
import com.nisum.challenge.domain.exception.NotFoundException;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(UserController.class);

	@ExceptionHandler(EmailAlreadyExistsException.class)
	public ResponseEntity<Map<String, String>> handleEmailAlreadyExists(EmailAlreadyExistsException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", ex.getMessage()));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, String>> handleGeneric(Exception ex) {
		log.error("Internal Server Error. ", ex);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(Map.of("mensaje", "An unexpected error has occurred."));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
		Map<String, String> errores = ex.getBindingResult().getFieldErrors().stream()
				.collect(Collectors.toMap(fieldError -> fieldError.getField(), // nombre del campo
						DefaultMessageSourceResolvable::getDefaultMessage, // mensaje de error
						(mensaje1, mensaje2) -> mensaje1 // por si se repite un campo
				));
		return ResponseEntity.badRequest().body(errores);
	}

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<Map<String, String>> handleAuthException(AuthenticationException ex) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", ex.getMessage()));
	}

	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<Map<String, String>> handleNotFound(NotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", ex.getMessage()));
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<Map<String, String>> handleInvalidUUID(MethodArgumentTypeMismatchException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Invalid user ID format"));
	}

	@ExceptionHandler(HandlerMethodValidationException.class)
	public ResponseEntity<Map<String, String>> handleConstraintViolation(ConstraintViolationException ex) {
	    String mensaje = ex.getConstraintViolations().stream()
	            .map(v -> v.getPropertyPath() + ": " + v.getMessage())
	            .collect(Collectors.joining(", "));
	    return ResponseEntity.badRequest().body(Map.of("message", mensaje));
	}

}
