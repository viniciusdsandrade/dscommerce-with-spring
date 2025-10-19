package com.resftul.dscommerce.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.Objects;

import static jakarta.persistence.GenerationType.IDENTITY;

@EqualsAndHashCode(callSuper = false)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Payment")
@Table(name = "tb_payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "payment_moment",
            nullable = false,
            columnDefinition = "TIMESTAMP")
    private Instant moment;

    @OneToOne
    @MapsId
    private Order order;

    @Override
    public String toString() {
        return "{\n" +
                "  \"id\": " + this.id +
                ",\n  \"moment\": \"" + this.moment + "\"" +
                ",\n  \"orderId\": " + (this.order != null ? this.order.getId() : null) +
                "\n}";
    }

    public Payment(Payment payment) {
        this.id = payment.id;
        this.moment = payment.moment;
        this.order = payment.order;
    }
}