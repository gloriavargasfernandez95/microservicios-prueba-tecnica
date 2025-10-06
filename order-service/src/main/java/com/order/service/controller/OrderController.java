package com.order.service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.order.service.dto.OrderRequest;
import com.order.service.entity.OrderEntity;
import com.order.service.service.IOrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
	
	private final IOrderService orderService;
	
	@Operation(summary = "Crear nueva orden", description = "Crea una orden")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "orden creada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
	 @PostMapping("/create")
	public ResponseEntity<OrderEntity> createProduct(@Valid @RequestBody OrderRequest order) {
	    OrderEntity orderResponse = orderService.createOrder(order);
	    return new ResponseEntity<>(orderResponse, HttpStatus.CREATED);
	}
	
	@Operation(summary = "Buscar orden por id", description = "Obtiene orden especifica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "orden especifica obtenida")
    })
    @GetMapping("find/{orderId}")
    public ResponseEntity<OrderEntity> getOrdenById(@PathVariable Long orderId) {
		OrderEntity getOrder = orderService.getOrderById(orderId);
        return ResponseEntity.ok(getOrder);
    }
	
	@Operation(summary = "Listar todas las ordenes", description = "Obtiene una lista de todas las ordenes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de productos obtenida")
    })
    @GetMapping("findAll")
    public ResponseEntity<List<OrderEntity>> getAllProducts() {
		List<OrderEntity> allOrders = orderService.getAllOrders();
        return ResponseEntity.ok(allOrders);
    }
}
