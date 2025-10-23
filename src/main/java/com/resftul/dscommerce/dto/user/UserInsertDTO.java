package com.resftul.dscommerce.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.resftul.dscommerce.config.MultiFormatLocalDateDeserializer;
import com.resftul.dscommerce.validation.annotation.StrongPassword;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;

public record UserInsertDTO(
        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 2, max = 60, message = "Nome deve ter entre 2 e 60 caracteres")
        @Pattern(regexp = "^[\\p{L} .'-]{2,60}$", message = "Nome contém caracteres inválidos")
        String name,

        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email inválido")
        @Size(max = 120, message = "Email deve ter no máximo 120 caracteres")
        String email,

        @NotBlank(message = "Telefone é obrigatório")
        @Size(min = 8, max = 30, message = "Telefone deve ter entre 8 e 30 caracteres")
        @Pattern(regexp = "^\\+?[1-9]\\d{7,14}$", message = "Phone must be a valid international number (E.164)")
        String phone,

        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 8, max = 60, message = "Senha deve ter entre 8 e 60 caracteres")
        @JsonProperty(access = WRITE_ONLY)
        @StrongPassword(message = "Senha deve conter ao menos uma letra maiúscula, uma letra minúscula, um número e um caractere especial")
        String password,

        @NotNull(message = "Data de nascimento é obrigatória")
        @Past(message = "Data de nascimento deve ser uma data no passado")
        @JsonDeserialize(using = MultiFormatLocalDateDeserializer.class)
        LocalDate birthDate
) {
}

