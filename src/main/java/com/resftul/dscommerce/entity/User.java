package com.resftul.dscommerce.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Consumer;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.NONE;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity(name = "User")
@Table(name = "tb_user")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private String name;

    @Column(unique = true)
    private String email;
    private String phone;
    private LocalDate birthDate;
    private String password;

    @OneToMany(mappedBy = "client")
    private List<Order> orders = new ArrayList<>();

    @ManyToMany(fetch = LAZY)
    @JoinTable(
            name = "tb_user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Setter(NONE)
    @BatchSize(size = 50)
    private Set<Role> roles = new HashSet<>();

    public void initializeProfile(String name, String normalizedEmail, String passwordHash) {
        this.name = requireNonBlank(name, "name");
        this.email = requireNonBlank(normalizedEmail, "email");
        this.password = requireNonBlank(passwordHash, "passwordHash");
    }

    public void updateProfile(String firstName, String normalizedEmail) {
        applyIfPresent(firstName, "name", v -> this.name = v);
        applyIfPresent(normalizedEmail, "email", v -> this.email = v);
    }

    private static String requireNonBlank(String v, String field) {
        if (v == null) throw new IllegalArgumentException(field + " required");
        v = v.strip();
        if (v.isEmpty()) throw new IllegalArgumentException(field + " blank");
        return v;
    }

    private static void applyIfPresent(String value, String field, Consumer<String> apply) {
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
}