package com.resftul.dscommerce.entities;

import com.resftul.dscommerce.entity.Category;
import com.resftul.dscommerce.entity.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import jakarta.persistence.ManyToMany;

import java.util.Set;

import static jakarta.persistence.FetchType.LAZY;
import static org.assertj.core.api.Assertions.assertThat;

public class CategoryTest {

    private static <T> T withId(T entity, Long id) {
        ReflectionTestUtils.setField(entity, "id", id);
        return entity;
    }

    private static Category newCategory(String name) {
        return new Category(name);
    }

    @Test
    @DisplayName("equals/hashCode: consideram apenas o id (padrão HibernateProxy-aware)")
    void equalsAndHashCode_onlyById() {
        Category category1 = withId(newCategory("Eletrônicos"), 10L);
        Category category2 = withId(newCategory("Informática"), 10L);

        assertThat(category1).isEqualTo(category2);
        assertThat(category1.hashCode()).isEqualTo(category2.hashCode());

        Category category = withId(newCategory("Games"), 11L);
        assertThat(category1).isNotEqualTo(category);

        Category newCategory = newCategory("Acessórios");
        assertThat(newCategory).isNotEqualTo(category1);
        assertThat(category1).isNotEqualTo(newCategory);
    }

    @Test
    @DisplayName("products: coleção inicializada, não nula e vazia; mutável via get()")
    void products_isInitializedAndMutable() {
        Category category = newCategory("Livros");

        Set<Product> products = category.getProducts();
        assertThat(products).isNotNull().isEmpty();

        Product product = new Product();
        product.setName("Livro de Java");
        products.add(product);

        assertThat(category.getProducts()).hasSize(1)
                .first()
                .extracting(Product::getName)
                .isEqualTo("Livro de Java");
    }

    @Test
    @DisplayName("@ManyToMany(mappedBy=\"categories\"): presente e com fetch LAZY por default")
    void mapping_annotations_present_and_lazyByDefault() throws Exception {
        var field = Category.class.getDeclaredField("products");
        var manyToMany = field.getAnnotation(ManyToMany.class);

        assertThat(manyToMany).as("@ManyToMany deve estar presente").isNotNull();
        assertThat(manyToMany.mappedBy()).isEqualTo("categories");
        assertThat(manyToMany.fetch()).isEqualTo(LAZY);
    }
}
