package com.resftul.dscommerce.controller;

import com.resftul.dscommerce.dto.CategoryDTO;
import com.resftul.dscommerce.dto.product.ProductDTO;
import com.resftul.dscommerce.dto.product.ProductMinDTO;
import com.resftul.dscommerce.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.endsWith;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("h2")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @TestConfiguration
    static class TestBeans {
        @Bean
        PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }

    @Test
    @DisplayName("GET /products/{id} -> 200 e corpo com id e name")
    void findById_ok() throws Exception {
        ProductDTO productDTO = new ProductDTO(
                1L, "Notebook", "desc",
                new BigDecimal("5499.90"),
                "https://img.example/notebook.jpg",
                List.of(new CategoryDTO(1L, "Informática"))
        );
        when(productService.findById(1L)).thenReturn(productDTO);

        mockMvc.perform(get("/products/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Notebook"));
    }

    @Test
    @DisplayName("GET /products -> 200 com página vazia (shape: content + page)")
    void findAll_empty() throws Exception {
        Page<ProductMinDTO> empty = new PageImpl<>(emptyList(), PageRequest.of(0, 10), 0);
        when(productService.findAll(eq(""), any())).thenReturn(empty);

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.page.totalElements").value(0));
    }

    @Test
    @DisplayName("GET /products?name=pc -> 200 com conteúdo e metadados em $.page")
    void findAll_withContent() throws Exception {
        var product1 = new ProductMinDTO(new com.resftul.dscommerce.entity.Product(
                10L, "PC Gamer", "d", new BigDecimal("3500.00"), "https://img/pc1.jpg"
        ));
        var product2 = new ProductMinDTO(new com.resftul.dscommerce.entity.Product(
                11L, "PC Office", "d", new BigDecimal("2500.00"), "https://img/pc2.jpg"
        ));
        Page<ProductMinDTO> page = new PageImpl<>(List.of(product1, product2), PageRequest.of(0, 2), 2);

        when(productService.findAll(eq("pc"), any())).thenReturn(page);

        mockMvc.perform(get("/products").param("name", "pc"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].id").value(10))
                .andExpect(jsonPath("$.content[0].name").value("PC Gamer"))
                .andExpect(jsonPath("$.content[1].id").value(11))
                .andExpect(jsonPath("$.content[1].name").value("PC Office"))
                .andExpect(jsonPath("$.page.totalElements").value(2))
                .andExpect(jsonPath("$.page.size").value(2))
                .andExpect(jsonPath("$.page.number").value(0));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /products -> 201 Created, Location e corpo com id e name (payload válido)")
    void insert_created() throws Exception {
        ProductDTO productDTO = new ProductDTO(
                100L, "Notebook Pro", "Ultra leve",
                new BigDecimal("8999.90"),
                "https://img.example/note-pro.jpg",
                List.of(new CategoryDTO(1L, "Informática"))
        );
        when(productService.insert(any(ProductDTO.class))).thenReturn(productDTO);

        String requestJson = """
        {
          "name": "Notebook Pro",
          "description": "Ultra leve",
          "price": 8999.90,
          "imgUrl": "https://img.example/note-pro.jpg",
          "categories": [ { "id": 1, "name": "Informática" } ]
        }
        """;

        mockMvc.perform(post("/products")
                        .contentType(APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith("/products/100")))
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.name").value("Notebook Pro"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /products -> 400 quando validação falha (ex.: sem name ou sem categories)")
    void insert_badRequest_validation() throws Exception {
        String invalidJson = """
        {
          "description": "qualquer",
          "price": 10.00,
          "imgUrl": "https://img.example/x.jpg",
          "categories": []
        }
        """;

        mockMvc.perform(post("/products")
                        .contentType(APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PUT /products/{id} -> 200 e corpo atualizado")
    void update_ok() throws Exception {
        ProductDTO productDTO = new ProductDTO(
                5L, "Mouse Gamer", "RGB",
                new BigDecimal("199.90"),
                "https://img.example/mouse.jpg",
                List.of(new CategoryDTO(2L, "Acessórios"))
        );
        when(productService.update(eq(5L), any(ProductDTO.class))).thenReturn(productDTO);

        String requestJson = """
        {
          "name": "Mouse Gamer",
          "description": "RGB",
          "price": 199.90,
          "imgUrl": "https://img.example/mouse.jpg",
          "categories": [ { "id": 2, "name": "Acessórios" } ]
        }
        """;

        mockMvc.perform(put("/products/{id}", 5L)
                        .contentType(APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.name").value("Mouse Gamer"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /products/{id} -> 204 No Content")
    void delete_noContent() throws Exception {
        doNothing().when(productService).delete(7L);

        mockMvc.perform(delete("/products/{id}", 7L))
                .andExpect(status().isNoContent());
    }
}
