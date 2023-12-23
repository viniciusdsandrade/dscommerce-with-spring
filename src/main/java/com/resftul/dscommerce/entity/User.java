package com.resftul.dscommerce.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDate;
import java.util.*;

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

    @OneToMany(mappedBy = "client")
    @Setter(AccessLevel.NONE)
    private List<Order> orders = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "tb_user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();


    public void addRole(Role role) {
        this.roles.add(role);
    }

    public boolean hasRole(String roleName) {
        for (Role role : roles) {
            if (role.getAuthority().equals(roleName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;
        if (this.getClass() != o.getClass()) return false;

        User that = (User) o;

        return Objects.equals(this.id, that.id) &&
                Objects.equals(this.email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.email);
    }

    @Override
    public String toString() {
        return "{\n" +
                "  \"id\": " + this.id +
                ",\n  \"name\": \"" + this.name + '\"' +
                ",\n  \"email\": \"" + this.email + '\"' +
                ",\n  \"phone\": \"" + this.phone + '\"' +
                ",\n  \"birthDate\": \"" + this.birthDate + '\"' +
                ",\n  \"password\": \"" + this.password + '\"' +
                ",\n  \"roles\": \"" + this.roles + '\"' +
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