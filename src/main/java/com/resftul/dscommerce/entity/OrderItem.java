package com.resftul.dscommerce.entity;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.Objects;

@NoArgsConstructor
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

    public Integer getQuantity() {
        return quantity;
    }
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    public Double getPrice() {
        return price;
    }
    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (this.getClass() != o.getClass()) return false;

        OrderItem that = (OrderItem) o;

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