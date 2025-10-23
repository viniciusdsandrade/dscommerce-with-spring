package com.resftul.dscommerce.mapper;

import com.resftul.dscommerce.dto.product.ProductDTO;
import com.resftul.dscommerce.entity.Category;
import com.resftul.dscommerce.entity.Product;
import com.resftul.dscommerce.repository.CategoryRepository;
import org.springframework.stereotype.Component;

import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Component
public class ProductMapper {

    private final CategoryRepository categoryRepository;

    public ProductMapper(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public void updateEntityFromDto(ProductDTO productDTO, Product product) {
        Set<Category> categories = (productDTO.getCategories() == null)
                ? null
                : productDTO.getCategories()
                .stream()
                .map(c -> categoryRepository.getReferenceById(c.getId()))
                .collect(toSet());

        product.replaceAttributes(
                productDTO.getName(),
                productDTO.getDescription(),
                productDTO.getPrice(),
                productDTO.getImgUrl(),
                categories
        );
    }
}
