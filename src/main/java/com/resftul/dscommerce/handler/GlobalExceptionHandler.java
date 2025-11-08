package com.resftul.dscommerce.handler;

import com.resftul.dscommerce.exception.ProductAlreadyExistsException;
import com.resftul.dscommerce.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EntityNotFoundException;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpServerErrorException.InternalServerError;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

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
            BadRequestException badRequestException,
            WebRequest webRequest
    ) {
        ErrorDetails errorDetails = new ErrorDetails(
                now(),
                badRequestException.getMessage(),
                webRequest.getDescription(false),
                "BAD_REQUEST"
        );

        return new ResponseEntity<>(List.of(errorDetails), BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @Schema(description = "Manipula a exceção MethodArgumentNotValidException, lançada em caso de erros de validação.")
    public ResponseEntity<List<ValidationErrorDetails>> handleValidationException(
            MethodArgumentNotValidException methodArgumentNotValidException,
            WebRequest webRequest
    ) {
        List<ValidationErrorDetails> errors = new ArrayList<>();
        for (FieldError fieldError : methodArgumentNotValidException.getBindingResult().getFieldErrors()) {
            errors.add(
                    new ValidationErrorDetails(
                            now(),
                            fieldError.getDefaultMessage(),
                            webRequest.getDescription(false),
                            "METHOD_ARGUMENT_NOT_VALID_ERROR",
                            fieldError.getField()
                    )
            );
        }
        return ResponseEntity.status(BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @Schema(description = "Manipula erros de conversão de parâmetros de método, por exemplo, path variables com tipo inválido.")
    public ResponseEntity<List<ErrorDetails>> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException methodArgumentTypeMismatchException,
            WebRequest webRequest
    ) {
        ErrorDetails errorDetails = new ErrorDetails(
                now(),
                "Invalid value for parameter '%s': '%s'".formatted(
                        methodArgumentTypeMismatchException.getName(),
                        String.valueOf(methodArgumentTypeMismatchException.getValue())
                ),
                webRequest.getDescription(false),
                "METHOD_ARGUMENT_TYPE_MISMATCH"
        );

        return ResponseEntity.status(BAD_REQUEST).body(List.of(errorDetails));
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<List<ErrorDetails>> handleNotAcceptable(
            HttpMediaTypeNotAcceptableException httpMediaTypeNotAcceptableException,
            WebRequest webRequest
    ) {
        ErrorDetails errorDetails = new ErrorDetails(
                now(),
                httpMediaTypeNotAcceptableException.getMessage(),
                webRequest.getDescription(false),
                "NOT_ACCEPTABLE"
        );
        return new ResponseEntity<>(List.of(errorDetails), NOT_ACCEPTABLE);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @Schema(description = "Manipula a exceção IllegalArgumentException, lançada quando um argumento inválido é passado.")
    public ResponseEntity<List<ErrorDetails>> handleIllegalArgumentException(
            IllegalArgumentException illegalArgumentException,
            WebRequest webRequest
    ) {
        ErrorDetails errorDetails = new ErrorDetails(
                now(),
                illegalArgumentException.getMessage(),
                webRequest.getDescription(false),
                "INVALID_ARGUMENT"
        );

        return new ResponseEntity<>(List.of(errorDetails), BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @Schema(description = "Manipula a exceção EntityNotFoundException, lançada quando uma entidade não é encontrada.")
    public ResponseEntity<List<ErrorDetails>> handleEntityNotFoundException(
            EntityNotFoundException entityNotFoundException,
            WebRequest webRequest
    ) {
        ErrorDetails errorDetails = new ErrorDetails(
                now(),
                entityNotFoundException.getMessage(),
                webRequest.getDescription(false),
                "RESOURCE_NOT_FOUND"
        );

        return new ResponseEntity<>(List.of(errorDetails), NOT_FOUND);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @Schema(description = "Manipula a exceção ResourceNotFoundException, lançada quando um recurso não é encontrado.")
    public ResponseEntity<List<ErrorDetails>> handleResourceNotFoundException(
            ResourceNotFoundException resourceNotFoundException,
            WebRequest webRequest
    ) {
        ErrorDetails errorDetails = new ErrorDetails(
                now(),
                resourceNotFoundException.getMessage(),
                webRequest.getDescription(false),
                "RESOURCE_NOT_FOUND"
        );

        return new ResponseEntity<>(List.of(errorDetails), NOT_FOUND);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @Schema(description = "Manipula exceções que indicam que o tipo de mídia da requisição não é suportado.")
    public ResponseEntity<List<ErrorDetails>> handleUnsupportedMediaTypeException(
            HttpMediaTypeNotSupportedException httpMediaTypeNotSupportedException,
            WebRequest webRequest
    ) {
        ErrorDetails errorDetails = new ErrorDetails(
                now(),
                httpMediaTypeNotSupportedException.getMessage(),
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

    @ExceptionHandler(ProductAlreadyExistsException.class)
    @Schema(description = "Manipula violação de unicidade para produto já existente.")
    public ResponseEntity<List<ErrorDetails>> handleProductAlreadyExistsException(
            ProductAlreadyExistsException productAlreadyExistsException,
            WebRequest webRequest
    ) {
        ErrorDetails errorDetails = new ErrorDetails(
                now(),
                productAlreadyExistsException.getMessage(),
                webRequest.getDescription(false),
                "CONFLICT"
        );
        return new ResponseEntity<>(List.of(errorDetails), CONFLICT);
    }
}