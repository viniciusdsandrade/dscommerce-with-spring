package com.resftul.dscommerce.mapper;

import com.resftul.dscommerce.dto.ProductDTO;
import com.resftul.dscommerce.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AutoProductMapper {

    AutoProductMapper MAPPER = Mappers.getMapper(AutoProductMapper.class);
    
    ProductDTO mapsToProductDTO(Product product);
    Product mapsToProduct(ProductDTO productDTO);
}
