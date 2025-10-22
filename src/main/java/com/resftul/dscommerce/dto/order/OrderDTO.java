package com.resftul.dscommerce.dto.order;

import com.resftul.dscommerce.dto.ClientDTO;
import com.resftul.dscommerce.dto.PaymentDTO;
import com.resftul.dscommerce.entity.Order;
import com.resftul.dscommerce.entity.OrderItem;
import com.resftul.dscommerce.entity.OrderStatus;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.NONE;

@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class OrderDTO {

    private Long id;
    private Instant moment;
    private OrderStatus status;
    private ClientDTO client;
    private PaymentDTO payment;

    @NotEmpty(message = "Deve ter pelo menos um item")
    @Setter(NONE)
    private List<OrderItemDTO> items = new ArrayList<>();

    public OrderDTO(Order order) {
        this.id = order.getId();
        this.moment = order.getMoment();
        this.status = order.getStatus();
        this.client = new ClientDTO(order.getClient());
        this.payment = (order.getPayment() == null) ? null : new PaymentDTO(order.getPayment());
        for (OrderItem item : order.getItems()) {
            OrderItemDTO itemDto = new OrderItemDTO(item);
            items.add(itemDto);
        }
    }

    public Double getTotal() {
        return items
                .stream()
                .mapToDouble(OrderItemDTO::getSubTotal)
                .sum();
    }
}
