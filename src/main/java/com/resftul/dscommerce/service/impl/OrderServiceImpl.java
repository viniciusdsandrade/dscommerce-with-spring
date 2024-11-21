package com.resftul.dscommerce.service.impl;

import com.resftul.dscommerce.dto.OrderDTO;
import com.resftul.dscommerce.dto.OrderItemDTO;
import com.resftul.dscommerce.entity.*;
import com.resftul.dscommerce.exception.ResourceNotFoundException;
import com.resftul.dscommerce.repository.OrderItemRepository;
import com.resftul.dscommerce.repository.OrderRepository;
import com.resftul.dscommerce.repository.ProductRepository;
import com.resftul.dscommerce.service.AuthService;
import com.resftul.dscommerce.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.resftul.dscommerce.entity.OrderStatus.WAITING_PAYMENT;
import static java.time.Instant.now;

@Service("orderService")
public class OrderServiceImpl implements OrderService {

    private final OrderRepository repository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserService userService;
    private final AuthService authService;

    public OrderServiceImpl(OrderRepository repository,
                            ProductRepository productRepository,
                            OrderItemRepository orderItemRepository,
                            UserService userService, AuthService authService) {
        this.repository = repository;
        this.productRepository = productRepository;
        this.orderItemRepository = orderItemRepository;
        this.userService = userService;
        this.authService = authService;
    }

    @Override
    public OrderDTO findById(Long id) {
        Order order = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Recurso não encontrado"));
        authService.validateSelfOrAdmin(order.getClient().getId());
        return new OrderDTO(order);
    }

    @Override
    @Transactional
    public OrderDTO insert(OrderDTO dto) {

        Order order = new Order();

        order.setMoment(now());
        order.setStatus(WAITING_PAYMENT);

        User user = userService.authenticated();
        order.setClient(user);

        for (OrderItemDTO itemDto : dto.getItems()) {
            Product product = productRepository.getReferenceById(itemDto.getProductId());
            OrderItem item = new OrderItem(order, product, itemDto.getQuantity(), product.getPrice());
            order.getItems().add(item);
        }

        repository.save(order);
        orderItemRepository.saveAll(order.getItems());

        return new OrderDTO(order);
    }
}
