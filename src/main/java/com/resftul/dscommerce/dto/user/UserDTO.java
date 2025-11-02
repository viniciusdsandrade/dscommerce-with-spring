package com.resftul.dscommerce.dto.user;


import com.resftul.dscommerce.dto.RoleDTO;
import com.resftul.dscommerce.entity.Role;
import com.resftul.dscommerce.entity.User;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static lombok.AccessLevel.NONE;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private LocalDate birthDate;
    @Setter(NONE)
    private Set<RoleDTO> roles = new HashSet<>();

    public UserDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.birthDate = user.getBirthDate();
        for (Role role : user.getRoles()) {
            this.roles.add(new RoleDTO(role));
        }
    }

    public UserDTO(long id, String name, String email, String phone, LocalDate birthDate, RoleDTO... roles) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.birthDate = birthDate;
        if (roles != null) {
            for (RoleDTO role : roles) {
                if (role != null) this.roles.add(role);
            }
        }
    }
}