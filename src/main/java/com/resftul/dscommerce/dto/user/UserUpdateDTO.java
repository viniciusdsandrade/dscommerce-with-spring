package com.resftul.dscommerce.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UserUpdateDTO(
        @Size(min = 2, max = 120)
        String name,

        @Email @Size(max = 120)
        String email,

        @Size(min = 8, max = 30)
        String phone,

        @Past
        LocalDate birthDate
) {}