package com.resftul.dscommerce.dto;

import com.resftul.dscommerce.entity.Payment;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@EqualsAndHashCode
@NoArgsConstructor
@Getter
public class PaymentDTO {

    private Long id;
    private Instant moment;

    public PaymentDTO(Payment payment) {
        id = payment.getId();
        moment = payment.getMoment();
    }
}
