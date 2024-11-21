package com.resftul.dscommerce.service.impl;

import com.resftul.dscommerce.dto.ProductDTO;
import com.resftul.dscommerce.dto.ProductMinDTO;
import com.resftul.dscommerce.entity.Product;
import com.resftul.dscommerce.exception.ProductAlreadyExistsException;
import com.resftul.dscommerce.exception.ResourceNotFoundException;
import com.resftul.dscommerce.mapper.ProductMapper;
import com.resftul.dscommerce.repository.ProductRepository;
import com.resftul.dscommerce.service.ProductService;
import org.slf4j.Logger;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static java.lang.String.format;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.transaction.annotation.Propagation.SUPPORTS;

@Service("productService")
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;
    private static final Logger logger = getLogger(ProductServiceImpl.class);

    public ProductServiceImpl(ProductRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public ProductDTO insert(ProductDTO productDTO) {
        if (repository.existsByNameAndDescription(productDTO.getName(), productDTO.getDescription()))
            throw new ProductAlreadyExistsException("Um produto com o mesmo nome e descrição já existe.");

        Product product = ProductMapper.mapToProduct(productDTO);
        Product savedProduct = repository.save(product);

        return ProductMapper.mapToProductDTO(savedProduct);
    }

    @Override
    public ProductDTO findById(Long id) {
        Product product = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Recurso não encontrado"));
        return new ProductDTO(product);
    }

    @Override
    public Page<ProductDTO> findByName(String name, Pageable pageable) {
        return repository.searchByName(name, pageable)
                .map(ProductMapper::mapToProductDTO);
    }

    @Override
    public Page<ProductMinDTO> findAll(String name, Pageable pageable) {
        Page<Product> result = repository.searchByName(name, pageable);
        return result.map(ProductMinDTO::new);
    }

    @Override
    @Transactional(propagation = SUPPORTS)
    public void delete(Long id) {

        try {
            repository.deleteById(id);
            logger.info("Product with id {} deleted successfully.", id);
        } catch (EmptyResultDataAccessException err) {
            String message = format("Product with id %d not found.", id);
            logger.error(message, err);
            throw new ResourceNotFoundException("Product not found with id " + id);
        } catch (DataIntegrityViolationException err) {
            String message = format("Cannot delete product with id %d because it has associated orders.", id);
            logger.error(message, err);
            throw new DataIntegrityViolationException(message, err);
        }
    }

    @Override
    public ProductDTO update(Long id, ProductDTO productDTO) {

        Product productToUpdate = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + id));

        productToUpdate.setName(productDTO.getName());
        productToUpdate.setDescription(productDTO.getDescription());
        productToUpdate.setPrice(productDTO.getPrice());
        productToUpdate.setImgUrl(productDTO.getImageUrl());

        Product updatedProduct = repository.save(productToUpdate);

        return ProductMapper.mapToProductDTO(updatedProduct);
    }
}