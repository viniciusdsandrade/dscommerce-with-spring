package com.resftul.dscommerce.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

@NoArgsConstructor
@Getter
@Setter
@Entity(name = "OrderItem")
@Table(name = "tb_order_item")
public class OrderItem {

    @EmbeddedId
    private OrderItemPK id = new OrderItemPK();

    private Integer quantity;
    private Double price;

    public Order getOrder() {
        return id.getOrder();
    }
    public Product getProduct() {
        return id.getProduct();
    }

    public OrderItem(
            Order order,
            Product product,
            Integer quantity,
            Double price
    ) {
        id.setOrder(order);
        id.setProduct(product);
        this.quantity = quantity;
        this.price = price;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        OrderItem orderItem = (OrderItem) o;
        return getId() != null && Objects.equals(getId(), orderItem.getId());
    }

    public OrderItem(OrderItem orderItem) {
        this.id = new OrderItemPK();
        this.quantity = orderItem.quantity;
        this.price = orderItem.price;
    }
}