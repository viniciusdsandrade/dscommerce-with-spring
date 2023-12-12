package com.resftul.dscommerce.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Product")
@Table(name = "tb_product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_name", nullable = false)
    private String name;

    @Column(name = "product_description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "product_price", nullable = false)
    private Double price;

    @Column(name = "product_img_url", nullable = false)
    private String imgUrl;

    @ManyToMany
    @JoinTable(name = "tb_product_category",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    @Setter(AccessLevel.NONE)
    private Set<Category> categories = new HashSet<>();

    @OneToMany(mappedBy = "id.product")
    private Set<OrderItem> items = new HashSet<>();

    public List<Order> getOrders() {
        return items.stream().map(OrderItem::getOrder).toList();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (this.getClass() != o.getClass()) return false;

        Product product = (Product) o;

        return Objects.equals(id, product.id) &&
                Objects.equals(name, product.name) &&
                Objects.equals(price, product.price) &&
                Objects.equals(imgUrl, product.imgUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, price, imgUrl);
    }

    @Override
    public String toString() {
        return "{\n" +
                "  \"id\": " + id +
                ",\n  \"name\": \"" + name + '\"' +
                ",\n  \"description\": \"" + description + '\"' +
                ",\n  \"price\": " + price +
                ",\n  \"imgUrl\": \"" + imgUrl + '\"' +
                "\n}";
    }

    public Product(Product product) {
        this.id = product.id;
        this.name = product.name;
        this.description = product.description;
        this.price = product.price;
        this.imgUrl = product.imgUrl;

        // Copie o conteúdo dos conjuntos para evitar compartilhamento de estado.
        this.categories = new HashSet<>(product.categories);
        this.items = new HashSet<>(product.items);
    }

}