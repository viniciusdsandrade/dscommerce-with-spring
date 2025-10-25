package com.resftul.dscommerce.entities;

import com.resftul.dscommerce.entity.Order;
import com.resftul.dscommerce.entity.Payment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

public class PaymentTest {

    @Test
    @DisplayName("getters/setters: id, moment e order")
    void getters_setters_roundtrip() {
        Payment payment1 = new Payment();
        payment1.setId(10L);
        Instant now = Instant.parse("2024-01-01T10:00:00Z");
        payment1.setMoment(now);

        Order order = new Order();
        order.setId(10L);
        payment1.setOrder(order);

        assertThat(payment1.getId()).isEqualTo(10L);
        assertThat(payment1.getMoment()).isEqualTo(now);
        assertThat(payment1.getOrder()).isSameAs(order);
    }

    @Test
    @DisplayName("equals/hashCode: true quando o id (não-nulo) é o mesmo")
    void equals_hashCode_true_when_same_non_null_id() {
        Payment payment1 = new Payment();
        payment1.setId(1L);

        Payment payment2 = new Payment();
        payment2.setId(1L);

        assertThat(payment1).isEqualTo(payment2).hasSameHashCodeAs(payment2);
    }

    @Test
    @DisplayName("equals: false para ids diferentes e quando algum id é nulo")
    void equals_false_when_different_or_null_id() {
        Payment payment1 = new Payment();
        payment1.setId(1L);

        Payment payment2 = new Payment();
        payment2.setId(2L);

        Payment payment3 = new Payment();

        assertThat(payment1).isNotEqualTo(payment2);
        assertThat(payment1).isNotEqualTo(payment3);
        assertThat(payment3).isNotEqualTo(payment1);
    }

    @Test
    @DisplayName("equals: contrato básico e consistência do hashCode")
    void equals_contract_basics() {
        Payment payment1 = new Payment(); payment1.setId(5L);
        Payment payment2 = new Payment(); payment2.setId(5L);
        Payment payment3 = new Payment(); payment3.setId(5L);

        assertThat(payment1).isEqualTo(payment1);

        assertThat(payment1).isEqualTo(payment2);
        assertThat(payment2).isEqualTo(payment1);
        assertThat(payment2).isEqualTo(payment3);
        assertThat(payment1).isEqualTo(payment3);

        assertThat(payment1.hashCode()).isEqualTo(payment2.hashCode());
        assertThat(payment2.hashCode()).isEqualTo(payment3.hashCode());
    }

    @Test
    @DisplayName("equals: false quando comparado com outro tipo")
    void equals_false_for_different_class() {
        Payment payment1 = new Payment(); payment1.setId(99L);
        Object other = new Object();
        assertThat(payment1).isNotEqualTo(other);
    }
}
