package com.resftul.dscommerce.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@EqualsAndHashCode
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
    public void setProduct(Product product) {
        id.setProduct(product);
    }
    public void setOrder(Order order) {
        id.setOrder(order);
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
    public String toString() {
        return "{\n" +
                "  \"id\": " + this.id +
                ",\n  \"quantity\": " + this.quantity +
                ",\n  \"price\": " + this.price +
                "\n}";
    }

    public OrderItem(OrderItem orderItem) {
        this.id = new OrderItemPK();
        this.quantity = orderItem.quantity;
        this.price = orderItem.price;
    }
}