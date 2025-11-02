package com.resftul.dscommerce.service;

import com.resftul.dscommerce.dto.CategoryDTO;
import com.resftul.dscommerce.entity.Category;
import com.resftul.dscommerce.repository.CategoryRepository;
import com.resftul.dscommerce.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private static Category category(Long id, String name) {
        return new Category(
                id,
                name
        );
    }

    @Test
    @DisplayName("findAll: mapeia List<Category> -> List<CategoryDTO> preservando ordem e valores")
    void findAll_ok() {
        var category1 = category(1L, "Informática");
        var category2 = category(2L, "Eletrônicos");
        when(categoryRepository.findAll()).thenReturn(List.of(category1, category2));

        List<CategoryDTO> out = categoryService.findAll();

        assertThat(out).containsExactly(
                new CategoryDTO(1L, "Informática"),
                new CategoryDTO(2L, "Eletrônicos")
        );
        verify(categoryRepository).findAll();
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    @DisplayName("findAll: retorna lista vazia quando repositório não possui registros")
    void findAll_empty() {
        when(categoryRepository.findAll()).thenReturn(emptyList());

        List<CategoryDTO> out = categoryService.findAll();

        assertThat(out).isEmpty();
        verify(categoryRepository).findAll();
        verifyNoMoreInteractions(categoryRepository);
    }
}