package com.ecommerce.product;

import com.ecommerce.product.model.Product;
import com.ecommerce.product.repository.ProductRepository;
import com.ecommerce.product.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ProductServiceApplicationTests {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
    }

    @Test
    void contextLoads() {
    }

    @Test
    void shouldCreateProduct() {
        Product product = Product.builder()
                .name("Test Product")
                .description("Test Description")
                .price(new BigDecimal("99.99"))
                .stockQuantity(10)
                .build();

        Product saved = productService.createProduct(product);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Test Product");
        assertThat(saved.getPrice()).isEqualTo(new BigDecimal("99.99"));
    }

    @Test
    void shouldReserveStockSuccessfully() {
        Product product = Product.builder()
                .name("Stock Product")
                .description("Product with stock")
                .price(new BigDecimal("50.00"))
                .stockQuantity(20)
                .build();
        Product saved = productService.createProduct(product);

        boolean result = productService.reserveStock(saved.getId(), 5);

        assertThat(result).isTrue();
        Optional<Product> updated = productService.getProductById(saved.getId());
        assertThat(updated).isPresent();
        assertThat(updated.get().getStockQuantity()).isEqualTo(15);
    }

    @Test
    void shouldFailReserveWhenInsufficientStock() {
        Product product = Product.builder()
                .name("Low Stock Product")
                .description("Product with low stock")
                .price(new BigDecimal("50.00"))
                .stockQuantity(2)
                .build();
        Product saved = productService.createProduct(product);

        boolean result = productService.reserveStock(saved.getId(), 10);

        assertThat(result).isFalse();
    }
}
