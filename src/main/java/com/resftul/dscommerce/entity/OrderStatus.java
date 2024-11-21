package com.resftul.dscommerce.entity;

import jakarta.persistence.Table;

@Table(name = "tb_order_status")
public enum OrderStatus {
    WAITING_PAYMENT,
    PAID,
    SHIPPED,
    DELIVERED,
}