package com.resftul.dscommerce.service.impl;

import com.resftul.dscommerce.dto.product.ProductDTO;
import com.resftul.dscommerce.dto.product.ProductMinDTO;
import com.resftul.dscommerce.entity.Product;
import com.resftul.dscommerce.exception.ProductAlreadyExistsException;
import com.resftul.dscommerce.exception.ResourceNotFoundException;
import com.resftul.dscommerce.mapper.ProductMapper;
import com.resftul.dscommerce.repository.ProductRepository;
import com.resftul.dscommerce.service.ProductService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static java.lang.String.format;
import static org.springframework.transaction.annotation.Propagation.SUPPORTS;

@Service("productService")
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Override
    @Transactional
    public ProductDTO insert(ProductDTO productDTO) {
        if (productRepository.existsByName(productDTO.getName()))
            throw new ProductAlreadyExistsException("Já existe um produto com nome " + productDTO.getName());

        try {
            var product = new Product();
            productMapper.updateEntityFromDto(productDTO, product);
            return new ProductDTO(productRepository.save(product));
        } catch (DataIntegrityViolationException e) {
            throw new ProductAlreadyExistsException("Já existe produto com este nome.");
        }
    }

    @Override
    public ProductDTO findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recurso não encontrado"));
        return new ProductDTO(product);
    }

    @Override
    public Page<ProductMinDTO> findAll(String name, Pageable pageable) {
        Page<Product> result = productRepository.searchByName(name, pageable);
        return result.map(ProductMinDTO::new);
    }

    @Override
    @Transactional(propagation = SUPPORTS)
    public void delete(Long id) {
        try {
            productRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("Product not found with id " + id);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException(
                    format("Cannot delete product with id %d because it has associated orders.", id), e);
        }
    }

    @Override
    @Transactional
    public ProductDTO update(Long id, ProductDTO productDTO) {
        Product entity = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + id));

        final String newName = productDTO.getName();
        if (newName != null && !newName.equals(entity.getName()) && productRepository.existsByName(newName))
            throw new ProductAlreadyExistsException("Já existe um produto com nome " + newName);

        try {
            productMapper.updateEntityFromDto(productDTO, entity);
            Product updated = productRepository.save(entity);
            return new ProductDTO(updated);
        } catch (DataIntegrityViolationException e) {
            throw new ProductAlreadyExistsException("Já existe um produto com nome " + newName);
        }
    }
}
