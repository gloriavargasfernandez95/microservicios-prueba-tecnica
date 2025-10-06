package com.order.service.test.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.order.service.client.ProductClient;
import com.order.service.dto.OrderItemRequest;
import com.order.service.dto.OrderRequest;
import com.order.service.dto.ProductStockResponse;
import com.order.service.dto.StockAdjustmentItem;
import com.order.service.dto.StockAdjustmentRequest;
import com.order.service.entity.OrderEntity;
import com.order.service.repository.IOrderRepository;
import com.order.service.service.OrderServiceImp;

public class OrderServiceImpTest {
	
	 @Mock
	    private IOrderRepository orderRepository;

	    @Mock
	    private ProductClient productClient;

	    @InjectMocks
	    private OrderServiceImp orderService;

	    @BeforeEach
	    void setUp() {
	        MockitoAnnotations.openMocks(this);
	    }

	    @Test
	    void testCreateOrder_success() {
	    	
	        OrderItemRequest item1 = new OrderItemRequest();
	        item1.setProductId(1L);
	        item1.setQuantity(2);

	        OrderItemRequest item2 = new OrderItemRequest();
	        item2.setProductId(2L);
	        item2.setQuantity(1);

	        OrderRequest request = new OrderRequest();
	        request.setCustomerName("Juan");
	        request.setCustomerEmail("juan@email.com");
	        request.setItems(List.of(item1, item2));

	        when(productClient.checkAvailability(any(Map.class))).thenReturn(Map.of());
	        when(productClient.findProduct(1L)).thenReturn(new ProductStockResponse(1L, "Producto 1", BigDecimal.valueOf(100), 10, true));
	        when(productClient.findProduct(2L)).thenReturn(new ProductStockResponse(2L, "Producto 2", BigDecimal.valueOf(50), 5, true));

	        when(orderRepository.save(any(OrderEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

	        OrderEntity result = orderService.createOrder(request);

	        assertNotNull(result);
	        assertEquals("Juan", result.getCustomerName());
	        assertEquals(2, result.getProducts().size());
	        assertEquals(BigDecimal.valueOf(250), result.getTotalAmount());

	        // Verificar que se llamó reduceStockBatch
	        ArgumentCaptor<StockAdjustmentRequest> captor = ArgumentCaptor.forClass(StockAdjustmentRequest.class);
	        verify(productClient).reduceStockBatch(captor.capture());

	        List<StockAdjustmentItem> itemsReduced = captor.getValue().getProducts();
	        assertEquals(2, itemsReduced.size());
	    }

	    @Test
	    void testCreateOrder_insufficientStock() {
	        OrderRequest request = new OrderRequest();
	        request.setCustomerName("Maria");
	        request.setCustomerEmail("maria@email.com");
	        OrderItemRequest item = new OrderItemRequest();
	        item.setProductId(1L);
	        item.setQuantity(5);
	        request.setItems(List.of(item));

	        when(productClient.checkAvailability(any(Map.class))).thenReturn(Map.of("1", 2));

	        RuntimeException exception = assertThrows(RuntimeException.class, () -> orderService.createOrder(request));
	        assertTrue(exception.getMessage().contains("Stock insuficiente"));
	    }
	    
	    @Test
	    void testGetOrderById_success() {
	        OrderEntity order = new OrderEntity();
	        order.setId(1L);
	        order.setCustomerName("Juan");

	        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

	        OrderEntity result = orderService.getOrderById(1L);

	        assertNotNull(result);
	        assertEquals(1L, result.getId());
	        assertEquals("Juan", result.getCustomerName());
	        verify(orderRepository, times(1)).findById(1L);
	    }

	    @Test
	    void testGetOrderById_notFound() {
	        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

	        RuntimeException exception = assertThrows(RuntimeException.class, () -> orderService.getOrderById(1L));
	        assertTrue(exception.getMessage().contains("Orden no encontrado con ID:1"));
	        verify(orderRepository, times(1)).findById(1L);
	    }


	    @Test
	    void testGetAllOrders() {
	        OrderEntity order1 = new OrderEntity();
	        order1.setId(1L);

	        OrderEntity order2 = new OrderEntity();
	        order2.setId(2L);

	        when(orderRepository.findAll()).thenReturn(List.of(order1, order2));

	        List<OrderEntity> result = orderService.getAllOrders();

	        assertEquals(2, result.size());
	        verify(orderRepository).findAll();
	    }

}

