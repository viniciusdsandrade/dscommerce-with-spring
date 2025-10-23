package com.resftul.dscommerce.dto.product;


import com.resftul.dscommerce.entity.Product;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@EqualsAndHashCode
@NoArgsConstructor
@Getter
public class ProductMinDTO {

    private Long id;
    private String name;
    private BigDecimal price;
    private String imgUrl;

    public ProductMinDTO(Product product) {
        id = product.getId();
        name = product.getName();
        price = product.getPrice();
        imgUrl = product.getImgUrl();
    }
}
