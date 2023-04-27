package com.david.express.exception;

import com.david.express.validation.ErrorResponseBuilder;
import com.david.express.validation.dto.ErrorResponseDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public @ResponseBody ResponseEntity<Map<String, ErrorResponseDTO>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorResponseDTO errors = new ErrorResponseDTO(
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                new Date()
        );
        return new ResponseEntity<>(ErrorResponseBuilder.build(errors), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = ResourceAffiliationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ResponseEntity<Map<String, ErrorResponseDTO>> handleUserNotResourceOwnerException(ResourceAffiliationException ex) {
        ErrorResponseDTO errors = new ErrorResponseDTO(
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                new Date()
        );
        return new ResponseEntity<>(ErrorResponseBuilder.build(errors), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public ResponseEntity<Map<String, ErrorResponseDTO>> handleResourceAlreadyExistsException(ResourceAlreadyExistsException ex) {
        ErrorResponseDTO errors = new ErrorResponseDTO(
                HttpStatus.CONFLICT.getReasonPhrase(),
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                new Date()
        );
        return new ResponseEntity<>(ErrorResponseBuilder.build(errors), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, ErrorResponseDTO>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        ErrorResponseDTO errors = new ErrorResponseDTO(
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                new Date()
        );
        return new ResponseEntity<>(ErrorResponseBuilder.build(errors), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public ResponseEntity<Map<String, ErrorResponseDTO>> handleAccessDeniedException(Exception ex) {
        System.out.println(HttpStatus.FORBIDDEN.value() + " : CA PASSE LA !");
        ErrorResponseDTO errors = new ErrorResponseDTO(
                HttpStatus.FORBIDDEN.getReasonPhrase(),
                HttpStatus.FORBIDDEN.value(),
                ex.getMessage(),
                new Date()
        );
        return new ResponseEntity<>(ErrorResponseBuilder.build(errors), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(InsufficientAuthenticationException.class)
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public ResponseEntity<Map<String, ErrorResponseDTO>> handleAuthenticationException(Exception ex) {
        System.out.println(HttpStatus.UNAUTHORIZED.value() + " : CA PASSE LA !");
        ErrorResponseDTO errors = new ErrorResponseDTO(
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                HttpStatus.UNAUTHORIZED.value(),
                ex.getMessage(),
                new Date()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ErrorResponseBuilder.build(errors));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        List<String> errorMessages = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getDefaultMessage())
                .collect(Collectors.toList());

        Map<String, Object> errors = new HashMap<>();
        Map<String, Object> response = new HashMap<>();
        errors.put("statusCode", HttpStatus.BAD_REQUEST.value());
        errors.put("status", HttpStatus.BAD_REQUEST.getReasonPhrase());
        errors.put("messages", errorMessages);
        errors.put("timestamp", new Date());
        response.put("errors", errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobalExceptions(Exception ex) {
        System.out.println(ex);
        ErrorResponseDTO errors = new ErrorResponseDTO(
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ex.getMessage(),
                new Date()
        );
        return new ResponseEntity<>(ErrorResponseBuilder.build(errors), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
