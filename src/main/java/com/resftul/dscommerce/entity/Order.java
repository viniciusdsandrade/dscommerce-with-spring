package com.resftul.dscommerce.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Order")
@Table(name = "tb_order")
public class Order {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "order_moment",
            nullable = false,
            columnDefinition = "TIMESTAMP")
    private Instant moment;

    @Enumerated(STRING)
    private OrderStatus status;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Users client;

    @OneToOne(mappedBy = "order", cascade = ALL)
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

        Order that = (Order) o;

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
               ",\n  \"moment\": \"" + this.moment + "\"" +
               ",\n  \"client\": " + (this.client != null ? this.client.getId() : null) +
               ",\n  \"payment\": " + (this.payment != null ? this.payment.getId() : null) +
               ",\n  \"items\": " + this.items +
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