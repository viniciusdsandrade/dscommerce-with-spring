package com.resftul.dscommerce.service;

import com.resftul.dscommerce.dto.CategoryDTO;
import com.resftul.dscommerce.dto.product.ProductDTO;
import com.resftul.dscommerce.dto.product.ProductMinDTO;
import com.resftul.dscommerce.entity.Product;
import com.resftul.dscommerce.exception.ProductAlreadyExistsException;
import com.resftul.dscommerce.exception.ResourceNotFoundException;
import com.resftul.dscommerce.mapper.ProductMapper;
import com.resftul.dscommerce.repository.ProductRepository;
import com.resftul.dscommerce.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock private ProductRepository productRepository;
    @Mock private ProductMapper productMapper;

    @InjectMocks private ProductServiceImpl productService;

    private static ProductDTO dto(String name) {
        return new ProductDTO(
                null,
                name,
                "desc " + name,
                new BigDecimal("123.45"),
                "https://img",
                List.of(new CategoryDTO(1L, "Informática"))
        );
    }

    private static Product entity(Long id, String name) {
        Product p = new Product();
        p.setId(id);
        p.setName(name);
        p.setDescription("desc " + name);
        p.setPrice(new BigDecimal("123.45"));
        p.setImgUrl("https://img");
        return p;
    }

    private void stubMapperCopy() {
        doAnswer(inv -> {
            ProductDTO src = inv.getArgument(0);
            Product tgt = inv.getArgument(1);
            tgt.setName(src.getName());
            tgt.setDescription(src.getDescription());
            tgt.setPrice(src.getPrice());
            tgt.setImgUrl(src.getImgUrl());
            return null;
        }).when(productMapper).updateEntityFromDto(any(ProductDTO.class), any(Product.class));
    }

    @Test
    @DisplayName("insert: persiste quando nome é único e retorna ProductDTO")
    void insert_ok() {
        var in = dto("Notebook Ultra");
        when(productRepository.existsByName("Notebook Ultra")).thenReturn(false);
        stubMapperCopy();
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> {
            Product p = inv.getArgument(0);
            p.setId(10L);
            return p;
        });

        ProductDTO out = productService.insert(in);

        assertThat(out.getId()).isEqualTo(10L);
        assertThat(out.getName()).isEqualTo("Notebook Ultra");
        verify(productRepository).existsByName("Notebook Ultra");
        verify(productMapper).updateEntityFromDto(eq(in), any(Product.class));
        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("insert: lança ProductAlreadyExistsException quando nome já existe")
    void insert_duplicateName() {
        var in = dto("Duplicado");
        when(productRepository.existsByName("Duplicado")).thenReturn(true);

        assertThrows(ProductAlreadyExistsException.class, () -> productService.insert(in));
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("insert: ao violar integridade, traduz para ProductAlreadyExistsException")
    void insert_translatesDataIntegrity() {
        var in = dto("Qualquer");
        when(productRepository.existsByName("Qualquer")).thenReturn(false);
        stubMapperCopy();
        when(productRepository.save(any(Product.class)))
                .thenThrow(new DataIntegrityViolationException("unique"));

        assertThrows(ProductAlreadyExistsException.class, () -> productService.insert(in));
    }

    @Test
    @DisplayName("findById: retorna DTO quando existe")
    void findById_ok() {
        when(productRepository.findById(5L)).thenReturn(Optional.of(entity(5L, "PC")));
        ProductDTO out = productService.findById(5L);
        assertThat(out.getId()).isEqualTo(5L);
        assertThat(out.getName()).isEqualTo("PC");
    }

    @Test
    @DisplayName("findById: lança ResourceNotFoundException quando não existe")
    void findById_notFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> productService.findById(99L));
    }

    @Test
    @DisplayName("findAll: mapeia Page<Product> -> Page<ProductMinDTO>")
    void findAll_ok() {
        var p1 = entity(1L, "PC Gamer");
        var page = new PageImpl<>(List.of(p1), PageRequest.of(0, 1), 1);
        when(productRepository.searchByName(eq("pc"), any(Pageable.class))).thenReturn(page);

        Page<ProductMinDTO> out = productService.findAll("pc", PageRequest.of(0, 1));

        assertThat(out.getTotalElements()).isEqualTo(1);
        assertThat(out.getContent()).hasSize(1);
        assertThat(out.getContent().getFirst().getId()).isEqualTo(1L);
        assertThat(out.getContent().getFirst().getName()).isEqualTo("PC Gamer");
    }

    @Test
    @DisplayName("findAll: retorna página vazia quando não há resultados")
    void findAll_empty() {
        var empty = new PageImpl<Product>(emptyList(), PageRequest.of(0, 10), 0);
        when(productRepository.searchByName(eq(""), any(Pageable.class))).thenReturn(empty);

        Page<ProductMinDTO> out = productService.findAll("", PageRequest.of(0, 10));

        assertThat(out.getTotalElements()).isZero();
        assertThat(out.getContent()).isEmpty();
    }

    @Test
    @DisplayName("delete: invoca deleteById quando existe (não lança)")
    void delete_ok() {
        productService.delete(7L);
        verify(productRepository).deleteById(7L);
    }

    @Test
    @DisplayName("delete: traduz EmptyResultDataAccessException -> ResourceNotFoundException")
    void delete_notFound() {
        doThrow(new EmptyResultDataAccessException(1)).when(productRepository).deleteById(123L);
        assertThrows(ResourceNotFoundException.class, () -> productService.delete(123L));
    }

    @Test
    @DisplayName("delete: propaga DataIntegrityViolationException com mensagem customizada")
    void delete_integrityViolation() {
        doThrow(new DataIntegrityViolationException("fk"))
                .when(productRepository).deleteById(50L);

        assertThrows(DataIntegrityViolationException.class, () -> productService.delete(50L));
    }

    @Test
    @DisplayName("update: atualiza quando id existe e nome novo não conflita")
    void update_ok() {
        var existing = entity(10L, "Old");
        when(productRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(productRepository.existsByName("New")).thenReturn(false);
        stubMapperCopy();
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        var in = new ProductDTO(
                10L,
                "New",
                "desc",
                new BigDecimal("999.99"),
                "https://img2",
                List.of(new CategoryDTO(2L, "Eletrônicos"))
        );

        ProductDTO out = productService.update(10L, in);

        assertThat(out.getId()).isEqualTo(10L);
        assertThat(out.getName()).isEqualTo("New");
        verify(productRepository).findById(10L);
        verify(productRepository).existsByName("New");
        verify(productMapper).updateEntityFromDto(eq(in), eq(existing));
        verify(productRepository).save(eq(existing));
    }

    @Test
    @DisplayName("update: lança ResourceNotFoundException quando id não existe")
    void update_notFound() {
        when(productRepository.findById(404L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> productService.update(404L, dto("X")));
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("update: lança ProductAlreadyExistsException quando novo nome já existe")
    void update_duplicateName() {
        var existing = entity(1L, "Old");
        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(productRepository.existsByName("OldTaken")).thenReturn(true);

        var in = dto("OldTaken");
        assertThrows(ProductAlreadyExistsException.class, () -> productService.update(1L, in));
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("update: violação de integridade é traduzida para ProductAlreadyExistsException")
    void update_integrityViolation() {
        var existing = entity(1L, "Old");
        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(productRepository.existsByName("New")).thenReturn(false);
        stubMapperCopy();
        when(productRepository.save(any(Product.class)))
                .thenThrow(new DataIntegrityViolationException("unique"));

        var in = dto("New");
        assertThrows(ProductAlreadyExistsException.class, () -> productService.update(1L, in));
    }
}
