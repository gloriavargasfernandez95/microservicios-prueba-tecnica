package com.order.service.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductStockResponse {
	
	private Long id;
	private String name;
	private BigDecimal price;
	private Integer stock;
	private Boolean active;

}
