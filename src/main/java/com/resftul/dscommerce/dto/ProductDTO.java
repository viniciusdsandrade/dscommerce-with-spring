package com.resftul.dscommerce.dto;

import com.resftul.dscommerce.entity.Product;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ProductDTO {

    private Long id;

    @Size(min = 3, max = 80, message = "Name must be between 3 and 80 characters")
    @NotBlank(message = "Name required")
    private String name;

    @Size(max = 5000, message = "Max 5000 characters")
    private String description;

    @DecimalMax(value = "100000.00", inclusive = false, message = "Price must be less than 100000.00")
    @Positive(message = "Price must be a positive value")
    @NotBlank(message = "Price required")
    private Double price;

    @URL(message = "Invalid URL")
    private String imageUrl;

    public ProductDTO(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.imageUrl = product.getImgUrl();
    }
}