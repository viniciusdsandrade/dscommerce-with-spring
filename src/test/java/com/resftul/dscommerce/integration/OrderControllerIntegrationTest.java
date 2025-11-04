package com.resftul.dscommerce.integration;

import com.resftul.dscommerce.dto.order.OrderDTO;
import com.resftul.dscommerce.entity.OrderStatus;
import com.resftul.dscommerce.service.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.hamcrest.Matchers.endsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("h2")
class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    private static OrderDTO orderDto(Long id, OrderStatus status) {
        return new OrderDTO(
                id,
                status,
                Instant.parse("2025-01-01T00:00:00Z")
        );
    }

    @Test
    @WithMockUser(roles = {"CLIENT"})
    @DisplayName("GET /orders/{id} -> 200 e corpo JSON com id e status")
    void findById_ok() throws Exception {
        when(orderService.findById(1L)).thenReturn(orderDto(1L, OrderStatus.WAITING_PAYMENT));

        mockMvc.perform(get("/orders/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("WAITING_PAYMENT"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("GET /orders -> 200 e lista JSON (ADMIN)")
    void listAll_ok() throws Exception {
        var orderDto1 = orderDto(10L, OrderStatus.WAITING_PAYMENT);
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
    @WithMockUser(roles = "CLIENT")
    @DisplayName("POST /orders -> 201 Created, Location e corpo com id e status (payload válido)")
    void insert_created() throws Exception {
        var saved = orderDto(999L, OrderStatus.WAITING_PAYMENT);
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
                        .contentType(APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith("/orders/999")))
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(999))
                .andExpect(jsonPath("$.status").value("WAITING_PAYMENT"));
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    @DisplayName("POST /orders -> 400 Bad Request quando validação falha (items vazio)")
    void insert_badRequest_validation() throws Exception {
        String invalidJson = """
        {
          "items": []
        }
        """;

        mockMvc.perform(post("/orders")
                        .contentType(APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }
}
