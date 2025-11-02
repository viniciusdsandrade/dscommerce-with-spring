package com.resftul.dscommerce.repository;

import com.resftul.dscommerce.entity.Category;
import com.resftul.dscommerce.entity.Product;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.persistence.PersistenceUnitUtil;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.ANY;

@DataJpaTest
@AutoConfigureTestDatabase(replace = ANY)
@ActiveProfiles("h2")
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

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


    private Category cat(String name) {
        var c = new Category();
        c.setName(name);
        return testEntityManager.persistAndFlush(c);
    }

    private Product prod(
            String name,
            BigDecimal price,
            String desc,
            String img,
            Category... categories
    ) {
        var product = new Product(
                null,
                name,
                desc,
                price,
                img
        );
        for (Category category : categories) product.getCategories().add(category);
        return testEntityManager.persistAndFlush(product);
    }

    @Test
    @DisplayName("ManyToMany categorias: LAZY inicialmente; carrega ao acessar")
    void categories_lazy_then_loaded_on_access() {
        var info = cat("Informática");
        var games = cat("Games");
        Product product = prod(
                "Notebook",
                new BigDecimal("5499.90"),
                "d",
                null,
                info,
                games
        );
        testEntityManager.clear();

        Product found = productRepository.findById(product.getId()).orElseThrow();
        assertThat(persistenceUnitUtil.isLoaded(found, "categories")).isFalse();
        assertThat(found.getCategories()).hasSize(2);
        assertThat(persistenceUnitUtil.isLoaded(found, "categories")).isTrue();
    }

    @Test
    @DisplayName("searchByName: LIKE case-insensitive e paginação")
    void searchByName_like_case_insensitive_and_paged() {
        prod("PC Gamer", new BigDecimal("3500.00"), "d", null);
        prod("pc office", new BigDecimal("2500.00"), "d", null);
        prod("Notebook", new BigDecimal("5499.90"), "d", null);
        testEntityManager.clear();

        Page<Product> page = productRepository.searchByName("pC", PageRequest.of(0, 2));

        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getSize()).isEqualTo(2);

        var names = page.map(Product::getName).getContent();
        assertThat(names).containsExactlyInAnyOrder("PC Gamer", "pc office");
    }

    @Test
    @DisplayName("searchByName: retorna vazio quando não há match")
    void searchByName_empty_when_no_match() {
        prod("Mouse USB", new BigDecimal("79.90"), "d", null);
        testEntityManager.clear();

        Page<Product> page = productRepository.searchByName("teclado", PageRequest.of(0, 5));
        assertThat(page.getTotalElements()).isZero();
        assertThat(page.getContent()).isEmpty();
    }

    @Test
    @DisplayName("findById: retorna Optional presente quando o id existe")
    void findById_returnsPresentOptional_whenIdExists() {
        var info = cat("Informática");
        var saved = prod("Teclado ABNT2", new BigDecimal("299.90"), "d", null, info);
        testEntityManager.clear();

        var opt = productRepository.findById(saved.getId());
        assertThat(opt).isPresent();
        var found = opt.orElseThrow();
        assertThat(found.getId()).isEqualTo(saved.getId());
        assertThat(found.getName()).isEqualTo("Teclado ABNT2");
        assertThat(found.getPrice()).isEqualByComparingTo("299.90");
    }

    @Test
    @DisplayName("findById: retorna Optional vazio quando o id não existe")
    void findById_returnsEmptyOptional_whenIdDoesNotExist() {
        var saved = prod("Mouse USB", new BigDecimal("79.90"), "d", null);
        long missingId = saved.getId() + 9999L;
        testEntityManager.clear();

        assertThat(productRepository.findById(missingId)).isEmpty();
    }

    @Test
    @DisplayName("findById(null): traduz IllegalArgumentException em InvalidDataAccessApiUsageException")
    void findById_throwsInvalidDataAccessApiUsageException_whenIdIsNull() {
        var ex = assertThrows(InvalidDataAccessApiUsageException.class,
                () -> productRepository.findById(null));
        assertThat(ex.getMostSpecificCause())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must not be null");
    }
}