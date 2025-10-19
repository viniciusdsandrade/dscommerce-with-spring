package com.resftul.dscommerce.handler;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Schema(description = "Detalhes de um erro de validação ocorrido durante o processamento de uma requisição.")
public class ValidationErrorDetails extends ErrorDetails {
    private String field;

    public ValidationErrorDetails(
            LocalDateTime timestamp,
            String message,
            String details,
            String errorCode,
            String field
    ) {
        super(timestamp, message, details, errorCode);
        this.field = field;
    }
}