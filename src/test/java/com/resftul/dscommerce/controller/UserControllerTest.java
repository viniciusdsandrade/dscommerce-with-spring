package com.resftul.dscommerce.controller;

import com.resftul.dscommerce.dto.RoleDTO;
import com.resftul.dscommerce.dto.user.UserDTO;
import com.resftul.dscommerce.dto.user.UserInsertDTO;
import com.resftul.dscommerce.service.UserService;
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

import java.time.LocalDate;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.endsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("h2")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @TestConfiguration
    static class TestBeans {
        @Bean
        PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }

    private static UserDTO userDto(long id, String name, String email) {
        return new UserDTO(
                id,
                name,
                email,
                "+5519999999999",
                LocalDate.of(2000, 1, 1),
                new RoleDTO(1L, "ROLE_CLIENT")
        );
    }

    @Test
    @DisplayName("GET /users -> 200 com página vazia (envelope: $.content + $.page.*)")
    void getAllUsers_empty() throws Exception {
        Page<UserDTO> empty = new PageImpl<>(emptyList(), PageRequest.of(0, 10), 0);
        when(userService.findAllPaged(any())).thenReturn(empty);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.page.totalElements").value(0))
                .andExpect(jsonPath("$.page.size").value(10))
                .andExpect(jsonPath("$.page.number").value(0));
    }

    @Test
    @DisplayName("GET /users -> 200 com conteúdo e metadados em $.page")
    void getAllUsers_withContent() throws Exception {
        var user1 = userDto(10L, "Ana", "ana@example.com");
        var user2 = userDto(11L, "Bruno", "bruno@example.com");
        Page<UserDTO> page = new PageImpl<>(List.of(user1, user2), PageRequest.of(0, 2), 2);

        when(userService.findAllPaged(any())).thenReturn(page);

        mockMvc.perform(get("/users").param("page", "0").param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].id").value(10))
                .andExpect(jsonPath("$.content[0].name").value("Ana"))
                .andExpect(jsonPath("$.content[0].email").value("ana@example.com"))
                .andExpect(jsonPath("$.content[1].id").value(11))
                .andExpect(jsonPath("$.content[1].name").value("Bruno"))
                .andExpect(jsonPath("$.content[1].email").value("bruno@example.com"))
                .andExpect(jsonPath("$.page.totalElements").value(2))
                .andExpect(jsonPath("$.page.size").value(2))
                .andExpect(jsonPath("$.page.number").value(0));
    }

    @Test
    @DisplayName("GET /users/{id} -> 200 e corpo com id, name, email")
    void findById_ok() throws Exception {
        when(userService.findById(5L)).thenReturn(userDto(5L, "Carol", "carol@example.com"));

        mockMvc.perform(get("/users/{id}", 5L))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.name").value("Carol"))
                .andExpect(jsonPath("$.email").value("carol@example.com"));
    }

    @Test
    @WithMockUser(username = "me@example.com", roles = {"CLIENT"})
    @DisplayName("GET /users/me -> 200 quando autenticado")
    void getMe_ok_authenticated() throws Exception {
        when(userService.getMe()).thenReturn(userDto(1L, "Me", "me@example.com"));

        mockMvc.perform(get("/users/me"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("me@example.com"));
    }

    @Test
    @DisplayName("POST /users -> 201 Created, Location e corpo com id e name (payload válido)")
    void createUser_created() throws Exception {
        when(userService.insert(any(UserInsertDTO.class)))
                .thenReturn(userDto(100L, "Novo Usuário", "novo@example.com"));

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
                .andExpect(header().string("Location", endsWith("/users/100")))
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.name").value("Novo Usuário"))
                .andExpect(jsonPath("$.email").value("novo@example.com"));
    }

    @Test
    @DisplayName("POST /users -> 400 quando validação falha")
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
}

