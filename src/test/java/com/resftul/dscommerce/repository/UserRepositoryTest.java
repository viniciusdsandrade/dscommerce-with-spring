package com.resftul.dscommerce.repository;

import com.resftul.dscommerce.entity.Role;
import com.resftul.dscommerce.entity.User;
import com.resftul.dscommerce.projections.UserDetailsProjection;
import jakarta.persistence.PersistenceUnitUtil;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.ANY;

@DataJpaTest
@AutoConfigureTestDatabase(replace = ANY)
@ActiveProfiles("h2")
class UserRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private PersistenceUnitUtil persistenceUnitUtil;

    @TestConfiguration
    static class SecurityTestConfig {
        @Bean
        PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }

    @BeforeEach
    void setUp() {
        persistenceUnitUtil = testEntityManager.getEntityManager()
                .getEntityManagerFactory()
                .getPersistenceUnitUtil();
    }

    @AfterEach
    void clear() {
        testEntityManager.clear();
    }

    private Role role(String authority) {
        Role role = new Role(null, authority);
        return testEntityManager.persistAndFlush(role);
    }

    private User user(String name, String email, String rawPassword, Role... roles) {
        User user = new User(
                name,
                email,
                "+5519999999999",
                LocalDate.of(2000, 1, 1),
                rawPassword,
                roles
        );
        for (Role role : roles) user.getRoles().add(role);
        return testEntityManager.persistAndFlush(user);
    }

    @Test
    @DisplayName("findByEmail: retorna Optional presente quando email existe")
    void findByEmail_present_whenExists() {
        var client = role("ROLE_CLIENT");
        var saved = user("Ana", "ana@example.com", "{noop}pwd", client);
        testEntityManager.clear();

        var opt = userRepository.findByEmail("ana@example.com");
        assertThat(opt).isPresent();
        var found = opt.orElseThrow();
        assertThat(found.getId()).isEqualTo(saved.getId());
        assertThat(found.getEmail()).isEqualTo("ana@example.com");
    }

    @Test
    @DisplayName("findByEmail: retorna Optional vazio quando email não existe")
    void findByEmail_empty_whenNotExists() {
        user("Ana", "ana@example.com", "{noop}pwd", role("ROLE_CLIENT"));
        testEntityManager.clear();

        assertThat(userRepository.findByEmail("missing@example.com")).isEmpty();
    }

    @Test
    @DisplayName("save: viola UK de email ao inserir duplicado -> DataIntegrityViolationException")
    void save_throwsDataIntegrity_onDuplicateEmail() {
        var client = role("ROLE_CLIENT");
        var user1 = new User(
                "Ana",
                "ana@example.com",
                "+5519999999999",
                LocalDate.of(2000, 1, 1),
                "{noop}pwd"
        );
        user1.getRoles().add(client);
        userRepository.saveAndFlush(user1);

        var user2 = new User(
                "Bruno",
                "ana@example.com",
                "+5519999999999",
                LocalDate.of(2000, 1, 1),
                "{noop}pwd2"
        );
        user2.getRoles().add(client);

        assertThrows(DataIntegrityViolationException.class, () -> userRepository.saveAndFlush(user2));
    }

    @Test
    @DisplayName("searchUserAndRolesByEmail: retorna uma linha por role do usuário")
    void searchUserAndRolesByEmail_returnsOneRowPerRole() {
        var client = role("ROLE_CLIENT");
        var admin = role("ROLE_ADMIN");
        user("Carol", "carol@example.com", "{noop}secret", client, admin);
        testEntityManager.clear();

        List<UserDetailsProjection> rows = userRepository.searchUserAndRolesByEmail("carol@example.com");

        assertThat(rows).hasSize(2);
        assertThat(rows).extracting(UserDetailsProjection::getUsername)
                .containsOnly("carol@example.com");
        assertThat(rows).extracting(UserDetailsProjection::getPassword)
                .containsOnly("{noop}secret");
        assertThat(rows).extracting(UserDetailsProjection::getAuthority)
                .containsExactlyInAnyOrder("ROLE_CLIENT", "ROLE_ADMIN");
        assertThat(rows).extracting(UserDetailsProjection::getRoleId)
                .containsExactlyInAnyOrder(client.getId(), admin.getId());
    }

    @Test
    @DisplayName("searchUserAndRolesByEmail: retorna lista vazia quando email não existe")
    void searchUserAndRolesByEmail_empty_whenEmailNotFound() {
        user("Diego", "diego@example.com", "{noop}pwd", role("ROLE_CLIENT"));
        testEntityManager.clear();

        List<UserDetailsProjection> rows = userRepository.searchUserAndRolesByEmail("missing@example.com");
        assertThat(rows).isEmpty();
    }

    @Test
    @DisplayName("roles (ManyToMany): estado lazy inicialmente; carrega ao acessar")
    void roles_lazy_then_loaded_on_access() {
        var client = role("ROLE_CLIENT");
        var saved = user("Eva", "eva@example.com", "{noop}pwd", client);
        testEntityManager.clear();

        User found = userRepository.findById(saved.getId()).orElseThrow();
        assertThat(persistenceUnitUtil.isLoaded(found, "roles")).isFalse();
        assertThat(found.getRoles()).hasSize(1);
        assertThat(persistenceUnitUtil.isLoaded(found, "roles")).isTrue();
    }
}
