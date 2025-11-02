package com.resftul.dscommerce.service;

import com.resftul.dscommerce.dto.order.OrderDTO;
import com.resftul.dscommerce.dto.order.OrderItemDTO;
import com.resftul.dscommerce.entity.*;
import com.resftul.dscommerce.exception.ResourceNotFoundException;
import com.resftul.dscommerce.repository.OrderItemRepository;
import com.resftul.dscommerce.repository.OrderRepository;
import com.resftul.dscommerce.repository.ProductRepository;
import com.resftul.dscommerce.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static com.resftul.dscommerce.entity.OrderStatus.WAITING_PAYMENT;
import static java.math.BigDecimal.valueOf;
import static java.time.Instant.parse;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock private OrderRepository orderRepository;
    @Mock private ProductRepository productRepository;
    @Mock private OrderItemRepository orderItemRepository;
    @Mock private UserService userService;
    @Mock private AuthService authService;

    @InjectMocks private OrderServiceImpl orderServiceImpl;

    private static User user(Long id, String name, String email, Role... roles) {
        return new User(
                id,
                name,
                email,
                roles
        );
    }

    private static Role role(String auth) {
        return new Role((long) auth.hashCode(), auth);
    }

    private static Product product(Long id, String name, BigDecimal price) {
        return new Product(
                id,
                name,
                price
        );
    }

    private static Order order(Long id, User client, OrderStatus orderStatus, Instant moment) {
        return new Order(
                id,
                moment,
                orderStatus,
                client
        );
    }

    private static OrderItemDTO itemDto(long productId, int quantity) {
        OrderItemDTO orderItemDTO = mock(OrderItemDTO.class);
        when(orderItemDTO.getProductId()).thenReturn(productId);
        when(orderItemDTO.getQuantity()).thenReturn(quantity);
        return orderItemDTO;
    }

    @Test
    @DisplayName("findById: retorna DTO e valida autorização via AuthService")
    void findById_ok() {
        var client = user(10L, "Ana", "ana@example.com", role("ROLE_CLIENT"));
        var persisted = order(100L, client, OrderStatus.PAID, parse("2024-01-01T10:00:00Z"));
        when(orderRepository.findById(100L)).thenReturn(Optional.of(persisted));
        doNothing().when(authService).validateSelfOrAdmin(10L);

        OrderDTO out = orderServiceImpl.findById(100L);

        assertThat(out.getId()).isEqualTo(100L);
        assertThat(out.getStatus()).isEqualTo(OrderStatus.PAID);
        verify(authService).validateSelfOrAdmin(10L);
    }

    @Test
    @DisplayName("findById: lança ResourceNotFoundException quando id não existe")
    void findById_notFound() {
        when(orderRepository.findById(404L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> orderServiceImpl.findById(404L));
        verify(authService, never()).validateSelfOrAdmin(anyLong());
    }

    @Test
    @DisplayName("insert: cria pedido com usuário autenticado, status WAITING_PAYMENT e itens precificados")
    void insert_ok_buildsOrderAndItems() {
        var currentUser = user(20L, "Bob", "bob@example.com", role("ROLE_CLIENT"));
        when(userService.authenticated()).thenReturn(currentUser);

        var product1 = product(1L, "Notebook", valueOf(100.00));
        var product2 = product(2L, "Mouse", valueOf(50.00));
        when(productRepository.getReferenceById(1L)).thenReturn(product1);
        when(productRepository.getReferenceById(2L)).thenReturn(product2);

        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> {
            Order o = inv.getArgument(0);
            o.setId(999L);
            return o;
        });
        when(orderItemRepository.saveAll(any())).thenReturn(emptyList());

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.getItems().add(itemDto(1L, 2));
        orderDTO.getItems().add(itemDto(2L, 3));

        OrderDTO out = orderServiceImpl.insert(orderDTO);

        assertThat(out.getId()).isEqualTo(999L);
        assertThat(out.getStatus()).isEqualTo(WAITING_PAYMENT);
        assertThat(out.getTotal()).isEqualTo(2 * 100.0 + 3 * 50.0);

        verify(userService).authenticated();
        verify(productRepository).getReferenceById(1L);
        verify(productRepository).getReferenceById(2L);
        verify(orderRepository).save(any(Order.class));
        verify(orderItemRepository).saveAll(any());
    }

    @Test
    @DisplayName("listAll: quando CLIENT, retorna apenas pedidos do próprio usuário")
    void listAll_nonAdmin_onlyOwnOrders() {
        var client = user(30L, "Carol", "carol@example.com", role("ROLE_CLIENT"));
        var order1 = order(1001L, client, OrderStatus.WAITING_PAYMENT, parse("2024-02-01T00:00:00Z"));
        var order2 = order(1002L, client, OrderStatus.DELIVERED, parse("2024-02-05T00:00:00Z"));
        client.getOrders().add(order1);
        client.getOrders().add(order2);
        when(userService.authenticated()).thenReturn(client);

        List<OrderDTO> out = orderServiceImpl.listAll();

        assertThat(out).hasSize(2);
        assertThat(out).extracting(OrderDTO::getId).containsExactlyInAnyOrder(1001L, 1002L);
        verify(orderRepository, never()).findAll();
    }

    @Test
    @DisplayName("listAll: quando ADMIN, retorna todos os pedidos do repositório")
    void listAll_admin_returnsAll() {
        var admin = user(40L, "Dave", "dave@example.com", role("ROLE_ADMIN"));
        when(userService.authenticated()).thenReturn(admin);

        var orderA = order(2001L, admin, OrderStatus.PAID, parse("2024-03-10T00:00:00Z"));
        var orderB = order(2002L, admin, OrderStatus.SHIPPED, parse("2024-03-12T00:00:00Z"));
        when(orderRepository.findAll()).thenReturn(List.of(orderA, orderB));

        List<OrderDTO> out = orderServiceImpl.listAll();

        assertThat(out).hasSize(2);
        assertThat(out).extracting(OrderDTO::getId).containsExactlyInAnyOrder(2001L, 2002L);
        verify(orderRepository).findAll();
    }
}
