package com.resftul.dscommerce.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.resftul.dscommerce.validation.annotation.StrongPassword;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;

public record UserInsertDTO(
        @NotBlank(message = "First name is required")
        @Size(min = 2, max = 60, message = "First name must be between 2 and 60 characters")
        @Pattern(regexp = "^[\\p{L} .'-]{2,60}$", message = "First name contains invalid characters")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        @Size(max = 120, message = "Email must be at most 120 characters")
        String email,

        @NotBlank @Size(min = 8, max = 30)
        String phone,

        @NotBlank(message = "Password is required")
        @Size(min = 6, max = 60, message = "Password must be between 6 and 60 characters")
        @JsonProperty(access = WRITE_ONLY)
        @StrongPassword()
        String password,

        @NotNull @Past
        LocalDate birthDate
) {
}

