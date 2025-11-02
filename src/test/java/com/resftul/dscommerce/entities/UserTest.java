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
            Field field = target.getClass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(target, id);
        } catch (ReflectiveOperationException exception) {
            throw new AssertionError("Falha ao setar id por reflexão", exception);
        }
    }

    private static User newUser(String name, String email) {
        return new User(
                name,
                email,
                "9999-0000",
                LocalDate.of(1990, 1, 1),
                "{noop}secret"
        );
    }

    @Test
    @DisplayName("equals/hashCode: iguais quando id é o mesmo; diferentes quando id difere ou é nulo")
    void equals_hashCode_based_on_id() {
        User user1 = newUser("A", "a@x.com");
        User user2 = newUser("B", "b@x.com");
        setPrivateId(user1, 10L);
        setPrivateId(user2, 10L);

        assertThat(user1).isEqualTo(user2);
        assertThat(user1.hashCode()).isEqualTo(user2.hashCode());

        User user3 = newUser("C", "c@x.com");
        setPrivateId(user3, 11L);
        assertThat(user1).isNotEqualTo(user3);

        User x = newUser("X", "x@x.com");
        User y = newUser("Y", "y@x.com");
        assertThat(x).isNotEqualTo(y);

        assertThat(user1).isEqualTo(user1);
    }

    @Test
    @DisplayName("UserDetails: getUsername retorna email; flags padrões são false")
    void userDetails_contract() {
        User user = newUser("Alice", "alice@acme.com");

        assertThat(user.getUsername()).isEqualTo("alice@acme.com");

        assertThat(user.isAccountNonExpired()).isFalse();
        assertThat(user.isAccountNonLocked()).isFalse();
        assertThat(user.isCredentialsNonExpired()).isFalse();
        assertThat(user.isEnabled()).isFalse();
    }

    @Test
    @DisplayName("Coleções: orders e roles não são nulas e começam vazias")
    void collections_are_non_null_and_initially_empty() {
        User user = newUser("Carol", "carol@acme.com");
        assertThat(user.getOrders()).isNotNull().isEmpty();
        assertThat(user.getRoles()).isNotNull().isEmpty();
    }
}
