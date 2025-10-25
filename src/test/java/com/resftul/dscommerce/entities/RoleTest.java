package com.resftul.dscommerce.entities;

import com.resftul.dscommerce.entity.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RoleTest {

    @Test
    @DisplayName("getters/setters: preenchem e retornam id/authority")
    void getters_and_setters() {
        Role role = new Role();
        role.setId(10L);
        role.setAuthority("ROLE_ADMIN");

        assertThat(role.getId()).isEqualTo(10L);
        assertThat(role.getAuthority()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    @DisplayName("GrantedAuthority: getAuthority() expõe a string da role")
    void granted_authority_getAuthority() {
        Role role = new Role();
        role.setAuthority("ROLE_CLIENT");

        assertThat(role).isInstanceOf(org.springframework.security.core.GrantedAuthority.class);
        assertThat(role.getAuthority()).isEqualTo("ROLE_CLIENT");
    }

    @Test
    @DisplayName("equals/hashCode: iguais quando id é o mesmo; reflexivo/simétrico/transitivo")
    void equals_and_hashCode_same_id() {
        Role role1 = new Role();
        role1.setId(1L);
        role1.setAuthority("ROLE_X");

        Role role2 = new Role();
        role2.setId(1L);
        role2.setAuthority("ROLE_Y");

        assertThat(role1).isEqualTo(role2);
        assertThat(role1.hashCode()).isEqualTo(role2.hashCode());

        assertThat(role1).isEqualTo(role1);
        assertThat(role2).isEqualTo(role1);
    }

    @Test
    @DisplayName("equals: diferentes quando IDs diferentes (sem impor hashCode diferente)")
    void equals_different_when_ids_different() {
        Role role1 = new Role();
        role1.setId(1L);
        role1.setAuthority("ROLE_A");

        Role role2 = new Role();
        role2.setId(2L);
        role2.setAuthority("ROLE_A");

        assertThat(role1).isNotEqualTo(role2);

        assertThat(role1.hashCode()).isEqualTo(Role.class.hashCode());
        assertThat(role2.hashCode()).isEqualTo(Role.class.hashCode());
    }

    @Test
    @DisplayName("equals: com id nulo nunca é igual a um com id não nulo")
    void equals_null_id_behavior() {
        Role withNullId = new Role();
        withNullId.setAuthority("ROLE_X");

        Role withId = new Role();
        withId.setId(99L);
        withId.setAuthority("ROLE_X");

        assertThat(withNullId).isNotEqualTo(withId);
        Role anotherNull = new Role();
        assertThat(withNullId).isNotEqualTo(anotherNull);
    }

    @Test
    @DisplayName("equals: não é igual a null nem a outro tipo")
    void equals_not_equals_to_null_or_other_type() {
        Role role = new Role();
        role.setId(1L);
        role.setAuthority("ROLE_X");

        assertThat(role).isNotEqualTo(null);
        assertThat(role.equals(new Object())).isFalse();
    }
}
