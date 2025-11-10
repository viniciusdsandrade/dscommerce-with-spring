package com.resftul.dscommerce.dto.product;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.resftul.dscommerce.config.BigDecimalDeserializer;
import com.resftul.dscommerce.dto.CategoryDTO;
import com.resftul.dscommerce.entity.Category;
import com.resftul.dscommerce.entity.Product;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductDTO {

    private Long id;

    @Size(min = 3, max = 80, message = "Name must be between 3 and 80 characters")
    @NotBlank(message = "Name required")
    private String name;

    @Size(max = 5000, message = "Max 5000 characters")
    private String description;

    @NotNull(message = "Price required")
    @Digits(integer = 8, fraction = 2, message = "Price deve ter até 8 dígitos inteiros e 2 decimais")
    @DecimalMin(value = "0.01", message = "Price deve ser >= 0.01")
    @DecimalMax(value = "100000.00", inclusive = false, message = "Price deve ser < 100000.00")
    @JsonDeserialize(using = BigDecimalDeserializer.class)
    private BigDecimal price;

    @URL(protocol = "https", message = "Invalid URL (exigir https)")
    private String imgUrl;

    @NotEmpty(message = "Deve ter pelo menos uma categoria")
    private List<CategoryDTO> categories = new ArrayList<>();

    public ProductDTO(Product product) {
        id = product.getId();
        name = product.getName();
        description = product.getDescription();
        price = product.getPrice();
        imgUrl = product.getImgUrl();
        for (Category category : product.getCategories()) {
            categories.add(new CategoryDTO(category));
        }
    }
}
