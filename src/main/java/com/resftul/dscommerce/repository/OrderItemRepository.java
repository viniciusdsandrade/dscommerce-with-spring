package com.resftul.dscommerce.repository;


import com.resftul.dscommerce.entity.OrderItem;
import com.resftul.dscommerce.entity.OrderItemPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("orderItemRepository")
public interface OrderItemRepository extends JpaRepository<OrderItem, OrderItemPK> {
}
