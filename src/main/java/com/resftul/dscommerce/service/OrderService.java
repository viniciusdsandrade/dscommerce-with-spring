package com.resftul.dscommerce.service;

import com.resftul.dscommerce.dto.order.OrderDTO;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OrderService {
    @Transactional(readOnly = true)
    OrderDTO findById(Long id);

    @Transactional
    OrderDTO insert(@Valid OrderDTO dto);

    @Transactional(readOnly = true)
    List<OrderDTO> listAll();
}
