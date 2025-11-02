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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import jakarta.persistence.PersistenceUnitUtil;
import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.ANY;

@DataJpaTest
@AutoConfigureTestDatabase(replace = ANY)
@ActiveProfiles("h2")
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

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
        Category category = new Category(name);
        return testEntityManager.persistAndFlush(category);
    }

        private void prod(
                BigDecimal price,
                Category... categories
        ) {
            var product = new Product(
                    "Notebook",
                    "d",
                    price,
                    null,
                    categories
            );
            testEntityManager.persistAndFlush(product);
        }

    @Test
    @DisplayName("products (ManyToMany): LAZY inicialmente; carrega ao acessar")
    void products_lazy_then_loaded_on_access() {
        var info = cat("Informática");
        prod(new BigDecimal("5499.90"), info);
        testEntityManager.clear();

        Category found = categoryRepository.findById(info.getId()).orElseThrow();
        assertThat(persistenceUnitUtil.isLoaded(found, "products")).isFalse();
        assertThat(found.getProducts()).hasSize(1);
        assertThat(persistenceUnitUtil.isLoaded(found, "products")).isTrue();
    }

    @Test
    @DisplayName("findById: retorna Optional presente quando o id existe")
    void findById_returnsPresentOptional_whenIdExists() {
        var games = cat("Games");
        testEntityManager.clear();

        var opt = categoryRepository.findById(games.getId());
        assertThat(opt).isPresent();
        var found = opt.orElseThrow();
        assertThat(found.getId()).isEqualTo(games.getId());
        assertThat(found.getName()).isEqualTo("Games");
    }

    @Test
    @DisplayName("findById: retorna Optional vazio quando o id não existe")
    void findById_returnsEmptyOptional_whenIdDoesNotExist() {
        var saved = cat("Acessórios");
        long missingId = saved.getId() + 9999L;
        testEntityManager.clear();

        assertThat(categoryRepository.findById(missingId)).isEmpty();
    }

    @Test
    @DisplayName("findById(null): traduz IllegalArgumentException em InvalidDataAccessApiUsageException")
    void findById_throwsInvalidDataAccessApiUsageException_whenIdIsNull() {
        var exception = assertThrows(InvalidDataAccessApiUsageException.class,
                () -> categoryRepository.findById(null));
        assertThat(exception.getMostSpecificCause())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must not be null");
    }

    @Test
    @DisplayName("findAll: retorna todas as categorias (ordem não garantida)")
    void findAll_returnsAll() {
        var category1 = cat("Informática");
        var category2 = cat("Eletrônicos");
        var category3 = cat("Periféricos");
        testEntityManager.clear();

        List<Category> all = categoryRepository.findAll();

        assertThat(all).extracting(Category::getName)
                .containsExactlyInAnyOrder(category1.getName(), category2.getName(), category3.getName());
    }

    @Test
    @DisplayName("save: viola uk_category_name ao tentar inserir nome duplicado")
    void save_throwsDataIntegrityViolation_onDuplicateName() {
        categoryRepository.saveAndFlush(new Category(null, "Informática", null)); // id, name, products(null)
        assertThrows(DataIntegrityViolationException.class, () -> {
            categoryRepository.saveAndFlush(new Category(null, "Informática", null));
        });
    }
}

