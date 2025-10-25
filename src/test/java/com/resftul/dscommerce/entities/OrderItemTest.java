package com.resftul.dscommerce.entities;

import com.resftul.dscommerce.entity.Order;
import com.resftul.dscommerce.entity.OrderItem;
import com.resftul.dscommerce.entity.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.persistence.*;

import java.lang.reflect.Field;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderItemTest {

    @Test
    @DisplayName("@Entity(name) e @Table(name)")
    void entity_and_table_annotations() {
        Entity entity = OrderItem.class.getAnnotation(Entity.class);
        Table table = OrderItem.class.getAnnotation(Table.class);

        assertThat(entity).isNotNull();
        assertThat(entity.name()).isEqualTo("OrderItem");

        assertThat(table).isNotNull();
        assertThat(table.name()).isEqualTo("tb_order_item");
    }

    @Test
    @DisplayName("@EmbeddedId presente no campo id")
    void embeddedId_present_on_id() throws Exception {
        Field id = OrderItem.class.getDeclaredField("id");
        assertThat(id.getAnnotation(EmbeddedId.class)).isNotNull();
    }

    @Test
    @DisplayName("getOrder()/getProduct() delegam para o id")
    void getters_delegate_to_id() {
        Order order = new Order();
        Product product = new Product();

        OrderItem orderItem = new OrderItem(
                order,
                product,
                3,
                new BigDecimal("99.90")
        );

        assertThat(orderItem.getOrder()).isSameAs(order);
        assertThat(orderItem.getProduct()).isSameAs(product);
        assertThat(orderItem.getQuantity()).isEqualTo(3);
        assertThat(orderItem.getPrice()).isEqualByComparingTo("99.90");
    }

    @Test
    @DisplayName("equals/hashCode baseados no id embutido")
    void equals_and_hashCode_by_embeddedId() {
        Order order = new Order();
        order.setId(1L);

        Product product = new Product();
        product.setId(2L);

        OrderItem orderItem1 = new OrderItem(order, product, 1, null);
        OrderItem orderItem2 = new OrderItem();
        orderItem2.setId(orderItem1.getId());

        assertThat(orderItem1).isEqualTo(orderItem2).hasSameHashCodeAs(orderItem2);

        OrderItem oi3 = new OrderItem(order, new Product(), 1, null);
        assertThat(orderItem1).isNotEqualTo(oi3);
    }
}
