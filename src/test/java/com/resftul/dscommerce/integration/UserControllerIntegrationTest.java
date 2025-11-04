package com.resftul.dscommerce.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.text.MatchesPattern.matchesPattern;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("h2")
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /users -> 200 com página vazia (envelope: $.content + $.page)")
    @Sql(scripts = {"/sql/users/clean.sql"}, executionPhase = BEFORE_TEST_METHOD)
    void getAllUsers_empty() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.page.totalElements").value(0))
                .andExpect(jsonPath("$.page.size").exists())
                .andExpect(jsonPath("$.page.number").value(0));
    }

    @Test
    @DisplayName("GET /users?size=2 -> 200 e página com itens (envelope: $.content + $.page)")
    @Sql(scripts = {"/sql/users/clean.sql", "/sql/users/seed-two-users.sql"}, executionPhase = BEFORE_TEST_METHOD)
    void getAllUsers_withContent() throws Exception {
        mockMvc.perform(get("/users").param("page", "0").param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].name").value("Ana"))
                .andExpect(jsonPath("$.content[0].email").value("ana@example.com"))
                .andExpect(jsonPath("$.content[1].name").value("Bruno"))
                .andExpect(jsonPath("$.content[1].email").value("bruno@example.com"))
                .andExpect(jsonPath("$.page.totalElements").value(2))
                .andExpect(jsonPath("$.page.size").value(2))
                .andExpect(jsonPath("$.page.number").value(0));
    }

    @Test
    @DisplayName("GET /users/{id} -> 200 e corpo com id, name, email")
    @Sql(scripts = {"/sql/users/clean.sql", "/sql/users/seed-user-5.sql"}, executionPhase = BEFORE_TEST_METHOD)
    void findById_ok() throws Exception {
        mockMvc.perform(get("/users/{id}", 5L))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.name").value("Carol"))
                .andExpect(jsonPath("$.email").value("carol@example.com"));
    }

    @Test
    @WithMockUser(username = "me@example.com", roles = {"CLIENT"})
    @DisplayName("GET /users/me -> 200 quando autenticado (Security ativa via filtros)")
    @Sql(scripts = {"/sql/users/clean.sql", "/sql/users/seed-me.sql"}, executionPhase = BEFORE_TEST_METHOD)
    void getMe_ok_authenticated() throws Exception {
        mockMvc.perform(get("/users/me"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value("me@example.com"));
    }

    @Test
    @DisplayName("POST /users -> 201 Created, Location e corpo com id e name (payload válido)")
    @Sql(scripts = {"/sql/users/clean.sql"}, executionPhase = BEFORE_TEST_METHOD)
    void createUser_created() throws Exception {
        String requestJson = """
                {
                  "name": "Novo Usuário",
                  "email": "novo@example.com",
                  "phone": "+5519999999999",
                  "password": "Str0ng_P@ss!",
                  "birthDate": "2000-01-01"
                }
                """;

        mockMvc.perform(post("/users")
                        .contentType(APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", matchesPattern(".*/users/\\d+$")))
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Novo Usuário"))
                .andExpect(jsonPath("$.email").value("novo@example.com"));
    }

    @Test
    @DisplayName("POST /users -> 400 quando validação falha")
    @Sql(scripts = {"/sql/users/clean.sql"}, executionPhase = BEFORE_TEST_METHOD)
    void createUser_badRequest_validation() throws Exception {
        String invalidJson = """
                {
                  "name": "",
                  "email": "not-an-email",
                  "phone": "123",
                  "password": "123",
                  "birthDate": "2999-01-01"
                }
                """;

        mockMvc.perform(post("/users")
                        .contentType(APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /users/me -> 401 quando não autenticado")
    void getMe_unauthorized() throws Exception {
        mockMvc.perform(get("/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /users/{id} inexistente -> 404 + contrato de erro")
    @Sql(scripts = {"/sql/users/clean.sql"}, executionPhase = BEFORE_TEST_METHOD)
    void findById_notFound() throws Exception {
        mockMvc.perform(get("/users/{id}", 9999))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$[*].errorCode", hasItem("RESOURCE_NOT_FOUND")));
    }

    @Test
    @DisplayName("POST /users -> 415 Unsupported Media Type quando Content-Type inválido")
    @Sql(scripts = {"/sql/users/clean.sql"}, executionPhase = BEFORE_TEST_METHOD)
    void createUser_unsupportedMediaType() throws Exception {
        String body = """
                {"name":"X","email":"x@example.com","phone":"+5519999999999","password":"Str0ng_P@ss!","birthDate":"2000-01-01"}
                """;
        mockMvc.perform(post("/users")
                        .contentType(TEXT_PLAIN)
                        .content(body))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    @DisplayName("POST /users -> 406 Not Acceptable quando Accept não é suportado")
    @Sql(scripts = {"/sql/users/clean.sql"}, executionPhase = BEFORE_TEST_METHOD)
    void createUser_notAcceptable_whenXmlRequested() throws Exception {
        String body = """
                {"name":"X","email":"x@example.com","phone":"+5519999999999","password":"Str0ng_P@ss!","birthDate":"2000-01-01"}
                """;
        mockMvc.perform(post("/users")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_XML) // força 406
                        .content(body))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    @DisplayName("POST /users -> 400 Bad Request quando JSON é malformado")
    @Sql(scripts = {"/sql/users/clean.sql"}, executionPhase = BEFORE_TEST_METHOD)
    void createUser_badRequest_malformedJson() throws Exception {
        String malformed = "{ \"name\": \"X\", ";
        mockMvc.perform(post("/users")
                        .contentType(APPLICATION_JSON)
                        .content(malformed))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /users persiste e GET /users/{id} retorna os mesmos dados essenciais (round-trip)")
    @Sql(scripts = {"/sql/users/clean.sql"}, executionPhase = BEFORE_TEST_METHOD)
    void createThenGetById_roundTrip() throws Exception {
        String body = """
                {
                  "name": "Round Trip",
                  "email": "round@example.com",
                  "phone": "+5511999999999",
                  "password": "Str0ng_P@ss!",
                  "birthDate": "1990-01-10"
                }
                """;

        var mvcResult = mockMvc.perform(post("/users")
                        .contentType(APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", matchesPattern(".*/users/\\d+$")))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Round Trip"))
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        String id = response.replaceAll(".*\"id\"\\s*:\\s*(\\d+).*", "$1");

        mockMvc.perform(get("/users/{id}", Long.parseLong(id)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(Long.parseLong(id)))
                .andExpect(jsonPath("$.name").value("Round Trip"))
                .andExpect(jsonPath("$.email").value("round@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    @DisplayName("GET /users ordenado por nome desc -> 'Bruno' antes de 'Ana'")
    @Sql(scripts = {"/sql/users/clean.sql", "/sql/users/seed-two-users.sql"}, executionPhase = BEFORE_TEST_METHOD)
    void getAllUsers_sortedByName_desc() throws Exception {
        mockMvc.perform(get("/users")
                        .param("sort", "name,desc")
                        .param("page", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].name").value("Bruno"))
                .andExpect(jsonPath("$.content[1].name").value("Ana"));
    }

    @Test
    @DisplayName("GET /users/{id} com id não numérico -> 400 (type mismatch)")
    void findById_typeMismatch_badRequest() throws Exception {
        mockMvc.perform(get("/users/{id}", "abc")) // path variable inválido
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /users -> 409 quando email duplicado")
    @Sql(scripts = {"/sql/users/clean.sql", "/sql/users/seed-one-user.sql"}, executionPhase = BEFORE_TEST_METHOD)
    void createUser_conflict_duplicateEmail() throws Exception {
        String body = """
                {
                  "name": "Outro Usuário",
                  "email": "ana@example.com",
                  "phone": "+5519999999999",
                  "password": "Str0ng_P@ss!",
                  "birthDate": "2000-01-01"
                }
                """;

        mockMvc.perform(post("/users")
                        .contentType(APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict());
    }

    @ParameterizedTest
    @MethodSource("invalidPayloads")
    void createUser_badRequest_fieldErrors(String json, String expectedField) throws Exception {
        mockMvc.perform(post("/users").contentType(APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[*].field", hasItem(expectedField)));
    }

    static Stream<Arguments> invalidPayloads() {
        return Stream.of(
                // name vazio
                arguments("""
                        {
                          "name": "",
                          "email": "novo@example.com",
                          "phone": "+5519999999999",
                          "password": "Str0ng_P@ss!",
                          "birthDate": "2000-01-01"
                        }
                        """, "name"),
                // name curto
                arguments("""
                        {
                          "name": "A",
                          "email": "novo@example.com",
                          "phone": "+5519999999999",
                          "password": "Str0ng_P@ss!",
                          "birthDate": "2000-01-01"
                        }
                        """, "name"),
                // email inválido
                arguments("""
                        {
                          "name": "Novo Usuário",
                          "email": "not-an-email",
                          "phone": "+5519999999999",
                          "password": "Str0ng_P@ss!",
                          "birthDate": "2000-01-01"
                        }
                        """, "email"),
                // phone inválido
                arguments("""
                        {
                          "name": "Novo Usuário",
                          "email": "novo@example.com",
                          "phone": "123",
                          "password": "Str0ng_P@ss!",
                          "birthDate": "2000-01-01"
                        }
                        """, "phone"),
                // password fraca
                arguments("""
                        {
                          "name": "Novo Usuário",
                          "email": "novo@example.com",
                          "phone": "+5519999999999",
                          "password": "123",
                          "birthDate": "2000-01-01"
                        }
                        """, "password"),
                // birthDate no futuro
                arguments("""
                        {
                          "name": "Novo Usuário",
                          "email": "novo@example.com",
                          "phone": "+5519999999999",
                          "password": "Str0ng_P@ss!",
                          "birthDate": "2999-01-01"
                        }
                        """, "birthDate")
        );
    }
}
