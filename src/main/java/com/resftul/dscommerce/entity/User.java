package com.resftul.dscommerce.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "User")
@Table(name = "tb_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_name", nullable = false)
    private String name;

    @Email
    @Column(name = "user_email", nullable = false, unique = true)
    private String email;

    @Column(name = "user_phone")
    @Pattern(regexp = "^[0-9]{2}\\s*[0-9]{5}-?[0-9]{4}$")
    private String phone;

    @Column(name = "user_birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "user_password", nullable = false)
    private String password;

    @Column(name = "user_roles", nullable = false)
    private String roles;

    @OneToMany(mappedBy = "client")
    @Setter(AccessLevel.NONE)
    private List<Order> orders = new ArrayList<>();

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;
        if (this.getClass() != o.getClass()) return false;

        User user = (User) o;

        return Objects.equals(id, user.id) &&
                Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }

    @Override
    public String toString() {
        return "{\n" +
                "  \"id\": " + id +
                ",\n  \"name\": \"" + name + '\"' +
                ",\n  \"email\": \"" + email + '\"' +
                ",\n  \"phone\": \"" + phone + '\"' +
                ",\n  \"birthDate\": \"" + birthDate + '\"' +
                ",\n  \"password\": \"" + password + '\"' +
                ",\n  \"roles\": \"" + roles + '\"' +
                "\n}";
    }
    
    public User(User user) {
        this.id = user.id;
        this.name = user.name;
        this.email = user.email;
        this.phone = user.phone;
        this.birthDate = user.birthDate;
        this.password = user.password;
        this.roles = user.roles;

        // Crie um novo conjunto para garantir uma cópia profunda
        this.orders = new ArrayList<>(user.orders.size());
        for (Order order : user.orders) {
            this.orders.add(new Order(order));
        }
    }
}