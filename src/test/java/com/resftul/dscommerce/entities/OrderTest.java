package com.resftul.dscommerce.entities;

import com.resftul.dscommerce.entity.Order;
import com.resftul.dscommerce.entity.OrderItem;
import com.resftul.dscommerce.entity.Product;
import jakarta.persistence.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.List;
import java.util.Set;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.FetchType.LAZY;
import static org.assertj.core.api.Assertions.assertThat;

public class OrderTest {

    @Test
    @DisplayName("@Entity(name) e @Table(name)")
    void entity_and_table_annotations() {
        Entity entity = Order.class.getAnnotation(Entity.class);
        Table table = Order.class.getAnnotation(Table.class);

        assertThat(entity).isNotNull();
        assertThat(entity.name()).isEqualTo("Order");

        assertThat(table).isNotNull();
        assertThat(table.name()).isEqualTo("tb_order");
    }

    @Test
    @DisplayName("@ManyToOne client: fetch EAGER (padrão) e @JoinColumn(client_id)")
    void client_manyToOne_default_eager_and_joinColumn() throws Exception {
        Field client = Order.class.getDeclaredField("client");
        ManyToOne m = client.getAnnotation(ManyToOne.class);
        JoinColumn jc = client.getAnnotation(JoinColumn.class);

        assertThat(m).isNotNull();
        assertThat(m.fetch()).isEqualTo(EAGER);

        assertThat(jc).isNotNull();
        assertThat(jc.name()).isEqualTo("client_id");
    }

    @Test
    @DisplayName("@OneToOne payment: mappedBy=\"order\", cascade=ALL e fetch EAGER (padrão)")
    void payment_oneToOne_mappedBy_and_cascade_all_default_eager() throws Exception {
        Field payment = Order.class.getDeclaredField("payment");
        OneToOne oo = payment.getAnnotation(OneToOne.class);

        assertThat(oo).isNotNull();
        assertThat(oo.mappedBy()).isEqualTo("order");
        assertThat(Set.of(oo.cascade())).containsExactlyInAnyOrder(ALL);
        assertThat(oo.fetch()).isEqualTo(EAGER);
    }

    @Test
    @DisplayName("@OneToMany items: mappedBy=\"id.order\" e fetch LAZY (padrão)")
    void items_oneToMany_mappedBy_id_order_default_lazy() throws Exception {
        Field items = Order.class.getDeclaredField("items");
        OneToMany otm = items.getAnnotation(OneToMany.class);

        assertThat(otm).isNotNull();
        assertThat(otm.mappedBy()).isEqualTo("id.order");
        assertThat(otm.fetch()).isEqualTo(LAZY);
    }

    @Test
    @DisplayName("Construtor padrão inicializa items como Set vazio")
    void default_constructor_initializes_empty_items() {
        Order order = new Order();
        assertThat(order.getItems()).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("getProducts() mapeia itens -> produtos")
    void getProducts_maps_items_to_products() {
        Order order = new Order();
        order.setMoment(Instant.now());

        Product product1 = new Product();
        product1.setName("P1");
        Product product2 = new Product();
        product2.setName("P2");

        OrderItem i1 = new OrderItem(order, product1, 2, null);
        OrderItem i2 = new OrderItem(order, product2, 1, null);

        order.addItem(i1);
        order.addItem(i2);

        List<Product> out = order.getProducts();

        assertThat(out).extracting(Product::getName)
                .containsExactlyInAnyOrder("P1", "P2");
    }

    @Test
    @DisplayName("equals/hashCode baseados em id")
    void equals_and_hashCode_by_id() {
        Order a = new Order();
        a.setId(10L);

        Order b = new Order();
        b.setId(10L);

        Order c = new Order();
        c.setId(99L);

        assertThat(a).isEqualTo(b).hasSameHashCodeAs(b);
        assertThat(a).isNotEqualTo(c);
    }

    @Test
    @DisplayName("Campos simples possuem tipos esperados")
    void simple_fields_types() throws Exception {
        Field moment = Order.class.getDeclaredField("moment");
        Field status = Order.class.getDeclaredField("status");

        assertThat(moment.getType()).isEqualTo(Instant.class);
        assertThat(status.getType().getSimpleName()).isEqualTo("OrderStatus");
    }
}
