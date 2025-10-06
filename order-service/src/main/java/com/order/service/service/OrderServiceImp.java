package com.order.service.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.order.service.client.ProductClient;
import com.order.service.dto.OrderItemRequest;
import com.order.service.dto.OrderRequest;
import com.order.service.dto.ProductStockResponse;
import com.order.service.dto.StockAdjustmentItem;
import com.order.service.dto.StockAdjustmentRequest;
import com.order.service.entity.OrderEntity;
import com.order.service.entity.OrderItemEntity;
import com.order.service.repository.IOrderRepository;
import com.order.service.util.Util;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImp implements IOrderService {
	
	private final IOrderRepository orderRepository;
    private final ProductClient productClient;
	
    @Override
    @Transactional
    public OrderEntity createOrder(OrderRequest request) {

        Map<Long, Integer> productMap = Util.convertToMap(request.getItems());
        Map<String, Integer> insufficientStock = productClient.checkAvailability(productMap);

        if (insufficientStock != null && !insufficientStock.isEmpty()) {
            throw new RuntimeException("Stock insuficiente para productos: " + insufficientStock.keySet());
        }
        
        OrderEntity order = new OrderEntity();
        order.setCustomerName(request.getCustomerName());
        order.setCustomerEmail(request.getCustomerEmail());
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("CONFIRMED");

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItemEntity> products = new ArrayList<>();

        for (OrderItemRequest item : request.getItems()) {
            ProductStockResponse product = productClient.findProduct(item.getProductId());

            if (product == null) {
                throw new RuntimeException("Producto no encontrado: " + item.getProductId());
            }

            OrderItemEntity productData = new OrderItemEntity();
            productData.setProductId(item.getProductId());
            productData.setQuantity(item.getQuantity());
            productData.setUnitPrice(product.getPrice());
            productData.setOrder(order);

            totalAmount = totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            products.add(productData);
        }

        order.setProducts(products);
        order.setTotalAmount(totalAmount);

        
        OrderEntity savedOrder = orderRepository.save(order);

        StockAdjustmentRequest stockRequest = new StockAdjustmentRequest();
        List<StockAdjustmentItem> itemsToReduce = request.getItems().stream().map(i -> {
            StockAdjustmentItem s = new StockAdjustmentItem();
            s.setProductId(i.getProductId());
            s.setQuantity(i.getQuantity());
            return s;
        }).toList();

        stockRequest.setProducts(itemsToReduce);

        try {
            productClient.reduceStockBatch(stockRequest);
        } catch (Exception e) {
            throw new RuntimeException("Error al reducir stock en Product Service: " + e.getMessage(), e);
        }

        return savedOrder;
    }


	@Override
	public OrderEntity getOrderById(Long id) {
		return orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Orden no encontrado con ID:" + id));
	}

	@Override
	public List<OrderEntity> getAllOrders() {
		return orderRepository.findAll();
	}

}
