package com.resftul.dscommerce.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

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

        return Objects.equals(order, that.order) &&
                Objects.equals(product, that.product);
    }

    @Override
    public int hashCode() {

        int hash = 7;
        final int prime = 31;

        hash *= prime + (order == null ? 0 : order.hashCode());
        hash *= prime + (product == null ? 0 : product.hashCode());

        if (hash < 0)
            hash *= -1;

        return hash;
    }

    @Override
    public String toString() {
        return "{\n" +
                "  \"order\": " + (order != null ? order.getId() : null) +
                ",\n  \"product\": " + (product != null ? product.getId() : null) +
                "\n}";
    }

    public OrderItemPK(OrderItemPK orderItemPK) {
        this.order = new Order(orderItemPK.getOrder());
        this.product = new Product(orderItemPK.getProduct());
    }
}