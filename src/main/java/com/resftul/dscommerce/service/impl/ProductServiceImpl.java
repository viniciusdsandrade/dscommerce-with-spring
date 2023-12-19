package com.resftul.dscommerce.service.impl;

import com.resftul.dscommerce.dto.ProductDTO;
import com.resftul.dscommerce.entity.Product;
import com.resftul.dscommerce.exception.ProductAlreadyExistsException;
import com.resftul.dscommerce.exception.ResourceNotFoundException;
import com.resftul.dscommerce.mapper.ProductMapper;
import com.resftul.dscommerce.repository.ProductRepository;
import com.resftul.dscommerce.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);


    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public ProductDTO save(ProductDTO productDTO) {

        if (productRepository.existsByNameAndDescription(productDTO.getName(), productDTO.getDescription()))
            throw new ProductAlreadyExistsException("Um produto com o mesmo nome e descrição já existe.");

        Product product = ProductMapper.mapToProduct(productDTO);
        Product savedProduct = productRepository.save(product);

        return ProductMapper.mapToProductDTO(savedProduct);
    }

    @Override
    public ProductDTO findProductById(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        return ProductMapper.mapToProductDTO(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> findByName(String name, Pageable pageable) {

        return productRepository.searchByName(name, pageable)
                .map(ProductMapper::mapToProductDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> findAll(String name, Pageable pageable) {
        Page<Product> productPage = productRepository.searchByName(name, pageable);
        return productPage.map(ProductMapper::mapToProductDTO);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {

        try {
            productRepository.deleteById(id);
            logger.info("Product with id {} deleted successfully.", id);
        } catch (EmptyResultDataAccessException err) {
            throw new ResourceNotFoundException("Product", "id", id);
        } catch (DataIntegrityViolationException err) {
            String message = String.format("Cannot delete product with id %d because it has associated orders.", id);
            logger.error(message, err);
            throw new DataIntegrityViolationException(message, err);
        }
    }

    @Override
    public ProductDTO update(Long id, ProductDTO productDTO) {

        Product productToUpdate = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        productToUpdate.setName(productDTO.getName());
        productToUpdate.setDescription(productDTO.getDescription());
        productToUpdate.setPrice(productDTO.getPrice());
        productToUpdate.setImgUrl(productDTO.getImageUrl());

        Product updatedProduct = productRepository.save(productToUpdate);

        return ProductMapper.mapToProductDTO(updatedProduct);
    }

    @Override
    public ProductDTO update(ProductDTO productDTO) {

        Product productToUpdate = productRepository.findById(productDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productDTO.getId()));

        productToUpdate.setName(productDTO.getName());
        productToUpdate.setDescription(productDTO.getDescription());
        productToUpdate.setPrice(productDTO.getPrice());
        productToUpdate.setImgUrl(productDTO.getImageUrl());

        Product updatedProduct = productRepository.save(productToUpdate);

        return ProductMapper.mapToProductDTO(updatedProduct);
    }
}