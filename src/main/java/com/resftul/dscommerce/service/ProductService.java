package com.resftul.dscommerce.service;

import com.resftul.dscommerce.dto.product.ProductDTO;
import com.resftul.dscommerce.dto.product.ProductMinDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public interface ProductService {

    @Transactional
    ProductDTO insert(@Valid ProductDTO productDTO);

    @Transactional
    ProductDTO update(Long id, @Valid ProductDTO productDTO);

    @Transactional
    void delete(Long id);

    ProductDTO findById(Long id);

    Page<ProductMinDTO> findAll(String name, Pageable pageable);
}