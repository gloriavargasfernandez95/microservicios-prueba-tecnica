package com.order.service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StockAdjustmentItem {
	
	@NotNull(message = "El productId es obligatorio")
	private Long productId;
	
	@NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser un número entero positivo")
	private Integer quantity;
	
}
