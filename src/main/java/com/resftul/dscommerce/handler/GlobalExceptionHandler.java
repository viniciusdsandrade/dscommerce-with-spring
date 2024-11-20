package com.resftul.dscommerce.handler;

import com.resftul.dscommerce.exception.ValidationErrorDetails;
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

/**
 * Classe responsável por tratar exceções de forma global na aplicação, proporcionando
 * uma resposta uniforme e amigável ao cliente em caso de erros.
 * <p>
 * Esta classe utiliza as anotações do Spring para interceptar exceções lançadas
 * nos controladores REST e retornar mensagens de erro apropriadas ao cliente.
 * <p>
 * Exceções específicas, como {@link EntityNotFoundException},
 * são manipuladas de maneira a fornecer feedback claro sobre o que deu errado na requisição.
 * </p>
 *
 * @see RestControllerAdvice
 * @see ExceptionHandler
 */
@RestControllerAdvice(basePackages = "com.restful.desafio_locadora_de_veiculos_solutis_school_dev_trail.controller")
@Schema(description = "Classe responsável por tratar exceções globalmente na aplicação.")
public class GlobalExceptionHandler {

    /**
     * Manipula a exceção {@link BadRequestException}, que é lançada quando uma requisição malformada
     * ou inválida é recebida pelo servidor.
     * <p>
     * Esta exceção indica que a requisição não pôde ser processada devido a um erro na sintaxe
     * ou no formato dos dados fornecidos. O metodo encapsula os detalhes do erro em um objeto
     * {@link ErrorDetails} e retorna uma resposta com status HTTP 400 (Bad Request),
     * indicando que a requisição não pôde ser entendida ou processada pelo servidor.
     * </p>
     *
     * @param exception  A exceção de requisição malformada, que contém a mensagem de erro a ser retornada ao cliente.
     * @param webRequest O objeto {@link WebRequest} que fornece informações adicionais sobre a requisição que causou a exceção.
     * @return Uma {@link ResponseEntity} contendo uma lista com os detalhes do erro encapsulados em {@link ErrorDetails}
     * e o status HTTP 400 (Bad Request).
     */
    @ExceptionHandler(BadRequestException.class)
    @Schema(description = "Manipula a exceção BadRequestException, lançada quando uma requisição malformada é recebida.")
    public ResponseEntity<List<ErrorDetails>> handleBadRequestException(BadRequestException exception,
                                                                        WebRequest webRequest) {
        ErrorDetails errorDetails = new ErrorDetails(
                now(),
                exception.getMessage(),
                webRequest.getDescription(false),
                "BAD_REQUEST"
        );

        return new ResponseEntity<>(List.of(errorDetails), BAD_REQUEST);
    }

    /**
     * Manipula a exceção {@link MethodArgumentNotValidException}, que é lançada quando ocorre um erro de validação
     * dos dados de entrada de um metodo de um controlador.
     * <p>
     * Esta exceção é comum em operações onde os dados fornecidos pelo cliente, através do corpo da requisição
     * ou parâmetros de URL,  não atendem aos requisitos de validação definidos pelas anotações do Bean Validation,
     * como @NotNull, @NotBlank, @Size, etc.
     * </p>
     * <p>
     * O metodo captura a exceção, extrai os detalhes dos erros de validação e os encapsula em uma lista de
     * objetos {@link ValidationErrorDetails}. Cada objeto ValidationErrorDetails contém informações específicas
     * sobre um erro de validação, como o campo que causou o erro, a mensagem de erro e o código de erro.
     * </p>
     * <p>
     * Em seguida, o metodo retorna uma resposta HTTP com o status 400 (Bad Request) e a lista de erros de
     * validação no corpo da resposta, no formato JSON. Essa resposta informa ao cliente quais campos da requisição
     * são inválidos e quais são as mensagens de erro correspondentes, permitindo que o cliente corrija os erros e
     * reenvie a requisição.
     * </p>
     *
     * @param exception A exceção {@link MethodArgumentNotValidException} que contém os detalhes dos erros de validação.
     * @param request   O objeto {@link WebRequest} que fornece informações adicionais sobre a requisição que causou a exceção.
     * @return Uma {@link ResponseEntity} contendo uma lista de {@link ValidationErrorDetails} e o status HTTP 400 (Bad Request).
     * @see ValidationErrorDetails
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @Schema(description = "Manipula a exceção MethodArgumentNotValidException, lançada em caso de erros de validação.")
    public ResponseEntity<List<ValidationErrorDetails>> handleValidationException(MethodArgumentNotValidException exception,
                                                                                  WebRequest request) {
        List<ValidationErrorDetails> errors = new ArrayList<>();
        for (FieldError error : exception.getBindingResult().getFieldErrors()) {
            errors.add(new ValidationErrorDetails(
                    now(),
                    error.getDefaultMessage(), // Mensagem mais amigável do Bean Validation
                    request.getDescription(false),
                    "METHOD_ARGUMENT_NOT_VALID_ERROR",
                    error.getField() // Nome do campo com erro
            ));
        }
        return ResponseEntity.status(BAD_REQUEST).body(errors);
    }

    /**
     * Manipula a exceção {@link IllegalArgumentException}, que é lançada quando um argumento inválido
     * é passado para um metodo.
     * <p>
     * Esta exceção indica que os dados fornecidos pelo cliente na requisição não são válidos
     * para a operação solicitada. O metodo encapsula os detalhes do erro em um objeto
     * {@link ErrorDetails} e retorna uma resposta com status HTTP 400 (Bad Request),
     * indicando que a requisição não pôde ser processada devido a dados inválidos.
     * </p>
     *
     * @param exception  A exceção de argumento inválido, que contém a mensagem de erro a ser retornada ao cliente.
     * @param webRequest O objeto {@link WebRequest} que fornece informações adicionais sobre a requisição que causou a exceção.
     * @return Uma {@link ResponseEntity} contendo uma lista com os detalhes do erro encapsulados em {@link ErrorDetails}
     * e o status HTTP 400 (Bad Request).
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @Schema(description = "Manipula a exceção IllegalArgumentException, lançada quando um argumento inválido é passado.")
    public ResponseEntity<List<ErrorDetails>> handleIllegalArgumentException(IllegalArgumentException exception,
                                                                             WebRequest webRequest) {
        ErrorDetails errorDetails = new ErrorDetails(
                now(),
                exception.getMessage(),
                webRequest.getDescription(false),
                "INVALID_ARGUMENT"
        );

        return new ResponseEntity<>(List.of(errorDetails), BAD_REQUEST);
    }


    /**
     * Manipula a exceção {@link EntityNotFoundException}, que é lançada quando uma entidade
     * requisitada não é encontrada no banco de dados.
     * <p>
     * Esta exceção é comum em operações de busca ou atualização, onde o identificador fornecido
     * não corresponde a nenhuma entidade existente. O metodo encapsula os detalhes do erro em
     * um objeto {@link ErrorDetails} e retorna uma resposta com status HTTP 404 (Not Found),
     * indicando que o recurso requisitado não pôde ser localizado.
     * </p>
     *
     * @param exception  A exceção de entidade não encontrada, que contém a mensagem de erro a ser retornada ao cliente.
     * @param webRequest O objeto {@link WebRequest} que fornece informações adicionais sobre a requisição que causou a exceção.
     * @return Uma {@link ResponseEntity} contendo uma lista com os detalhes do erro encapsulados em {@link ErrorDetails}
     * e o status HTTP 404 (Not Found).
     */
    @ExceptionHandler(EntityNotFoundException.class)
    @Schema(description = "Manipula a exceção EntityNotFoundException, lançada quando uma entidade não é encontrada.")
    public ResponseEntity<List<ErrorDetails>> handleEntityNotFoundException(EntityNotFoundException exception,
                                                                            WebRequest webRequest) {
        ErrorDetails errorDetails = new ErrorDetails(
                now(),
                exception.getMessage(),
                webRequest.getDescription(false),
                "RESOURCE_NOT_FOUND"
        );

        return new ResponseEntity<>(List.of(errorDetails), NOT_FOUND);
    }

    /**
     * Manipula exceções que indicam que o tipo de mídia da requisição não é suportado,
     * correspondente ao código de status HTTP 415 (Unsupported Media Type).
     * <p>
     * Este metodo captura exceções do tipo {@link HttpMediaTypeNotSupportedException}, que
     * geralmente são lançadas quando o cliente envia uma requisição com um tipo de conteúdo
     * (Content-Type) que o servidor não consegue processar.
     * </p>
     * <p>
     * O metodo encapsula os detalhes do erro em um objeto {@link ErrorDetails} e retorna uma resposta
     * com a mensagem "Tipo de mídia não suportado" e o status HTTP 415. Essa resposta informa ao
     * cliente que o servidor não consegue processar a requisição com o tipo de conteúdo enviado.
     * </p>
     *
     * @param exception  A exceção que representa o tipo de mídia não suportado.
     * @param webRequest O objeto {@link WebRequest} que fornece informações adicionais sobre a requisição que causou a exceção.
     * @return Uma {@link ResponseEntity} contendo uma lista com os detalhes do erro encapsulados em {@link ErrorDetails}
     * e o status HTTP 415 (Unsupported Media Type).
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @Schema(description = "Manipula exceções que indicam que o tipo de mídia da requisição não é suportado.")
    public ResponseEntity<List<ErrorDetails>> handleUnsupportedMediaTypeException(HttpMediaTypeNotSupportedException exception,
                                                                                  WebRequest webRequest) {
        ErrorDetails errorDetails = new ErrorDetails(
                now(),
                exception.getMessage(),
                webRequest.getDescription(false),
                "UNSUPPORTED_MEDIA_TYPE"
        );

        return new ResponseEntity<>(List.of(errorDetails), UNSUPPORTED_MEDIA_TYPE);
    }

    /**
     * Manipula exceções genéricas do tipo {@link Exception}, que representam erros inesperados
     * que podem ocorrer durante o processamento de uma requisição.
     * <p>
     * Este metodo atua como um "catch-all" para exceções não tratadas especificamente por outros
     * metodos de manipulação de exceções. Ele captura qualquer exceção do tipo Exception e retorna
     * uma resposta com status HTTP 500 (Internal Server Error), indicando que houve um erro interno
     * no servidor que impediu o processamento da requisição.
     * </p>
     * <p>
     * O metodo encapsula os detalhes do erro em um objeto {@link ErrorDetails} e retorna uma resposta
     * com a mensagem "Erro interno no servidor" e o status HTTP 500. Essa resposta informa ao
     * cliente que houve um problema no servidor, mas não expõe detalhes específicos sobre a causa
     * do erro, por questões de segurança e para evitar vazamento de informações sensíveis.
     * </p>
     *
     * @param exception  A exceção genérica que representa o erro interno no servidor.
     * @param webRequest O objeto {@link WebRequest} que fornece informações adicionais sobre a requisição que causou a exceção.
     * @return Uma {@link ResponseEntity} contendo uma lista com os detalhes do erro encapsulados em {@link ErrorDetails}
     * e o status HTTP 500 (Internal Server Error).
     */
    @ExceptionHandler(InternalServerError.class)
    @Schema(description = "Manipula exceções genéricas, representando erros inesperados durante o processamento da requisição.")
    public ResponseEntity<List<ErrorDetails>> handleGlobalException(Exception exception,
                                                                    WebRequest webRequest) {
        ErrorDetails errorDetails = new ErrorDetails(
                now(),
                exception.getMessage(),
                webRequest.getDescription(false),
                "INTERNAL_SERVER_ERROR"
        );

        return new ResponseEntity<>(List.of(errorDetails), INTERNAL_SERVER_ERROR);
    }

    /**
     * Manipula exceções que indicam que uma funcionalidade não está implementada,
     * correspondente ao código de status HTTP 501 (Not Implemented).
     * <p>
     * Este metodo captura exceções do tipo {@link UnsupportedOperationException}, que
     * geralmente são lançadas quando um metodo ou recurso ainda não foi implementado
     * na aplicação.
     * </p>
     * <p>
     * O metodo encapsula os detalhes do erro em um objeto {@link ErrorDetails} e retorna uma resposta
     * com a mensagem "Funcionalidade não implementada" e o status HTTP 501. Essa resposta informa ao
     * cliente que a funcionalidade solicitada ainda não está disponível.
     * </p>
     *
     * @param exception  A exceção que representa a funcionalidade não implementada.
     * @param webRequest O objeto {@link WebRequest} que fornece informações adicionais sobre a requisição que causou a exceção.
     * @return Uma {@link ResponseEntity} contendo uma lista com os detalhes do erro encapsulados em {@link ErrorDetails}
     * e o status HTTP 501 (Not Implemented).
     */
    @ExceptionHandler(UnsupportedOperationException.class)
    @Schema(description = "Manipula exceções que indicam que uma funcionalidade não está implementada.")
    public ResponseEntity<List<ErrorDetails>> handleNotImplementedException(UnsupportedOperationException exception,
                                                                            WebRequest webRequest) {
        ErrorDetails errorDetails = new ErrorDetails(
                now(),
                exception.getMessage(),
                webRequest.getDescription(false),
                "NOT_IMPLEMENTED"
        );

        return new ResponseEntity<>(List.of(errorDetails), NOT_IMPLEMENTED);
    }
}