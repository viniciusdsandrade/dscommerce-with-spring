package com.resftul.dscommerce.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.NONE;

@NoArgsConstructor
@Getter
@Setter
@Entity(name = "Product")
@Table(
        name = "tb_product",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_tb_product_name",
                columnNames = "name"
        )
)
public class Product {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private String name;
    private BigDecimal price;
    @Column(columnDefinition = "TEXT")
    private String description;
    private String imgUrl;

    @ManyToMany
    @JoinTable(
            name = "tb_product_category",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @Setter(NONE)
    private Set<Category> categories = new HashSet<>();

    @OneToMany(mappedBy = "id.product")
    @Setter(NONE)
    private Set<OrderItem> items = new HashSet<>();

    public Product(
            Long id,
            String name,
            String description,
            BigDecimal price,
            String imageUrl
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imgUrl = imageUrl;
    }

    public Product(
            String name,
            String description,
            BigDecimal price,
            String imageUrl,
            Set<Category> categories
    ) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.imgUrl = imageUrl;
        if (categories != null && !categories.isEmpty()) {
            this.categories.addAll(categories);
        }
    }

    public void replaceAttributes(
            String name,
            String description,
            BigDecimal price,
            String imageUrl,
            Set<Category> categories
    ) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.imgUrl = imageUrl;
        this.categories.clear();
        if (categories != null && !categories.isEmpty()) {
            this.categories.addAll(categories);
        }
    }

    public List<Order> getOrders() {
        return items.stream().map(OrderItem::getOrder).toList();
    }

    public Product(Product product) {
        this.id = product.id;
        this.name = product.name;
        this.description = product.description;
        this.price = product.price;
        this.imgUrl = product.imgUrl;
        this.categories = new HashSet<>(product.categories);
        this.items = new HashSet<>(product.items);
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

        Product product = (Product) obj;

        return getId() != null && Objects.equals(getId(), product.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy
                ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
                : getClass().hashCode();
    }
}