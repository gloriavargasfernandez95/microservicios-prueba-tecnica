package com.order.service.test.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.order.service.controller.OrderController;
import com.order.service.dto.OrderItemRequest;
import com.order.service.dto.OrderRequest;
import com.order.service.entity.OrderEntity;
import com.order.service.entity.OrderItemEntity;
import com.order.service.exception.GlobalExceptionHandler;
import com.order.service.service.IOrderService;

class OrderControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IOrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(orderController)
                                 .setControllerAdvice(new GlobalExceptionHandler()) // ✅
                                 .build();
    }

    @Test
    void testCreateProduct_success() throws Exception {
        OrderItemRequest item = new OrderItemRequest();
        item.setProductId(1L);
        item.setQuantity(2);

        OrderRequest request = new OrderRequest();
        request.setCustomerName("Juan");
        request.setCustomerEmail("juan@email.com");
        request.setItems(List.of(item));

        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setId(1L);
        orderEntity.setCustomerName("Juan");
        orderEntity.setCustomerEmail("juan@email.com");
        orderEntity.setOrderDate(LocalDateTime.now());
        orderEntity.setStatus("CONFIRMED");
        orderEntity.setTotalAmount(BigDecimal.valueOf(100));
        orderEntity.setProducts(List.of(new OrderItemEntity(1L, 1L, 2, BigDecimal.valueOf(50), orderEntity)));

        when(orderService.createOrder(any(OrderRequest.class))).thenReturn(orderEntity);

        mockMvc.perform(post("/api/orders/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.customerName").value("Juan"))
                .andExpect(jsonPath("$.totalAmount").value(100));

        verify(orderService, times(1)).createOrder(any(OrderRequest.class));
    }
    
    @Test
    void testGetOrdenById_success() throws Exception {
      
        OrderEntity order = new OrderEntity();
        order.setId(1L);
        order.setCustomerName("Juan");
        order.setCustomerEmail("juan@email.com");
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("CONFIRMED");
        order.setTotalAmount(BigDecimal.valueOf(100));

        when(orderService.getOrderById(1L)).thenReturn(order);

        mockMvc.perform(get("/api/orders/find/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.customerName").value("Juan"))
                .andExpect(jsonPath("$.totalAmount").value(100));

        verify(orderService, times(1)).getOrderById(1L);
    }

    @Test
    void testGetOrdenById_notFound() throws Exception {
        when(orderService.getOrderById(1L))
            .thenThrow(new RuntimeException("Orden no encontrado con ID:1"));

        mockMvc.perform(get("/api/orders/find/1"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Orden no encontrado con ID:1"));

        verify(orderService, times(1)).getOrderById(1L);
    }


    

    @Test
    void testGetAllProducts_success() throws Exception {
        OrderEntity order1 = new OrderEntity();
        order1.setId(1L);
        OrderEntity order2 = new OrderEntity();
        order2.setId(2L);

        when(orderService.getAllOrders()).thenReturn(List.of(order1, order2));

        mockMvc.perform(get("/api/orders/findAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(orderService, times(1)).getAllOrders();
    }
}
