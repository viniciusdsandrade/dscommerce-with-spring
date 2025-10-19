package com.resftul.dscommerce.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.NONE;

@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity(name = "Product")
@Table(name = "tb_product")
public class Product {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "product_name")
    private String name;

    @Column(name = "product_description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "product_price")
    private Double price;

    @Column(name = "product_img_url")
    private String imgUrl;

    @ManyToMany
    @JoinTable(name = "tb_product_category",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<Category> categories = new HashSet<>();

    @OneToMany(mappedBy = "id.product")
    @Setter(NONE)
    private Set<OrderItem> items = new HashSet<>();

    public Product(
            Long id,
            String name,
            String description,
            Double price,
            String imageUrl
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imgUrl = imageUrl;
    }

    public List<Order> getOrders() {
        return items.stream().map(OrderItem::getOrder).toList();
    }

    @Override
    public String toString() {
        return "{\n" +
                "  \"id\": " + this.id +
                ",\n  \"name\": \"" + this.name + '\"' +
                ",\n  \"description\": \"" + this.description + '\"' +
                ",\n  \"price\": " + this.price +
                ",\n  \"imgUrl\": \"" + this.imgUrl + '\"' +
                "\n}";
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
}