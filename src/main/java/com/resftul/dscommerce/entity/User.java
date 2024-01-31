package com.resftul.dscommerce.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.time.LocalDate;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "User")
@Table(name = "tb_user")
public class User implements UserDetails {

    @Serial
    private static final long serialVersionUID = 1L;

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
        roles.add(role);
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
        final int prime = 31;
        int hash = 1;

        hash *= prime + (this.id == null ? 0 : this.id.hashCode());
        hash *= prime + (this.email == null ? 0 : this.email.hashCode());

        if (hash < 0)
            hash *= -1;

        return hash;
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


        this.orders = new ArrayList<>(user.orders.size());
        for (Order order : user.orders) {
            this.orders.add(new Order(order));
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}