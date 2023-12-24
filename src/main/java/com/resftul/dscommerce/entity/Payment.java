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

        Payment that = (Payment) o;

        return Objects.equals(this.id, that.id) &&
                Objects.equals(this.moment, that.moment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.moment);
    }

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