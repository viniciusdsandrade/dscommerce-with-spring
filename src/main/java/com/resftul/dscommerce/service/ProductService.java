package com.resftul.dscommerce.service;

import com.resftul.dscommerce.dto.ProductDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ProductService {

    @Transactional
    ProductDTO save(ProductDTO productDTO);

    ProductDTO findProductById(Long id);

    @Transactional
    ProductDTO update(Long id, ProductDTO productDTO);

    @Transactional(readOnly = true)
    Page<ProductDTO> findAll(String name,Pageable pageable);

    void delete(Long id);

    ProductDTO update(ProductDTO productDTO);

    @Transactional(readOnly = true)
    Page<ProductDTO> findByName(String name, Pageable pageable);
}