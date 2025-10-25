package com.resftul.dscommerce.entities;

import com.resftul.dscommerce.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class UserTest {
    private static void setPrivateId(Object target, Long id) {
        try {
            Field f = target.getClass().getDeclaredField("id");
            f.setAccessible(true);
            f.set(target, id);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError("Falha ao setar id por reflexão", e);
        }
    }

    private static User newUser(String name, String email) {
        User u = new User();
        u.setName(name);
        u.setEmail(email);
        u.setPhone("9999-0000");
        u.setBirthDate(LocalDate.of(1990, 1, 1));
        u.setPassword("{noop}secret");
        return u;
    }

    @Test
    @DisplayName("equals/hashCode: iguais quando id é o mesmo; diferentes quando id difere ou é nulo")
    void equals_hashCode_based_on_id() {
        User a = newUser("A", "a@x.com");
        User b = newUser("B", "b@x.com");
        setPrivateId(a, 10L);
        setPrivateId(b, 10L);

        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());

        User c = newUser("C", "c@x.com");
        setPrivateId(c, 11L);
        assertThat(a).isNotEqualTo(c);

        User x = newUser("X", "x@x.com");
        User y = newUser("Y", "y@x.com");
        assertThat(x).isNotEqualTo(y);

        assertThat(a).isEqualTo(a);
    }

    @Test
    @DisplayName("UserDetails: getUsername retorna email; flags padrões são false")
    void userDetails_contract() {
        User u = newUser("Alice", "alice@acme.com");

        assertThat(u.getUsername()).isEqualTo("alice@acme.com");

        assertThat(u.isAccountNonExpired()).isFalse();
        assertThat(u.isAccountNonLocked()).isFalse();
        assertThat(u.isCredentialsNonExpired()).isFalse();
        assertThat(u.isEnabled()).isFalse();
    }

    @Test
    @DisplayName("Coleções: orders e roles não são nulas e começam vazias")
    void collections_are_non_null_and_initially_empty() {
        User u = newUser("Carol", "carol@acme.com");
        assertThat(u.getOrders()).isNotNull().isEmpty();
        assertThat(u.getRoles()).isNotNull().isEmpty();
    }
}
