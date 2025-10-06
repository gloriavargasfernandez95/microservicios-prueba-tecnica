package com.order.service.service;

import java.util.List;

import com.order.service.dto.OrderRequest;
import com.order.service.entity.OrderEntity;

public interface IOrderService {

	OrderEntity createOrder(OrderRequest request);
	OrderEntity getOrderById(Long id);
    List<OrderEntity> getAllOrders();
}
