package com.resftul.dscommerce.dto.user;


import com.resftul.dscommerce.dto.RoleDTO;
import com.resftul.dscommerce.entity.User;
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

    public UserDTO(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName  = user.getLastName();
        this.email     = user.getEmail();
        user.getRoles().forEach(role -> this.roles.add(new RoleDTO(role)));
    }
}