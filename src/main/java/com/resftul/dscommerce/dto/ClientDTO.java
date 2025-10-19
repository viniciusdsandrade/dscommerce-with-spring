package com.resftul.dscommerce.dto;

import com.resftul.dscommerce.entity.Users;

public class ClientDTO {
    private Long id;
    private String name;

    public ClientDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public ClientDTO(Users entity) {
        id = entity.getId();
        name = entity.getFirstName();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
