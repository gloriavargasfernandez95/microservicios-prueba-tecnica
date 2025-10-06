package com.order.service.client;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.order.service.dto.ProductStockResponse;
import com.order.service.dto.StockAdjustmentRequest;

@Component
public class ProductClient {

	private final RestTemplate restTemplate;
	private final String productServiceUrl;

	public ProductClient(RestTemplate restTemplate, @Value("${product.service.url}") String productServiceUrl) {
		this.restTemplate = restTemplate;
		this.productServiceUrl = productServiceUrl;
	}
	
	
    public Map<String, Integer> checkAvailability(Map<Long, Integer> productsWithQuantities) {
        String url = productServiceUrl + "check-availability";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<Long, Integer>> request = new HttpEntity<>(productsWithQuantities, headers);

        ResponseEntity<Map<String, Integer>> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<>() {}
        );

        return response.getBody();
    }


	public ProductStockResponse findProduct(Long productId) {
		String url = productServiceUrl + "find/" +productId;
		return restTemplate.getForObject(url, ProductStockResponse.class);
	}


	public void reduceStockBatch(StockAdjustmentRequest request) {
	    String url = productServiceUrl + "reduce-stock-batch";

	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    HttpEntity<StockAdjustmentRequest> entity = new HttpEntity<>(request, headers);

	    restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
	}

}
