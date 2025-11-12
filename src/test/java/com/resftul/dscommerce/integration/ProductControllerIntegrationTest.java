package com.resftul.dscommerce.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.text.MatchesPattern.matchesPattern;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_XML;
import static org.springframework.http.MediaType.TEXT_PLAIN;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("h2")
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /products/{id} -> 200 e corpo JSON com id e name")
    @Sql(scripts = {"/sql/products/clean.sql", "/sql/products/seed-product-1.sql"}, executionPhase = BEFORE_TEST_METHOD)
    void findById_ok() throws Exception {
        mockMvc.perform(get("/products/{id}", 1L).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("PC Gamer"));
    }

    @Test
    @DisplayName("GET /products -> 200 e página vazia (envelope: $.content + $.page)")
    @Sql(scripts = {"/sql/products/clean.sql"}, executionPhase = BEFORE_TEST_METHOD)
    void findAll_ok_emptyPage() throws Exception {
        mockMvc.perform(get("/products").accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.page").exists())
                .andExpect(jsonPath("$.page.totalElements").value(0))
                .andExpect(jsonPath("$.page.number").value(0));
    }

    @Test
    @DisplayName("GET /products?name=pc&size=2 -> 200 e página com itens (envelope: $.content + $.page)")
    @Sql(scripts = {"/sql/products/clean.sql", "/sql/products/seed-two-products.sql"}, executionPhase = BEFORE_TEST_METHOD)
    void findAll_ok_withContent() throws Exception {
        mockMvc.perform(get("/products")
                        .param("name", "pc")
                        .param("page", "0")
                        .param("size", "2")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value(10))
                .andExpect(jsonPath("$.content[0].name").value("PC Gamer"))
                .andExpect(jsonPath("$.content[1].id").value(11))
                .andExpect(jsonPath("$.content[1].name").value("PC Office"))
                .andExpect(jsonPath("$.page.totalElements").value(2))
                .andExpect(jsonPath("$.page.size").value(2))
                .andExpect(jsonPath("$.page.number").value(0));
    }

    @Test
    @DisplayName("GET /products ordenado por name,desc -> ordem determinística")
    @Sql(scripts = {"/sql/products/clean.sql", "/sql/products/seed-two-products.sql"}, executionPhase = BEFORE_TEST_METHOD)
    void findAll_sorted_desc() throws Exception {
        mockMvc.perform(get("/products")
                        .param("sort", "name,desc")
                        .param("page", "0")
                        .param("size", "2")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].name").value("PC Office"))
                .andExpect(jsonPath("$.content[1].name").value("PC Gamer"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /products -> 201 Created, Location e corpo com id e name (payload válido) + round-trip")
    @Sql(scripts = {"/sql/products/clean.sql", "/sql/products/seed-categories-basic.sql"}, executionPhase = BEFORE_TEST_METHOD)
    void insert_created() throws Exception {
        String requestJson = """
            {
              "name": "Notebook Pro",
              "description": "Ultrabook leve",
              "price": 8999.90,
              "imgUrl": "https://example.com/note.jpg",
              "categories": [ { "id": 1, "name": "Informática" } ]
            }
            """;

        MvcResult res = mockMvc.perform(post("/products")
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", matchesPattern(".*/products/\\d+$")))
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Notebook Pro"))
                .andReturn();

        String location = res.getResponse().getHeader("Location");

        mockMvc.perform(get(location).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Notebook Pro"))
                .andExpect(jsonPath("$.imgUrl").value("https://example.com/note.jpg"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PUT /products/{id} -> 200 OK e corpo atualizado")
    @Sql(scripts = {"/sql/products/clean.sql", "/sql/products/seed-product-5.sql"}, executionPhase = BEFORE_TEST_METHOD)
    void update_ok() throws Exception {
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
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.name").value("Mouse Gamer"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /products/{id} -> 204 No Content")
    @Sql(scripts = {"/sql/products/clean.sql", "/sql/products/seed-product-7.sql"}, executionPhase = BEFORE_TEST_METHOD)
    void delete_noContent() throws Exception {
        mockMvc.perform(delete("/products/{id}", 7L)
                        .with(csrf())
                        .accept(APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /products/{id} inexistente -> 404")
    @Sql(scripts = {"/sql/products/clean.sql"}, executionPhase = BEFORE_TEST_METHOD)
    void findById_notFound() throws Exception {
        mockMvc.perform(get("/products/{id}", 9999L).accept(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PUT /products/{id} inexistente -> 404")
    @Sql(scripts = {"/sql/products/clean.sql", "/sql/products/seed-categories-basic.sql"}, executionPhase = BEFORE_TEST_METHOD)
    void update_notFound() throws Exception {
        String requestJson = """
                {
                  "name": "Mouse Gamer",
                  "description": "RGB",
                  "price": 199.90,
                  "imgUrl": "https://example.com/mouse.jpg",
                  "categories": [ { "id": 2, "name": "Acessórios" } ]
                }
                """;

        mockMvc.perform(put("/products/{id}", 9999L)
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /products -> 400 Bad Request quando validação falha")
    @Sql(scripts = {"/sql/products/clean.sql"}, executionPhase = BEFORE_TEST_METHOD)
    void insert_badRequest_validation() throws Exception {
        String invalidJson = """
            {
              "name": "",
              "description": "x",
              "price": -10.00,
              "imgUrl": "http://insecure.example.com/img.jpg",
              "categories": []
            }
            """;

        mockMvc.perform(post("/products")
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /products -> 415 Unsupported Media Type quando Content-Type inválido")
    @Sql(scripts = {"/sql/products/clean.sql"}, executionPhase = BEFORE_TEST_METHOD)
    void insert_unsupportedMediaType() throws Exception {
        String body = """
                {"name":"X","description":"Y","price":100.00,"imgUrl":"https://ex.com/i.jpg","categories":[{"id":1,"name":"Informática"}]}
                """;

        mockMvc.perform(post("/products")
                        .with(csrf())
                        .contentType(TEXT_PLAIN)
                        .accept(APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /products -> 406 Not Acceptable quando Accept=application/xml (payload válido)")
    @Sql(scripts = {"/sql/products/clean.sql", "/sql/products/seed-categories-basic.sql"},
            executionPhase = BEFORE_TEST_METHOD)
    void insert_notAcceptable_whenXmlRequested() throws Exception {
        String body = """
        {
          "name": "Notebook Pro",
          "description": "Ultrabook leve",
          "price": 8999.90,
          "imgUrl": "https://ex.com/i.jpg",
          "categories": [ { "id": 1, "name": "Informática" } ]
        }
        """;

        mockMvc.perform(post("/products")
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_XML)
                        .content(body))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /products -> 400 Bad Request quando JSON é malformado")
    @Sql(scripts = {"/sql/products/clean.sql"}, executionPhase = BEFORE_TEST_METHOD)
    void insert_badRequest_malformedJson() throws Exception {
        String malformed = "{ \"name\": \"X\", ";

        mockMvc.perform(post("/products")
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .content(malformed))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /products sem autenticação -> 401 Unauthorized")
    @Sql(scripts = {"/sql/products/clean.sql", "/sql/products/seed-categories-basic.sql"}, executionPhase = BEFORE_TEST_METHOD)
    void insert_unauthorized_noAuth() throws Exception {
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
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    @DisplayName("POST /products com papel CLIENT -> 403 Forbidden")
    @Sql(scripts = {"/sql/products/clean.sql", "/sql/products/seed-categories-basic.sql"}, executionPhase = BEFORE_TEST_METHOD)
    void insert_forbidden_clientRole() throws Exception {
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
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isForbidden());
    }
}
