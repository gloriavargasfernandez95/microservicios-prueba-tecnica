package com.product.service.test.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.product.service.controller.ProductController;
import com.product.service.dto.StockAdjustmentItem;
import com.product.service.dto.StockAdjustmentRequest;
import com.product.service.entity.ProductEntity;
import com.product.service.service.ProductService;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    private ProductEntity product;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        product = new ProductEntity();
        product.setId(1L);
        product.setName("Producto Test");
        product.setPrice(new BigDecimal(150.23));
        product.setStock(10);
        product.setActive(true);
    }

    @Test
    void testCreateProduct() throws Exception {
        when(productService.createProduct(any(ProductEntity.class))).thenReturn(product);

        mockMvc.perform(post("/api/products/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Producto Test"));
    }

    @Test
    void testGetProductById() throws Exception {
        when(productService.getProductById(1L)).thenReturn(product);

        mockMvc.perform(get("/api/products/find/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }
    
    @Test
    void testGetAllProducts() throws Exception {
        List<ProductEntity> products = Arrays.asList(product);
        when(productService.getAllProduct()).thenReturn(products);

        mockMvc.perform(get("/api/products/findAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Producto Test"));
    }

    @Test
    void testCheckAvailability() throws Exception {
        Map<String, Integer> responseMap = new HashMap<>();
        responseMap.put("Producto Test", 5);
        Map<Long, Integer> requestMap = new HashMap<>();
        requestMap.put(1L, 15);

        when(productService.checkStockAvailability(requestMap)).thenReturn(responseMap);

        mockMvc.perform(post("/api/products/check-availability")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestMap)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.['Producto Test']").value(5));
    }

    @Test
    void testReduceStockForOrderBatch() throws Exception {
        StockAdjustmentItem item = new StockAdjustmentItem();
        item.setProductId(1L);
        item.setQuantity(5);
        StockAdjustmentRequest request = new StockAdjustmentRequest();
        request.setProducts(List.of(item));

        doNothing().when(productService).reduceStockForOrder(anyLong(), anyInt());

        mockMvc.perform(post("/api/products/reduce-stock-batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Stock reducido correctamente para todos los productos"));
    }
    
    @Test
    void testAddStockForOrderBatch() throws Exception {
        StockAdjustmentItem item = new StockAdjustmentItem();
        item.setProductId(1L);
        item.setQuantity(4);
        StockAdjustmentRequest request = new StockAdjustmentRequest();
        request.setProducts(List.of(item));

        doNothing().when(productService).increaseStock(anyLong(), anyInt());

        mockMvc.perform(post("/api/products/add-stock-batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Stock agregado correctamente para todos los productos"));
    }
}
