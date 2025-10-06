package com.order.service.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StockAdjustmentRequest {
	
	@Valid
    @NotEmpty(message = "Debe enviar al menos un producto")
	private List<StockAdjustmentItem> products;

}
