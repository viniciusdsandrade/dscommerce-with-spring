package com.resftul.dscommerce.dto.order;

import com.resftul.dscommerce.entity.OrderItem;
import lombok.*;

@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderItemDTO {
    private Long productId;
    private String name;
    private Double price;
    private Integer quantity;
    private String imgUrl;

    public OrderItemDTO(OrderItem entity) {
        productId = entity.getProduct().getId();
        name = entity.getProduct().getName();
        price = entity.getPrice();
        quantity = entity.getQuantity();
        imgUrl = entity.getProduct().getImgUrl();
    }

    public double getSubTotal() {
        final double p = (price != null ? price : 0.0);
        final int q = (quantity != null ? quantity : 0);
        return p * q;
    }
}
