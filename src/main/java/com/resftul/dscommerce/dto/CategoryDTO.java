package com.resftul.dscommerce.dto;

import com.resftul.dscommerce.entity.Category;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@EqualsAndHashCode
@NoArgsConstructor
@Getter
public class CategoryDTO {
    private Long id;
    private String name;

    public CategoryDTO(Category entity) {
        id = entity.getId();
        name = entity.getName();
    }
}
