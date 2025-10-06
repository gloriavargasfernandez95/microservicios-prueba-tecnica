package com.order.service.util;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.order.service.dto.OrderItemRequest;

public class Util {

	public static Map<Long, Integer> convertToMap(List<OrderItemRequest> items) {
	    return items.stream()
	            .collect(Collectors.toMap(OrderItemRequest::getProductId, OrderItemRequest::getQuantity));
	}
}
