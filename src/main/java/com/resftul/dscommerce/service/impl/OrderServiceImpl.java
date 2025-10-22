package com.resftul.dscommerce.service.impl;

import com.resftul.dscommerce.dto.order.OrderDTO;
import com.resftul.dscommerce.dto.order.OrderItemDTO;
import com.resftul.dscommerce.entity.*;
import com.resftul.dscommerce.exception.ResourceNotFoundException;
import com.resftul.dscommerce.repository.OrderItemRepository;
import com.resftul.dscommerce.repository.OrderRepository;
import com.resftul.dscommerce.repository.ProductRepository;
import com.resftul.dscommerce.service.AuthService;
import com.resftul.dscommerce.service.OrderService;
import com.resftul.dscommerce.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.resftul.dscommerce.entity.OrderStatus.WAITING_PAYMENT;
import static java.time.Instant.now;

@Service("orderService")
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserService userService;
    private final AuthService authService;

    public OrderServiceImpl(
            OrderRepository orderRepository,
            ProductRepository productRepository,
            OrderItemRepository orderItemRepository,
            UserService userService,
            AuthService authService
    ) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.orderItemRepository = orderItemRepository;
        this.userService = userService;
        this.authService = authService;
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDTO findById(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Recurso não encontrado"));
        authService.validateSelfOrAdmin(order.getClient().getId());
        return new OrderDTO(order);
    }

    @Override
    @Transactional
    public OrderDTO insert(OrderDTO orderDTO) {

        Order order = new Order();

        order.setMoment(now());
        order.setStatus(WAITING_PAYMENT);

        User user = userService.authenticated();
        order.setClient(user);

        for (OrderItemDTO itemDto : orderDTO.getItems()) {
            Product product = productRepository.getReferenceById(itemDto.getProductId());
            OrderItem item = new OrderItem(order, product, itemDto.getQuantity(), product.getPrice());
            order.getItems().add(item);
        }

        orderRepository.save(order);
        orderItemRepository.saveAll(order.getItems());

        return new OrderDTO(order);
    }
}
