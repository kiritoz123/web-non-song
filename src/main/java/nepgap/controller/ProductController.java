package nepgap.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import nepgap.dto.ApiResponse;
import nepgap.model.Product;
import nepgap.model.ProductProjection;
import nepgap.model.ProductStatus;
import nepgap.repository.ProductRepository;
import nepgap.security.UserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class ProductController {

    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // Public: list all published products (or all if admin)
    @GetMapping("/public/products")
    public ResponseEntity<ApiResponse<List<ProductProjection>>> listPublic(@RequestParam(value = "q", required = false) String q,
                                                                 HttpServletRequest req) {
        List<ProductProjection> list;
        if (q != null && !q.isBlank()) {
            list = productRepository.searchByKeyword(q);
            ApiResponse<List<ProductProjection>> ar = ApiResponse.<List<ProductProjection>>builder()
                    .timestamp(Instant.now())
                    .status(HttpStatus.OK.value())
                    .message("Products fetched")
                    .data(list)
                    .path(req.getRequestURI())
                    .build();
            return ResponseEntity.ok(ar);
        } else {
            list = productRepository.findAllProduct();
            ApiResponse<List<ProductProjection>> ar = ApiResponse.<List<ProductProjection>>builder()
                    .timestamp(Instant.now())
                    .status(HttpStatus.OK.value())
                    .message("Products fetched")
                    .data(list)
                    .path(req.getRequestURI())
                    .build();
            return ResponseEntity.ok(ar);
        }

    }

    @GetMapping("/public/product/coming-soon")
    public ResponseEntity<ApiResponse<List<ProductProjection>>> listPublicComingSoon(HttpServletRequest req) {
        List<ProductProjection> list = productRepository.findAllProductComingSoon();
            ApiResponse<List<ProductProjection>> ar = ApiResponse.<List<ProductProjection>>builder()
                    .timestamp(Instant.now())
                    .status(HttpStatus.OK.value())
                    .message("Products fetched")
                    .data(list)
                    .path(req.getRequestURI())
                    .build();
            return ResponseEntity.ok(ar);
    }

    // Public: get product by id
    @GetMapping("/public/products/{id}")
    public ResponseEntity<ApiResponse<ProductProjection>> getById(@PathVariable Long id, HttpServletRequest req) {
        Optional<ProductProjection> p = productRepository.findByIdAndImage(id);
        if (p.isEmpty()) {
            ApiResponse<ProductProjection> ar = ApiResponse.<ProductProjection>builder()
                    .timestamp(Instant.now())
                    .status(HttpStatus.NOT_FOUND.value())
                    .message("Product not found")
                    .path(req.getRequestURI())
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ar);
        }
        ApiResponse<ProductProjection> ar = ApiResponse.<ProductProjection>builder()
                .timestamp(Instant.now())
                .status(HttpStatus.OK.value())
                .message("Product fetched")
                .data(p.get())
                .path(req.getRequestURI())
                .build();
        return ResponseEntity.ok(ar);
    }

    // Admin: create product
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @PostMapping("/private/admin/products")
    public ResponseEntity<ApiResponse<Product>> create(@Valid @RequestBody Product product,
                                                       @AuthenticationPrincipal UserPrincipal user,
                                                       HttpServletRequest req) {
        if (product.getStatus() == null) product.setStatus(ProductStatus.PUBLISHED);
        Product saved = productRepository.save(product);
        ApiResponse<Product> ar = ApiResponse.<Product>builder()
                .timestamp(Instant.now())
                .status(HttpStatus.CREATED.value())
                .message("Product created")
                .data(saved)
                .path(req.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(ar);
    }

    // Admin: update product
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @PutMapping("/private/admin/products/{id}")
    public ResponseEntity<ApiResponse<Product>> update(@PathVariable Long id,
                                                       @Valid @RequestBody Product payload,
                                                       HttpServletRequest req) {
        Product p = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        p.setName(payload.getName());
        p.setDescription(payload.getDescription());
        p.setPrice(payload.getPrice());
        p.setSku(payload.getSku());
        p.setStock(payload.getStock());
        if (payload.getStatus() != null) p.setStatus(payload.getStatus());
        Product updated = productRepository.save(p);
        ApiResponse<Product> ar = ApiResponse.<Product>builder()
                .timestamp(Instant.now())
                .status(HttpStatus.OK.value())
                .message("Product updated")
                .data(updated)
                .path(req.getRequestURI())
                .build();
        return ResponseEntity.ok(ar);
    }

    // Admin: delete product
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @DeleteMapping("/private/admin/products/{id}")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable Long id, HttpServletRequest req) {
        if (!productRepository.existsById(id)) {
            ApiResponse<Object> ar = ApiResponse.builder()
                    .timestamp(Instant.now())
                    .status(HttpStatus.NOT_FOUND.value())
                    .message("Product not found")
                    .path(req.getRequestURI())
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ar);
        }
        productRepository.deleteById(id);
        ApiResponse<Object> ar = ApiResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.OK.value())
                .message("Product deleted")
                .path(req.getRequestURI())
                .build();
        return ResponseEntity.ok(ar);
    }

    // Admin: change stock (example)
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @PostMapping("/private/admin/products/{id}/stock")
    public ResponseEntity<ApiResponse<Product>> changeStock(@PathVariable Long id,
                                                            @RequestParam Integer delta,
                                                            HttpServletRequest req) {
        Product p = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        p.setStock((p.getStock() == null ? 0 : p.getStock()) + (delta == null ? 0 : delta));
        Product saved = productRepository.save(p);
        ApiResponse<Product> ar = ApiResponse.<Product>builder()
                .timestamp(Instant.now())
                .status(HttpStatus.OK.value())
                .message("Stock updated")
                .data(saved)
                .path(req.getRequestURI())
                .build();
        return ResponseEntity.ok(ar);
    }
}