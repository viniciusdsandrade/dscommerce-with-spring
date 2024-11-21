package com.resftul.dscommerce.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * Exceção lançada quando um recurso não é encontrado.
 * <p>
 * Esta exceção é anotada com {@code @ResponseStatus(HttpStatus.NOT_FOUND)}, indicando
 * que resultará em uma resposta HTTP 404 (Not Found) quando lançada.
 */
@ResponseStatus(NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Construtor que inicializa a mensagem de erro da exceção.
     *
     * @param message A mensagem de erro que descreve o problema ocorrido.
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}