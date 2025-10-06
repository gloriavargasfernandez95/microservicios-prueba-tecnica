package com.product.service.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.product.service.dto.StockAdjustmentItem;
import com.product.service.dto.StockAdjustmentRequest;
import com.product.service.entity.ProductEntity;
import com.product.service.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/products")
public class ProductController {

	private final ProductService service;
	
	public ProductController (ProductService service) {
		this.service = service;
	}
	
	@Operation(summary = "Crear un nuevo producto", description = "Crea un producto en el catálogo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Producto creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
	 @PostMapping("/create")
	public ResponseEntity<ProductEntity> createProduct(@Valid @RequestBody ProductEntity product) {
	    ProductEntity createdProduct = service.createProduct(product);
	    return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
	}
	
	@Operation(summary = "Obtener producto por ID", description = "Obtiene los detalles de un producto por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto encontrado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @GetMapping("find/{productId}")
    public ResponseEntity<ProductEntity> getProductById(@PathVariable Long productId) {
        ProductEntity product = service.getProductById(productId);
        return ResponseEntity.ok(product);
    }
	
    @Operation(summary = "Listar todos los productos", description = "Obtiene una lista de todos los productos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de productos obtenida")
    })
    @GetMapping("findAll")
    public ResponseEntity<List<ProductEntity>> getAllProducts() {
        List<ProductEntity> products = service.getAllProduct();
        return ResponseEntity.ok(products);
    }
    
    @Operation(summary = "Verificar disponibilidad de productos", description = "Devuelve los productos con stock insuficiente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Disponibilidad verificada")
    })
    @PostMapping("/check-availability")
    public ResponseEntity<Map<String, Integer>> checkAvailability(@RequestBody Map<Long, Integer> productsWithQuantities) {
        Map<String, Integer> insufficientStock = service.checkStockAvailability(productsWithQuantities);
        return ResponseEntity.ok(insufficientStock);
    }
    
    @Operation(
    	    summary = "Reducir stock de varios productos",
    	    description = "Reduce el stock de múltiples productos según las cantidades indicadas."
    	)
    	@ApiResponses(value = {
    	    @ApiResponse(responseCode = "200", description = "Stock reducido correctamente"),
    	    @ApiResponse(responseCode = "400", description = "Stock insuficiente o datos inválidos"),
    	    @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    	})
    	@PostMapping("/reduce-stock-batch")
    	public ResponseEntity<String> reduceStockForOrderBatch(
    	    @Valid @RequestBody StockAdjustmentRequest request
    	) {
    	    for (StockAdjustmentItem item : request.getProducts()) {
    	        service.reduceStockForOrder(item.getProductId(), item.getQuantity());
    	    }
    	    return ResponseEntity.ok("Stock reducido correctamente para todos los productos");
    	}
    
    @Operation(
    	    summary = "Agregar stock de varios productos",
    	    description = "Agrega el stock de múltiples productos según las cantidades indicadas."
    	)
    	@ApiResponses(value = {
    	    @ApiResponse(responseCode = "200", description = "Stock agregado correctamente"),
    	    @ApiResponse(responseCode = "400", description = "datos inválidos"),
    	    @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    	})
    	@PostMapping("/add-stock-batch")
    	public ResponseEntity<String> addStockForOrderBatch(
    	    @Valid @RequestBody StockAdjustmentRequest request
    	) {
    	    for (StockAdjustmentItem item : request.getProducts()) {
    	        service.increaseStock(item.getProductId(), item.getQuantity());
    	    }
    	    return ResponseEntity.ok("Stock agregado correctamente para todos los productos");
    	}
	
}
