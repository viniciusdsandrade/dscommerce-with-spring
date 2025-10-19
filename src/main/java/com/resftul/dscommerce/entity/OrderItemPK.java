package com.resftul.dscommerce.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

// Crie um novo conjunto para garantir uma cópia profunda
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class OrderItemPK implements Serializable {

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (o.getClass() != this.getClass()) return false;

        OrderItemPK that = (OrderItemPK) o;

        return Objects.equals(this.order, that.order) &&
                Objects.equals(this.product, that.product);
    }

    @Override
    public int hashCode() {

        int hash = 7;
        final int prime = 31;

        hash *= prime + (this.order == null ? 0 : this.order.hashCode());
        hash *= prime + (this.product == null ? 0 : this.product.hashCode());

        if (hash < 0)
            hash *= -1;

        return hash;
    }

    @Override
    public String toString() {
        return "{\n" +
                "  \"order\": " + (this.order != null ? order.getId() : null) +
                ",\n  \"product\": " + (this.product != null ? product.getId() : null) +
                "\n}";
    }

    public OrderItemPK(OrderItemPK orderItemPK) {
        this.order = new Order(orderItemPK.getOrder());
        this.product = new Product(orderItemPK.getProduct());
    }
}