package com.resftul.dscommerce.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.*;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.NONE;

@NoArgsConstructor
@Getter
@Setter
@Entity(name = "User")
@Table(
        name = "tb_user",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_tb_user_email",
                columnNames = "email"
        )
)
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String phone;
    private LocalDate birthDate;
    private String password;

    @OneToMany(mappedBy = "client")
    @Setter(NONE)
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

    public User(String email, String encodedPassword) {
        this.email = email;
        this.password = encodedPassword;
    }

    public User(
            String name,
            String email,
            String phone,
            LocalDate birthDate,
            String encodedPassword
    ) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.birthDate = birthDate;
        this.password = encodedPassword;
    }

    public User(
            Long id,
            String name,
            String email,
            String phone,
            LocalDate birthDate,
            String password
    ) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.birthDate = birthDate;
        this.password = password;
    }

    public User(Long id, String name, String email, Role[] roles) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.roles = new HashSet<>(Arrays.asList(roles));
    }

    public User(
            String name,
            String email,
            String phone,
            LocalDate birthDate,
            String rawPassword,
            Role[] roles
    ) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.birthDate = birthDate;
        this.password = rawPassword;
        this.roles = new HashSet<>(Arrays.asList(roles));
    }

    public void addRole(Role role) {
        this.roles.add(role);
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
    public final boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;

        Class<?> oEffectiveClass = obj instanceof HibernateProxy
                ? ((HibernateProxy) obj).getHibernateLazyInitializer().getPersistentClass()
                : obj.getClass();

        Class<?> thisEffectiveClass = this instanceof HibernateProxy
                ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass()
                : this.getClass();

        if (thisEffectiveClass != oEffectiveClass) return false;

        User user = (User) obj;

        return getId() != null && Objects.equals(getId(), user.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy
                ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
                : getClass().hashCode();
    }
}