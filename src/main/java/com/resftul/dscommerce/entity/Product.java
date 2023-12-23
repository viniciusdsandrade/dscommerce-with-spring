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
    @Setter(AccessLevel.NONE)
    private Set<Category> categories = new HashSet<>();

    @OneToMany(mappedBy = "id.product")
    private Set<OrderItem> items = new HashSet<>();

    public Product(Long id,
                   String name,
                   String description,
                   Double price,
                   String imageUrl) {
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
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;
        if (this.getClass() != o.getClass()) return false;

        Product that = (Product) o;

        return Objects.equals(this.id, that.id) &&
                Objects.equals(this.name, that.name) &&
                Objects.equals(this.price, that.price) &&
                Objects.equals(this.imgUrl, that.imgUrl);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int hash = 1;

        hash *= prime + ((this.id == null) ? 0 : this.id.hashCode());
        hash *= prime + ((this.imgUrl == null) ? 0 : this.imgUrl.hashCode());
        hash *= prime + ((this.name == null) ? 0 : this.name.hashCode());
        hash *= prime + ((this.price == null) ? 0 : this.price.hashCode());

        if (hash < 0)
            hash *= -1;

        return hash;
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

    // Construtor de cópia.
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