package com.resftul.dscommerce.repository;

import com.resftul.dscommerce.entity.*;
import com.resftul.dscommerce.entity.Order;
import jakarta.persistence.PersistenceUnitUtil;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import static com.resftul.dscommerce.entity.OrderStatus.WAITING_PAYMENT;
import static java.math.BigDecimal.valueOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.ANY;

@DataJpaTest
@AutoConfigureTestDatabase(replace = ANY)
@ActiveProfiles("h2")
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

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
    void setup() {
        persistenceUnitUtil = testEntityManager.getEntityManager()
                .getEntityManagerFactory()
                .getPersistenceUnitUtil();
    }

    @AfterEach
    void clear() {
        testEntityManager.clear();
    }

    private Role role() {
        Role role = new Role("ROLE_CLIENT");
        return testEntityManager.persistAndFlush(role);
    }

    private User user(String name, String email, Role... roles) {
        var user = new User(
                name,
                email,
                "+5519999999999",
                LocalDate.of(2000, 1, 1),
                "{noop}pwd",
                roles
        );
        for (Role role : roles) user.getRoles().add(role);
        return testEntityManager.persistAndFlush(user);
    }

    private Product product(String name, BigDecimal price) {
        var product = new Product(null, name, "desc " + name, price, "https://img");
        return testEntityManager.persistAndFlush(product);
    }

    private Order order(User client, Instant moment) {
        var order = new Order(client, moment, WAITING_PAYMENT);
        return testEntityManager.persistAndFlush(order);
    }

    private void item(Order order, Product product, int quantity, BigDecimal price) {
        var orderItem = new OrderItem(order, product, quantity, price);
        order.addItem(orderItem);
        testEntityManager.persistAndFlush(orderItem);
    }

    @Test
    @DisplayName("findById: retorna presente quando id existe e carrega dados essenciais")
    void findById_present_whenExists() {
        var client = role();
        var user = user("Ana", "ana@example.com", client);
        var product1 = product("Notebook", valueOf(100.00));
        var product2 = product("Mouse", valueOf(50.00));
        var order = order(user, Instant.parse("2024-01-10T10:15:30Z"));
        item(order, product1, 2, product1.getPrice());
        item(order, product2, 3, product2.getPrice());
        testEntityManager.clear();

        var opt = orderRepository.findById(order.getId());
        assertThat(opt).isPresent();

        var found = opt.orElseThrow();
        assertThat(found.getId()).isEqualTo(order.getId());
        assertThat(found.getOrderStatus()).isEqualTo(WAITING_PAYMENT);
        assertThat(found.getClient().getEmail()).isEqualTo("ana@example.com");
        assertThat(found.getItems()).hasSize(2);
        assertThat(found.getProducts()).extracting(Product::getName)
                .containsExactlyInAnyOrder("Notebook", "Mouse");
    }

    @Test
    @DisplayName("findById: retorna vazio quando id não existe")
    void findById_empty_whenNotExists() {
        var client = role();
        var user = user("Bob", "bob@example.com", client);
        var order = order(user, Instant.now());
        testEntityManager.clear();

        assertThat(orderRepository.findById(order.getId() + 9999L)).isEmpty();
    }

    @Test
    @DisplayName("items (OneToMany): LAZY inicialmente; carrega ao acessar")
    void items_lazy_then_loaded_on_access() {
        var client = role();
        var user = user("Carol", "carol@example.com", client);
        var product = product("Teclado", valueOf(199.90));
        var order = order(user, Instant.now());
        item(order, product, 1, product.getPrice());
        testEntityManager.clear();

        var found = orderRepository.findById(order.getId()).orElseThrow();
        assertThat(persistenceUnitUtil.isLoaded(found, "items")).as("items should be LAZY").isFalse();
        assertThat(found.getItems()).hasSize(1);
        assertThat(persistenceUnitUtil.isLoaded(found, "items")).as("items should be loaded after access").isTrue();
    }

    @Test
    @DisplayName("client (ManyToOne): carregado imediatamente (EAGER por padrão)")
    void client_eager_loaded() {
        var client = role();
        var user = user("Diego", "diego@example.com", client);
        var order = order(user, Instant.now());
        testEntityManager.clear();

        var found = orderRepository.findById(order.getId()).orElseThrow();
        assertThat(persistenceUnitUtil.isLoaded(found, "client")).isTrue();
        assertThat(found.getClient().getEmail()).isEqualTo("diego@example.com");
    }

    @Test
    @DisplayName("status (Enum): persiste e recupera corretamente")
    void status_enum_persisted_and_read() {
        var client = role();
        var user = user("Eva", "eva@example.com", client);
        var order = order(user, Instant.now());
        testEntityManager.clear();

        var found = orderRepository.findById(order.getId()).orElseThrow();
        assertThat(found.getOrderStatus()).isEqualTo(WAITING_PAYMENT);
    }
}
