package com.resftul.dscommerce.handler;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EntityNotFoundException;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpServerErrorException.InternalServerError;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.List;

import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
@Schema(description = "Classe responsável por tratar exceções globalmente na aplicação.")
public class GlobalExceptionHandler {


    @ExceptionHandler(BadRequestException.class)
    @Schema(description = "Manipula a exceção BadRequestException, lançada quando uma requisição malformada é recebida.")
    public ResponseEntity<List<ErrorDetails>> handleBadRequestException(
            BadRequestException exception,
            WebRequest webRequest
    ) {
        ErrorDetails errorDetails = new ErrorDetails(
                now(),
                exception.getMessage(),
                webRequest.getDescription(false),
                "BAD_REQUEST"
        );

        return new ResponseEntity<>(List.of(errorDetails), BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @Schema(description = "Manipula a exceção MethodArgumentNotValidException, lançada em caso de erros de validação.")
    public ResponseEntity<List<ValidationErrorDetails>> handleValidationException(
            MethodArgumentNotValidException exception,
            WebRequest request) {
        List<ValidationErrorDetails> errors = new ArrayList<>();
        for (FieldError error : exception.getBindingResult().getFieldErrors()) {
            errors.add(new ValidationErrorDetails(
                    now(),
                    error.getDefaultMessage(),
                    request.getDescription(false),
                    "METHOD_ARGUMENT_NOT_VALID_ERROR",
                    error.getField()
            ));
        }
        return ResponseEntity.status(BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @Schema(description = "Manipula a exceção IllegalArgumentException, lançada quando um argumento inválido é passado.")
    public ResponseEntity<List<ErrorDetails>> handleIllegalArgumentException(
            IllegalArgumentException exception,
            WebRequest webRequest
    ) {
        ErrorDetails errorDetails = new ErrorDetails(
                now(),
                exception.getMessage(),
                webRequest.getDescription(false),
                "INVALID_ARGUMENT"
        );

        return new ResponseEntity<>(List.of(errorDetails), BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @Schema(description = "Manipula a exceção EntityNotFoundException, lançada quando uma entidade não é encontrada.")
    public ResponseEntity<List<ErrorDetails>> handleEntityNotFoundException(
            EntityNotFoundException exception,
            WebRequest webRequest
    ) {
        ErrorDetails errorDetails = new ErrorDetails(
                now(),
                exception.getMessage(),
                webRequest.getDescription(false),
                "RESOURCE_NOT_FOUND"
        );

        return new ResponseEntity<>(List.of(errorDetails), NOT_FOUND);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @Schema(description = "Manipula exceções que indicam que o tipo de mídia da requisição não é suportado.")
    public ResponseEntity<List<ErrorDetails>> handleUnsupportedMediaTypeException(
            HttpMediaTypeNotSupportedException exception,
            WebRequest webRequest
    ) {
        ErrorDetails errorDetails = new ErrorDetails(
                now(),
                exception.getMessage(),
                webRequest.getDescription(false),
                "UNSUPPORTED_MEDIA_TYPE"
        );

        return new ResponseEntity<>(List.of(errorDetails), UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(InternalServerError.class)
    @Schema(description = "Manipula exceções genéricas, representando erros inesperados durante o processamento da requisição.")
    public ResponseEntity<List<ErrorDetails>> handleGlobalException(
            Exception exception,
            WebRequest webRequest
    ) {
        ErrorDetails errorDetails = new ErrorDetails(
                now(),
                exception.getMessage(),
                webRequest.getDescription(false),
                "INTERNAL_SERVER_ERROR"
        );

        return new ResponseEntity<>(List.of(errorDetails), INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    @Schema(description = "Manipula exceções que indicam que uma funcionalidade não está implementada.")
    public ResponseEntity<List<ErrorDetails>> handleNotImplementedException(
            UnsupportedOperationException exception,
            WebRequest webRequest
    ) {
        ErrorDetails errorDetails = new ErrorDetails(
                now(),
                exception.getMessage(),
                webRequest.getDescription(false),
                "NOT_IMPLEMENTED"
        );

        return new ResponseEntity<>(List.of(errorDetails), NOT_IMPLEMENTED);
    }
}