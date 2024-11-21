package com.resftul.dscommerce.service;

import com.resftul.dscommerce.dto.ProductDTO;
import com.resftul.dscommerce.dto.ProductMinDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.transaction.annotation.Propagation.SUPPORTS;

public interface ProductService {

    @Transactional
    ProductDTO insert(ProductDTO productDTO);

    ProductDTO findById(Long id);

    @Transactional
    ProductDTO update(Long id, ProductDTO productDTO);

    Page<ProductMinDTO> findAll(String name, Pageable pageable);

    @Transactional(propagation = SUPPORTS)
    void delete(Long id);

    Page<ProductDTO> findByName(String name, Pageable pageable);
}