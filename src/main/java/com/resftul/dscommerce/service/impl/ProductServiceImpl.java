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

import static org.springframework.transaction.annotation.Propagation.SUPPORTS;

@Service("productService")
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;
    private final ProductMapper mapper;

    public ProductServiceImpl(ProductRepository repository, ProductMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public ProductDTO insert(ProductDTO productDTO) {
        if (repository.existsByNameAndDescription(productDTO.getName(), productDTO.getDescription())) {
            throw new ProductAlreadyExistsException("Um produto com o mesmo nome e descrição já existe.");
        }

        Product entity = new Product();
        mapper.copyToEntity(productDTO, entity);
        Product saved = repository.save(entity);

        return new ProductDTO(saved);
    }

    @Override
    public ProductDTO findById(Long id) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recurso não encontrado"));
        return new ProductDTO(product);
    }

    @Override
    public Page<ProductDTO> findByName(String name, Pageable pageable) {
        return repository.searchByName(name, pageable)
                .map(ProductDTO::new);
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
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("Product not found with id " + id);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException(
                    String.format("Cannot delete product with id %d because it has associated orders.", id), e);
        }
    }

    @Override
    @Transactional
    public ProductDTO update(Long id, ProductDTO productDTO) {
        Product entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + id));

        mapper.copyToEntity(productDTO, entity);
        Product updated = repository.save(entity);

        return new ProductDTO(updated);
    }
}