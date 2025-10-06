package com.product.service.test.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import com.product.service.entity.ProductEntity;
import com.product.service.repository.IProductRepository;
import com.product.service.service.ProductService;

class ProductServiceTest {

    @Mock
    private IProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private ProductEntity product;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        product = new ProductEntity();
        product.setId(1L);
        product.setName("Producto Test");
        product.setPrice(new BigDecimal(150.45));
        product.setStock(10);
        product.setActive(true);
    }

    @Test
    void testCreateProduct() {
        when(productRepository.save(any(ProductEntity.class))).thenReturn(product);

        ProductEntity created = productService.createProduct(product);
        assertNotNull(created);
        assertEquals("Producto Test", created.getName());
    }

    @Test
    void testGetProductByIdFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductEntity found = productService.getProductById(1L);
        assertEquals(1L, found.getId());
    }

    @Test
    void testGetProductByIdNotFound() {
        when(productRepository.findById(2L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            productService.getProductById(2L);
        });

        assertTrue(exception.getMessage().contains("Producto no encontrado"));
    }

    @Test
    void testCheckStockAvailability() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Map<Long, Integer> map = new HashMap<>();
        map.put(1L, 15);

        Map<String, Integer> result = productService.checkStockAvailability(map);
        assertTrue(result.containsKey("Producto Test"));
        assertEquals(5, result.get("Producto Test"));
    }

    @Test
    void testReduceStockForOrderSuccess() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(ProductEntity.class))).thenReturn(product);

        assertDoesNotThrow(() -> productService.reduceStockForOrder(1L, 5));
        assertEquals(5, product.getStock()); 
    }

    @Test
    void testReduceStockForOrderInsufficient() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            productService.reduceStockForOrder(1L, 20);
        });

        assertTrue(exception.getMessage().contains("Stock insuficiente"));
    }
}
