package com.resftul.dscommerce.dto;

import com.resftul.dscommerce.entity.Product;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import java.util.Objects;

@Getter
@AllArgsConstructor
@NoArgsConstructor
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

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;
        if (this.getClass() != o.getClass()) return false;

        ProductDTO that = (ProductDTO) o;

        return Objects.equals(this.getId(), that.getId()) &&
               Objects.equals(this.getName(), that.getName()) &&
               Objects.equals(this.getDescription(), that.getDescription()) &&
               Objects.equals(this.getPrice(), that.getPrice()) &&
               Objects.equals(this.getImageUrl(), that.getImageUrl());
    }

    @Override
    public int hashCode() {

        final int prime = 31;
        int hash = 1;

        hash *= prime + (this.getId() == null ? 0 : this.getId().hashCode());
        hash *= prime + (this.getName() == null ? 0 : this.getName().hashCode());
        hash *= prime + (this.getDescription() == null ? 0 : getDescription().hashCode());
        hash *= prime + (this.getPrice() == null ? 0 : this.getPrice().hashCode());
        hash *= prime + (this.getImageUrl() == null ? 0 : this.getImageUrl().hashCode());

        if (hash < 0)
            hash = -hash;

        return hash;
    }
}