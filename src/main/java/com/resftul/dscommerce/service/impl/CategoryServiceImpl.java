package com.resftul.dscommerce.service.impl;

import com.resftul.dscommerce.dto.CategoryDTO;
import com.resftul.dscommerce.entity.Category;
import com.resftul.dscommerce.repository.CategoryRepository;
import com.resftul.dscommerce.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("categoryService")
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository repository;

    public CategoryServiceImpl(CategoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<CategoryDTO> findAll() {
        List<Category> result = repository.findAll();
        return result.stream()
                .map(CategoryDTO::new)
                .toList();
    }
}
