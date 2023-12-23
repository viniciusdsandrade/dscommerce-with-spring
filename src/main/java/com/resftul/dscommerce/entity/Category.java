package com.resftul.dscommerce.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Category")
@Table(name = "tb_category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "category_name", nullable = false)
    private String name;

    @ManyToMany(mappedBy = "categories")
    @Setter(AccessLevel.NONE)
    private Set<Product> products = new HashSet<>();

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;

        Category that = (Category) obj;

        return Objects.equals(this.id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public String toString() {
        return "{\n" +
                "  \"id\": " + this.id +
                ",\n  \"name\": \"" + this.name + '\"' +
                "\n}";
    }
    
    public Category(Category category) {
        this.id = category.id;
        this.name = category.name;

        // Crie um novo conjunto para garantir uma cópia profunda
        this.products = new HashSet<>(category.products.size());
        for (Product product : category.products) {
            this.products.add(new Product(product));
        }
    }
}