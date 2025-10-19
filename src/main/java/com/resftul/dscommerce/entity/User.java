package com.resftul.dscommerce.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.NONE;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "User")
@Table(name = "tb_user")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String email;

    @ToString.Exclude
    private String password;

    @BatchSize(size = 50)
    @ToString.Exclude
    @Setter(NONE)
    @JoinTable(
            name = "tb_user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @ManyToMany(fetch = LAZY)
    private Set<Role> roles = new HashSet<>();

    public void initializeProfile(String firstName, String lastName, String normalizedEmail, String passwordHash) {
        this.firstName = requireNonBlank(firstName, "firstName");
        this.lastName = requireNonBlank(lastName, "lastName");
        this.email = requireNonBlank(normalizedEmail, "email");
        this.password = requireNonBlank(passwordHash, "passwordHash");
    }

    public void updateProfile(String firstName, String lastName, String normalizedEmail) {
        applyIfPresent(firstName, "firstName", v -> this.firstName = v);
        applyIfPresent(lastName, "lastName", v -> this.lastName = v);
        applyIfPresent(normalizedEmail, "email", v -> this.email = v);
    }

    private static String requireNonBlank(String v, String field) {
        if (v == null) throw new IllegalArgumentException(field + " required");
        v = v.strip();
        if (v.isEmpty()) throw new IllegalArgumentException(field + " blank");
        return v;
    }

    private static void applyIfPresent(String value, String field, java.util.function.Consumer<String> apply) {
        if (value == null) return;
        String v = value.strip();
        if (v.isEmpty()) throw new IllegalArgumentException(field + " blank");
        apply.accept(v);
    }

    public void addRole(Role referenceById) {
        this.roles.add(referenceById);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        User user = (User) o;
        return getId() != null && Objects.equals(getId(), user.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    public boolean hasRole(String roleAdmin) {
        for (Role role : roles) {
            if (role.getAuthority().equals(roleAdmin)) {
                return true;
            }
        }
        return false;
    }
}