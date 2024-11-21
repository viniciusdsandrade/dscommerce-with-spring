package com.resftul.dscommerce.service;

import com.resftul.dscommerce.dto.CategoryDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CategoryService {
    @Transactional(readOnly = true)
    List<CategoryDTO> findAll();
}
