package com.resftul.dscommerce;

import com.resftul.dscommerce.entity.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class DscommerceApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(DscommerceApplication.class, args);
    }

    @Override
    public void run(String... args) {
        
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        user.setPhone("1234567890");
        user.setBirthDate(LocalDate.of(2001, 12, 6));
        user.setPassword("password123");

        // Exemplo de Product
        Product product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setDescription("Powerful laptop with high performance");
        product.setPrice(999.99);
        product.setImgUrl("laptop.jpg");

        // Exemplo de Category
        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");
        
        Order order = new Order();
        order.setId(1L);
        order.setMoment(Instant.now());
        order.setClient(user);
        
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(2);
        orderItem.setPrice(1999.98);
        
        Set<OrderItem> orderItems = new HashSet<>();
        orderItems.add(orderItem);
        order.setItems(orderItems);
        
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setMoment(Instant.now());
        payment.setOrder(order);
        
        
        System.out.println("User: " + user);
        System.out.println("Product: " + product);
        System.out.println("Category: " + category);
        System.out.println("Order: " + order);
        System.out.println("OrderItem: " + orderItem);
        System.out.println("Payment: " + payment);
    }
}