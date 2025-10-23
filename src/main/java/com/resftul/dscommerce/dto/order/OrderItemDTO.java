package com.resftul.dscommerce.dto.order;

import com.resftul.dscommerce.entity.OrderItem;
import lombok.*;

import java.math.BigDecimal;

import static java.math.BigDecimal.ZERO;

@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderItemDTO {

    private Long productId;
    private String name;
    private BigDecimal price;
    private Integer quantity;
    private String imgUrl;

    public OrderItemDTO(OrderItem orderItem) {
        productId = orderItem.getProduct().getId();
        name = orderItem.getProduct().getName();
        price = orderItem.getPrice();
        quantity = orderItem.getQuantity();
        imgUrl = orderItem.getProduct().getImgUrl();
    }

    public double getSubTotal() {
        final double p = (price != null ? price : ZERO).doubleValue();
        final int q = (quantity != null ? quantity : 0);
        return p * q;
    }
}
