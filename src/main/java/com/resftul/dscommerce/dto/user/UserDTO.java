package com.resftul.dscommerce.dto.user;


import com.resftul.dscommerce.dto.RoleDTO;
import com.resftul.dscommerce.entity.Users;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    @Setter(AccessLevel.NONE)
    private Set<RoleDTO> roles = new HashSet<>();

    public UserDTO(Users users) {
        this.id = users.getId();
        this.firstName = users.getFirstName();
        this.lastName  = users.getLastName();
        this.email     = users.getEmail();
        users.getRoles().forEach(role -> this.roles.add(new RoleDTO(role)));
    }
}