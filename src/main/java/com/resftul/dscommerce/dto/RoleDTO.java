package com.resftul.dscommerce.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.resftul.dscommerce.entity.Roles;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RoleDTO {

    @JsonProperty(access = WRITE_ONLY)
    private Long id;
    private String authority;

    public RoleDTO(Roles roles) {
        this.id = roles.getId();
        this.authority = roles.getAuthority();
    }
}
