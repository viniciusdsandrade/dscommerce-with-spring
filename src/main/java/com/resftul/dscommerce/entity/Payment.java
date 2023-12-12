package com.resftul.dscommerce.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Payment")
@Table(name = "tb_payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payment_moment",
            nullable = false,
            columnDefinition = "TIMESTAMP")
    private Instant moment;

    @OneToOne
    @MapsId
    private Order order;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (this.getClass() != o.getClass()) return false;

        Payment payment = (Payment) o;

        return Objects.equals(id, payment.id) &&
                Objects.equals(moment, payment.moment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, moment);
    }

    @Override
    public String toString() {
        return "{\n" +
                "  \"id\": " + id +
                ",\n  \"moment\": \"" + moment + "\"" +
                ",\n  \"orderId\": " + (order != null ? order.getId() : null) +
                "\n}";
    }

    public Payment(Payment payment) {
        this.id = payment.id;
        this.moment = payment.moment;
        this.order = payment.order;
    }

}
