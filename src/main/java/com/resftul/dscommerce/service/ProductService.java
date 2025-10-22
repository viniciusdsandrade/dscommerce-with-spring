package com.resftul.dscommerce.service;

import com.resftul.dscommerce.dto.product.ProductDTO;
import com.resftul.dscommerce.dto.product.ProductMinDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.transaction.annotation.Propagation.SUPPORTS;

public interface ProductService {

    @Transactional
    ProductDTO insert(ProductDTO productDTO);

    @Transactional
    ProductDTO update(Long id, ProductDTO productDTO);

    @Transactional(propagation = SUPPORTS)
    void delete(Long id);

    ProductDTO findById(Long id);

    Page<ProductMinDTO> findAll(String name, Pageable pageable);

    Page<ProductDTO> findByName(String name, Pageable pageable);
}