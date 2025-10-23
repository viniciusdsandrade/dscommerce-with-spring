package com.resftul.dscommerce.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@NoArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
@Embeddable
public class OrderItemPK implements Serializable {

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    public OrderItemPK(OrderItemPK orderItemPK) {
        this.order = new Order(orderItemPK.getOrder());
        this.product = new Product(orderItemPK.getProduct());
    }
}