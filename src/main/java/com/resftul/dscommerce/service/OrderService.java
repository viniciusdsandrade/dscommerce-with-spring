package com.resftul.dscommerce.service;

import com.resftul.dscommerce.dto.order.OrderDTO;
import org.springframework.transaction.annotation.Transactional;

public interface OrderService {
    @Transactional(readOnly = true)
    OrderDTO findById(Long id);

    @Transactional
    OrderDTO insert(OrderDTO dto);
}
