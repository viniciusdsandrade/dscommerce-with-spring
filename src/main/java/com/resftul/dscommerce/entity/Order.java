package com.resftul.dscommerce.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity(name = "Order")
@Table(name = "tb_order")
public class Order {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(
            name = "order_moment",
            nullable = false,
            columnDefinition = "TIMESTAMP"
    )
    private Instant moment;

    @Enumerated(STRING)
    private OrderStatus status;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Users client;

    @OneToOne(mappedBy = "order", cascade = ALL)
    private Payment payment;

    @OneToMany(mappedBy = "id.order")
    private Set<OrderItem> items = new HashSet<>();

    public List<Product> getProducts() {
        return items.stream().map(OrderItem::getProduct).toList();
    }

    public Order(Order order) {
        this.id = order.id;
        this.moment = order.moment;
        this.status = order.status;
        this.client = order.client;
        this.payment = order.payment;
        this.items = order.items;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Order order = (Order) o;
        return getId() != null && Objects.equals(getId(), order.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}