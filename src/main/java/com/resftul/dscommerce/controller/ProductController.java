package com.resftul.dscommerce.controller;

import com.resftul.dscommerce.dto.product.ProductDTO;
import com.resftul.dscommerce.dto.product.ProductMinDTO;
import com.resftul.dscommerce.service.ProductService;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

import static org.springframework.http.ResponseEntity.*;

@RestController
@RequestMapping({ "/products"})
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PermitAll
    @GetMapping(value = "/{id}")
    public ResponseEntity<ProductDTO> findById(@PathVariable Long id) {
        ProductDTO productDTO = productService.findById(id);
        return ok(productDTO);
    }

    @PermitAll
    @GetMapping
    public ResponseEntity<Page<ProductMinDTO>> findAll(
            @RequestParam(name = "name", defaultValue = "") String name,
            Pageable pageable
    ) {
        Page<ProductMinDTO> productMinDTOPage = productService.findAll(name, pageable);
        return ok(productMinDTOPage);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ProductDTO> insert(@Valid @RequestBody ProductDTO productDTO) {
        ProductDTO createdDto = productService.insert(productDTO);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdDto.getId())
                .toUri();
        return created(uri).body(createdDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/{id}")
    public ResponseEntity<ProductDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductDTO productDTO
    ) {
        ProductDTO updatedDto = productService.update(id, productDTO);
        return ok(updatedDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return noContent().build();
    }
}
