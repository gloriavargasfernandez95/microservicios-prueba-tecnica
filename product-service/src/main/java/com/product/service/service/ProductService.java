package com.product.service.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.product.service.entity.ProductEntity;
import com.product.service.repository.IProductRepository;

import jakarta.transaction.Transactional;

@Service
public class ProductService {
	
	private final IProductRepository productRepository;
	
	public ProductService(IProductRepository productRepository) {
        this.productRepository = productRepository;
    }
	
	
	public ProductEntity createProduct (ProductEntity product) {
		return productRepository.save(product);
	}
	
	public ProductEntity updateProduct(ProductEntity updatedProduct) {
		
		ProductEntity existingProduct = productRepository.findById(updatedProduct.getId())
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        existingProduct.setName(updatedProduct.getName());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setStock(updatedProduct.getStock());
        existingProduct.setActive(updatedProduct.getActive());
        
        return productRepository.save(existingProduct);
    }
	
    public ProductEntity deleteProduct(Long deleteProduct) {
		
		ProductEntity existingProduct = productRepository.findById(deleteProduct)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        existingProduct.setActive(false);
        
        return productRepository.save(existingProduct);
    }
	
	public ProductEntity getProductById (Long id) {
		
		return productRepository.findById(id).orElseThrow(() -> new RuntimeException("Producto no encontrado con ID:" + id));
	}
	
	public List<ProductEntity> getProductByName (String name) {
		
		return productRepository.findByName(name);
	}
	
	public List<ProductEntity> getAllProduct () {
		
		return productRepository.findAll();
	}
	
	public Map<String, Integer> checkStockAvailability(Map<Long, Integer> productsWithQuantities) {
		
	    Map<String, Integer> insufficientStock = new HashMap<>();

	    for (Map.Entry<Long, Integer> entry : productsWithQuantities.entrySet()) {
	        Long productId = entry.getKey();
	        int requiredQuantity = entry.getValue();

	        ProductEntity product = getProductById(productId);
	        
	        if (product.getActive()) {
	            if (product.getStock() < requiredQuantity) {
	                insufficientStock.put(product.getName(), requiredQuantity - product.getStock());
	            }
	        }
	    }

	    return insufficientStock; 
	}
	
	@Transactional
    public void updateStock(Long productId, int quantityChange) {
		
		ProductEntity product = getProductById(productId);
		
		if (product.getActive()) {
			
			int newStock = product.getStock() + quantityChange;
	        if (newStock < 0) {
	            throw new RuntimeException("Stock insuficiente para el producto: " + product.getName());
	        }
	        product.setStock(newStock);
	        productRepository.save(product);
		}
        
    }

    public void reduceStockForOrder(Long productId, int quantity) {
        updateStock(productId, -quantity);
    }

    public void increaseStock(Long productId, int quantity) {
        updateStock(productId, quantity);
    }
	
	
	
}
