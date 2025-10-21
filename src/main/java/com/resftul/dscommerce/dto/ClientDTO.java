package com.resftul.dscommerce.dto;

import com.resftul.dscommerce.entity.Users;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@EqualsAndHashCode
@NoArgsConstructor
@Getter
public class ClientDTO {
    private Long id;
    private String name;

    public ClientDTO(Users entity) {
        id = entity.getId();
        name = entity.getFirstName();
    }
}
