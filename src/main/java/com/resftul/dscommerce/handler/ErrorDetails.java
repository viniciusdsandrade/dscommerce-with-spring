package com.resftul.dscommerce.handler;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(description = "Detalhes de um erro ocorrido durante o processamento de uma requisição.")
public class ErrorDetails {
    @Schema(description = "Data e hora em que o erro ocorreu.")
    @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    @Schema(description = "Mensagem de erro que descreve o problema ocorrido.")
    private String message;

    @Schema(description = "Detalhes adicionais sobre o erro.")
    private String details;

    @Schema(description = "Código ou tipo do erro ocorrido.")
    private String errorCode;
}