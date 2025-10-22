package com.resftul.dscommerce.mapper;

import com.resftul.dscommerce.dto.product.ProductDTO;
import com.resftul.dscommerce.entity.Product;
import com.resftul.dscommerce.repository.CategoryRepository;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    private final CategoryRepository categoryRepository;

    public ProductMapper(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public void copyToEntity(ProductDTO dto, Product entity) {
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        entity.setImgUrl(dto.getImgUrl());

        entity.getCategories().clear();
        if (dto.getCategories() != null) {
            dto.getCategories().forEach(id ->
                    entity.getCategories().add(categoryRepository.getReferenceById(id.getId()))
            );
        }
    }
}
