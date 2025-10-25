package com.resftul.dscommerce.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import com.resftul.dscommerce.entity.Category;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@EqualsAndHashCode
@NoArgsConstructor
@Getter
public class CategoryDTO {
    private Long id;
    private String name;

    public CategoryDTO(Category category) {
        id = category.getId();
        name = category.getName();
    }

    @JsonValue
    public String getName() {
        return name;
    }
}
