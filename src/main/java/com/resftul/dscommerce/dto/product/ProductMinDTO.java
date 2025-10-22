package com.resftul.dscommerce.dto.product;


import com.resftul.dscommerce.entity.Product;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@EqualsAndHashCode
@NoArgsConstructor
@Getter
public class ProductMinDTO {

    private Long id;
    private String name;
    private Double price;
    private String imgUrl;

    public ProductMinDTO(
            Long id,
            String name,
            Double price,
            String imgUrl
    ) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imgUrl = imgUrl;
    }

    public ProductMinDTO(Product product) {
        id = product.getId();
        name = product.getName();
        price = product.getPrice();
        imgUrl = product.getImgUrl();
    }
}
