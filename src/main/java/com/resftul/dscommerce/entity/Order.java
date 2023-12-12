package com.resftul.dscommerce.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Order")
@Table(name = "tb_order")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_moment",
            nullable = false,
            columnDefinition = "TIMESTAMP")
    private Instant moment;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private User client;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Payment payment;

    @Getter
    @OneToMany(mappedBy = "id.order")
    private Set<OrderItem> items = new HashSet<>();

    public List<Product> getProducts() {
        return items.stream().map(OrderItem::getProduct).toList();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (this.getClass() != o.getClass()) return false;

        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "{\n" +
                "  \"id\": " + id +
                ",\n  \"moment\": \"" + moment + "\"" +
                ",\n  \"client\": " + (client != null ? client.getId() : null) +
                ",\n  \"payment\": " + (payment != null ? payment.getId() : null) +
                ",\n  \"items\": " + items +
                "\n}";
    }
    
    public Order(Order order) {
        this.id = order.id;
        this.moment = order.moment;
        this.client = order.client;
        this.payment = order.payment;
        this.items = order.items;
    }
}