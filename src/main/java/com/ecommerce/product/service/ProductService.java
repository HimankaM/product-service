package com.ecommerce.product.service;

import com.ecommerce.product.model.Product;
import com.ecommerce.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> getAllProducts() {
        log.info("Fetching all products");
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        log.info("Fetching product with id: {}", id);
        return productRepository.findById(id);
    }

    public List<Product> getAvailableProducts() {
        log.info("Fetching available products (stock > 0)");
        return productRepository.findByStockQuantityGreaterThan(0);
    }

    @Transactional
    public Product createProduct(Product product) {
        log.info("Creating new product: {}", product.getName());
        return productRepository.save(product);
    }

    @Transactional
    public Optional<Product> updateProduct(Long id, Product updatedProduct) {
        return productRepository.findById(id).map(existing -> {
            existing.setName(updatedProduct.getName());
            existing.setDescription(updatedProduct.getDescription());
            existing.setPrice(updatedProduct.getPrice());
            existing.setStockQuantity(updatedProduct.getStockQuantity());
            log.info("Updating product with id: {}", id);
            return productRepository.save(existing);
        });
    }

    @Transactional
    public boolean deleteProduct(Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            log.info("Deleted product with id: {}", id);
            return true;
        }
        return false;
    }

    /**
     * Called by Order Service to check stock and reserve items.
     * This is the inter-service communication entry point.
     */
    @Transactional
    public boolean reserveStock(Long productId, int quantity) {
        return productRepository.findById(productId).map(product -> {
            if (product.getStockQuantity() >= quantity) {
                product.setStockQuantity(product.getStockQuantity() - quantity);
                productRepository.save(product);
                log.info("Reserved {} units of product id: {}", quantity, productId);
                return true;
            }
            log.warn("Insufficient stock for product id: {}. Required: {}, Available: {}",
                    productId, quantity, product.getStockQuantity());
            return false;
        }).orElse(false);
    }
}
