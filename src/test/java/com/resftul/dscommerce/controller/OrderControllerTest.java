package com.resftul.dscommerce.controller;

import com.resftul.dscommerce.dto.order.OrderDTO;
import com.resftul.dscommerce.entity.OrderStatus;
import com.resftul.dscommerce.exception.ResourceNotFoundException;
import com.resftul.dscommerce.service.OrderService;
import com.resftul.dscommerce.util.TestSecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static com.resftul.dscommerce.entity.OrderStatus.WAITING_PAYMENT;
import static org.hamcrest.Matchers.endsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = OrderController.class)
@AutoConfigureMockMvc
@ActiveProfiles("h2")
@Import({ TestSecurityConfig.class, OrderControllerTest.TestMethodSecurityConfig.class })
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

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

    private static OrderDTO orderDto(Long id, OrderStatus orderStatus) {
        return new OrderDTO(
                id,
                orderStatus,
                Instant.parse("2025-01-01T00:00:00Z")
        );
    }

    @Test
    @WithMockUser(roles = {"CLIENT"})
    @DisplayName("GET /orders/{id} -> 200 e corpo com id e status (CLIENT)")
    void findById_ok_client() throws Exception {
        when(orderService.findById(1L))
                .thenReturn(orderDto(1L, WAITING_PAYMENT));

        mockMvc.perform(get("/orders/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("WAITING_PAYMENT"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("GET /orders/{id} -> 200 e corpo com id e status (ADMIN)")
    void findById_ok_admin() throws Exception {
        when(orderService.findById(1L))
                .thenReturn(orderDto(1L, WAITING_PAYMENT));

        mockMvc.perform(get("/orders/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("WAITING_PAYMENT"));
    }

    @Test
    @WithMockUser(roles = {"CLIENT"})
    @DisplayName("GET /orders/{id} -> 404 quando não encontrado")
    void findById_notFound() throws Exception {
        when(orderService.findById(999L))
                .thenThrow(new ResourceNotFoundException("Order 999")); // [INFERENCIA] mensagem exata

        mockMvc.perform(get("/orders/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$[0].message").value("Order 999")); // [INFERENCIA] shape do erro
    }

    @Test
    @DisplayName("GET /orders/{id} -> 403 quando não autenticado")
    void findById_forbidden_whenUnauthenticated() throws Exception {
        mockMvc.perform(get("/orders/{id}", 1L))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("GET /orders -> 200 e lista JSON (somente ADMIN)")
    void listAll_ok_admin() throws Exception {
        var orderDto1 = orderDto(10L, WAITING_PAYMENT);
        var orderDto2 = orderDto(11L, OrderStatus.PAID);

        when(orderService.listAll()).thenReturn(List.of(orderDto1, orderDto2));

        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].status").value("WAITING_PAYMENT"))
                .andExpect(jsonPath("$[1].id").value(11))
                .andExpect(jsonPath("$[1].status").value("PAID"));
    }

    @Test
    @WithMockUser(roles = {"CLIENT"})
    @DisplayName("GET /orders -> 403 quando CLIENT tenta listar todos")
    void listAll_forbidden_client() throws Exception {
        mockMvc.perform(get("/orders"))
                .andExpect(status().isForbidden());

        verify(orderService, never()).listAll();
    }

    @Test
    @DisplayName("GET /orders -> 403 quando não autenticado")
    void listAll_forbidden_unauthenticated() throws Exception {
        mockMvc.perform(get("/orders"))
                .andExpect(status().isForbidden());

        verify(orderService, never()).listAll();
    }

    @Test
    @WithMockUser(roles = {"CLIENT"})
    @DisplayName("POST /orders -> 201 Created, Location e corpo com id e status (payload válido; CLIENT)")
    void insert_created() throws Exception {
        var saved = orderDto(999L, WAITING_PAYMENT);
        when(orderService.insert(any(OrderDTO.class))).thenReturn(saved);

        String requestJson = """
        {
          "items": [
            { "productId": 1, "quantity": 2 },
            { "productId": 2, "quantity": 3 }
          ]
        }
        """;

        mockMvc.perform(post("/orders")
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith("/orders/999")))
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(999))
                .andExpect(jsonPath("$.status").value("WAITING_PAYMENT"));
    }

    @Test
    @WithMockUser(roles = {"CLIENT"})
    @DisplayName("POST /orders -> 400 Bad Request quando validação falha (items vazio)")
    void insert_badRequest_validation() throws Exception {
        String invalidJson = """
        {
          "items": []
        }
        """;

        mockMvc.perform(post("/orders")
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$[0].errorCode").value("METHOD_ARGUMENT_NOT_VALID_ERROR")); // [INFERENCIA]

        verify(orderService, never()).insert(any(OrderDTO.class));
    }

    @Test
    @WithMockUser(roles = {"CLIENT"})
    @DisplayName("POST /orders -> 415 quando Content-Type não é application/json")
    void insert_unsupportedMediaType() throws Exception {
        mockMvc.perform(post("/orders")
                        .with(csrf())
                        .contentType("text/plain")
                        .content("not json"))
                .andExpect(status().isUnsupportedMediaType());

        verify(orderService, never()).insert(any(OrderDTO.class));
    }

    @Test
    @WithMockUser(roles = {"CLIENT"})
    @DisplayName("POST /orders -> 406 quando Accept não é suportado")
    void insert_notAcceptable() throws Exception {
        var saved = orderDto(999L, WAITING_PAYMENT);
        when(orderService.insert(any(OrderDTO.class))).thenReturn(saved);

        String requestJson = """
    {
      "items": [
        { "productId": 1, "quantity": 2 }
      ]
    }
    """;

        mockMvc.perform(post("/orders")
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .header("Accept", "application/xml")
                        .content(requestJson))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    @DisplayName("POST /orders -> 403 quando não autenticado")
    void insert_forbidden_whenUnauthenticated() throws Exception {
        String requestJson = """
        {
          "items": [
            { "productId": 1, "quantity": 2 }
          ]
        }
        """;

        mockMvc.perform(post("/orders")
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isForbidden());

        verify(orderService, never()).insert(any(OrderDTO.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("POST /orders -> 403 quando autenticado sem ROLE_CLIENT")
    void insert_forbidden_whenNotClient() throws Exception {
        String requestJson = """
        {
          "items": [
            { "productId": 1, "quantity": 2 }
          ]
        }
        """;

        mockMvc.perform(post("/orders")
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isForbidden());

        verify(orderService, never()).insert(any(OrderDTO.class));
    }
}
