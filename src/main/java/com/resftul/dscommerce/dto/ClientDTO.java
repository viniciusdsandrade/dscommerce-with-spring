package com.resftul.dscommerce.dto;

import com.resftul.dscommerce.entity.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@EqualsAndHashCode
@NoArgsConstructor
@Getter
public class ClientDTO {
    private Long id;
    private String name;

    public ClientDTO(User user) {
        id = user.getId();
        name = user.getName();
    }
}
