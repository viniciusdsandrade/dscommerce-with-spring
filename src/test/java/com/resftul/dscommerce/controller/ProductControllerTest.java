package com.resftul.dscommerce.controller;

import com.resftul.dscommerce.dto.CategoryDTO;
import com.resftul.dscommerce.dto.product.ProductDTO;
import com.resftul.dscommerce.dto.product.ProductMinDTO;
import com.resftul.dscommerce.exception.ProductAlreadyExistsException;
import com.resftul.dscommerce.exception.ResourceNotFoundException;
import com.resftul.dscommerce.service.ProductService;
import com.resftul.dscommerce.util.TestSecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Objects.requireNonNull;
import static org.hamcrest.Matchers.endsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ProductController.class)
@AutoConfigureMockMvc
@ActiveProfiles("h2")
@Import({ TestSecurityConfig.class, ProductControllerTest.TestMethodSecurityConfig.class })
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

    @TestConfiguration
    @EnableMethodSecurity(jsr250Enabled = true, proxyTargetClass = true)
    static class TestMethodSecurityConfig {
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
    @DisplayName("GET /products/{id} -> 406 quando Accept não suportado")
    void findById_notAcceptable() throws Exception {
        ProductDTO productDTO = new ProductDTO(
                1L, "Notebook", "desc",
                new BigDecimal("5499.90"),
                "https://img.example/notebook.jpg",
                List.of(new CategoryDTO(1L, "Informática"))
        );
        when(productService.findById(1L)).thenReturn(productDTO);

        mockMvc.perform(get("/products/{id}", 1L)
                        .header("Accept", "application/xml"))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    @DisplayName("GET /products/{id} -> 404 quando não encontrado")
    void findById_notFound() throws Exception {
        when(productService.findById(999L))
                .thenThrow(new ResourceNotFoundException("Product 999"));

        mockMvc.perform(get("/products/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$[0].message").value("Product 999"));
    }

    @Test
    @DisplayName("GET /products/{id} -> 400 quando id não numérico (type mismatch)")
    void findById_badRequest_onTypeMismatch() throws Exception {
        mockMvc.perform(get("/products/{id}", "abc"))
                .andExpect(status().isBadRequest());
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
                .andExpect(jsonPath("$.page.totalElements").value(0))
                .andExpect(jsonPath("$.page.size").value(10))
                .andExpect(jsonPath("$.page.number").value(0));
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
    @DisplayName("GET /products -> Pageable com page=2,size=50,sort=name,asc é repassado ao service e name='pc'")
    void findAll_capturesPageable() throws Exception {
        Page<ProductMinDTO> page = new PageImpl<>(emptyList(), PageRequest.of(2, 50), 0);
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass(String.class);
        when(productService.findAll(anyString(), any())).thenReturn(page);

        mockMvc.perform(get("/products")
                        .param("name", "pc")
                        .param("page", "2")
                        .param("size", "50")
                        .param("sort", "name,asc"))
                .andExpect(status().isOk());

        verify(productService).findAll(nameCaptor.capture(), pageableCaptor.capture());
        assertEquals("pc", nameCaptor.getValue());
        Pageable captorValue = pageableCaptor.getValue();
        assertEquals(2, captorValue.getPageNumber());
        assertEquals(50, captorValue.getPageSize());
        assertTrue(requireNonNull(captorValue.getSort().getOrderFor("name")).isAscending());
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
                        .with(csrf())
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
    @DisplayName("POST /products -> 409 quando nome já existe (exceção de domínio ProductAlreadyExistsException)")
    void insert_conflict_onDuplicateName() throws Exception {
        when(productService.insert(any(ProductDTO.class)))
                .thenThrow(new ProductAlreadyExistsException("name already exists"));

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
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$[0].message").value("name already exists"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /products -> 400 quando validação falha (service NÃO chamado)")
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
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$[0].errorCode").value("METHOD_ARGUMENT_NOT_VALID_ERROR"));

        verify(productService, never()).insert(any(ProductDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /products -> 415 quando Content-Type não é application/json")
    void insert_unsupportedMediaType() throws Exception {
        mockMvc.perform(post("/products")
                        .with(csrf())
                        .contentType("text/plain")
                        .content("not json"))
                .andExpect(status().isUnsupportedMediaType());

        verify(productService, never()).insert(any(ProductDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /products -> 406 quando Accept não é suportado")
    void insert_notAcceptable() throws Exception {
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
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .header("Accept", "application/xml")
                        .content(requestJson))
                .andExpect(status().isNotAcceptable());
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
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.name").value("Mouse Gamer"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PUT /products/{id} -> 404 quando não encontrado")
    void update_notFound() throws Exception {
        when(productService.update(eq(123L), any(ProductDTO.class)))
                .thenThrow(new ResponseStatusException(NOT_FOUND, "Product 123"));

        String requestJson = """
    {
      "name": "Notebook Pro",
      "description": "Y",
      "price": 1.00,
      "imgUrl": "https://img.example/x.jpg",
      "categories": [ { "id": 1, "name": "Informática" } ]
    }
    """;

        mockMvc.perform(put("/products/{id}", 123L)
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /products/{id} -> 204 No Content")
    void delete_noContent() throws Exception {
        doNothing().when(productService).delete(7L);

        mockMvc.perform(delete("/products/{id}", 7L)
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /products/{id} -> 404 quando não encontrado")
    void delete_notFound() throws Exception {
        doThrow(new ResourceNotFoundException("Product 888"))
                .when(productService).delete(888L);

        mockMvc.perform(delete("/products/{id}", 888L)
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$[0].message").value("Product 888"));
    }

    @Test
    @DisplayName("POST /products -> 403 quando não autenticado")
    void insert_forbidden_whenUnauthenticated() throws Exception {
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
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isForbidden());

        verify(productService, never()).insert(any(ProductDTO.class));
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    @DisplayName("POST /products -> 403 quando autenticado sem ROLE_ADMIN")
    void insert_forbidden_whenNotAdmin() throws Exception {
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
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isForbidden());

        verify(productService, never()).insert(any(ProductDTO.class));
    }
}
