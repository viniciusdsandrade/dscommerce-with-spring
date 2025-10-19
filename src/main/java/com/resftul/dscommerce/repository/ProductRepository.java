package com.resftul.dscommerce.repository;

import com.resftul.dscommerce.entity.Product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository("productRepository")
public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsByNameAndDescription(String name, String description);

    @Query("SELECT p " +
           "FROM Product p " +
           "WHERE UPPER(p.name) " +
           "LIKE UPPER(CONCAT('%', :name, '%'))")
    Page<Product> searchByName(String name, Pageable pageable);
}
