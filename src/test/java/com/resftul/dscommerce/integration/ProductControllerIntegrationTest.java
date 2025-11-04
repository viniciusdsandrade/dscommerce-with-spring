package com.resftul.dscommerce.integration;

import com.resftul.dscommerce.dto.product.ProductDTO;
import com.resftul.dscommerce.dto.product.ProductMinDTO;
import com.resftul.dscommerce.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.endsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("h2")
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Test
    @DisplayName("GET /products/{id} -> 200 e corpo JSON com id e name")
    void findById_ok() throws Exception {
        ProductDTO dto = Mockito.mock(ProductDTO.class);
        when(dto.getId()).thenReturn(1L);
        when(dto.getName()).thenReturn("PC Gamer");
        when(productService.findById(1L)).thenReturn(dto);

        mockMvc.perform(get("/products/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("PC Gamer"));
    }

    @Test
    @DisplayName("GET /products -> 200 e página vazia (shape: content + page)")
    void findAll_ok_emptyPage() throws Exception {
        when(productService.findAll(eq(""), any())).thenReturn(Page.empty());

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.page").exists())
                .andExpect(jsonPath("$.page.totalElements").value(0));
    }

    @Test
    @DisplayName("GET /products?name=pc -> 200 e página com itens (shape: content + page)")
    void findAll_ok_withContent() throws Exception {
        ProductMinDTO p1 = Mockito.mock(ProductMinDTO.class);
        when(p1.getId()).thenReturn(10L);
        when(p1.getName()).thenReturn("PC Gamer");

        ProductMinDTO p2 = Mockito.mock(ProductMinDTO.class);
        when(p2.getId()).thenReturn(11L);
        when(p2.getName()).thenReturn("PC Office");

        PageRequest pr = PageRequest.of(0, 2);
        Page<ProductMinDTO> page = new PageImpl<>(List.of(p1, p2), pr, 2);

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
        var created = Mockito.mock(ProductDTO.class);
        when(created.getId()).thenReturn(100L);
        when(created.getName()).thenReturn("Notebook Pro");
        when(productService.insert(any(ProductDTO.class))).thenReturn(created);

        String requestJson = """
        {
          "name": "Notebook Pro",
          "description": "Ultrabook leve",
          "price": 8999.90,
          "imgUrl": "https://example.com/note.jpg",
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
    @DisplayName("PUT /products/{id} -> 200 OK e corpo atualizado")
    void update_ok() throws Exception {
        ProductDTO updated = Mockito.mock(ProductDTO.class);
        when(updated.getId()).thenReturn(5L);
        when(updated.getName()).thenReturn("Mouse Gamer");
        when(productService.update(eq(5L), any(ProductDTO.class))).thenReturn(updated);

        String requestJson = """
        {
          "name": "Mouse Gamer",
          "description": "RGB",
          "price": 199.90,
          "imgUrl": "https://example.com/mouse.jpg",
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
