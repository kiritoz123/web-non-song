package nepgap.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import nepgap.dto.ApiResponse;
import nepgap.dto.CustomerDTO;
import nepgap.service.CustomerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    /**
     * Lấy tất cả khách hàng (chỉ ADMIN hoặc MANAGER)
     * GET /api/v1/customers
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<List<CustomerDTO>>> getAllCustomers(HttpServletRequest req) {
        List<CustomerDTO> customers = customerService.getAllCustomers();
        
        ApiResponse<List<CustomerDTO>> response = ApiResponse.<List<CustomerDTO>>builder()
                .timestamp(Instant.now())
                .status(HttpStatus.OK.value())
                .message("Lấy danh sách khách hàng thành công")
                .data(customers)
                .path(req.getRequestURI())
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Lấy khách hàng theo phân trang (chỉ ADMIN hoặc MANAGER)
     * GET /api/v1/customers/page?page=0&size=10&sort=id,desc
     */
    @GetMapping("/page")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Page<CustomerDTO>>> getCustomersPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            HttpServletRequest req) {
        
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<CustomerDTO> customers = customerService.getCustomers(pageable);
        
        ApiResponse<Page<CustomerDTO>> response = ApiResponse.<Page<CustomerDTO>>builder()
                .timestamp(Instant.now())
                .status(HttpStatus.OK.value())
                .message("Lấy danh sách khách hàng thành công")
                .data(customers)
                .path(req.getRequestURI())
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Lấy thông tin khách hàng theo ID (chỉ ADMIN hoặc MANAGER)
     * GET /api/v1/customers/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<CustomerDTO>> getCustomerById(
            @PathVariable Long id,
            HttpServletRequest req) {
        
        CustomerDTO customer = customerService.getCustomerById(id);
        
        ApiResponse<CustomerDTO> response = ApiResponse.<CustomerDTO>builder()
                .timestamp(Instant.now())
                .status(HttpStatus.OK.value())
                .message("Lấy thông tin khách hàng thành công")
                .data(customer)
                .path(req.getRequestURI())
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Đếm tổng số khách hàng (chỉ ADMIN hoặc MANAGER)
     * GET /api/v1/customers/count
     */
    @GetMapping("/count")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getTotalCustomers(HttpServletRequest req) {
        Long total = customerService.getTotalCustomers();
        
        Map<String, Long> data = new HashMap<>();
        data.put("totalCustomers", total);
        
        ApiResponse<Map<String, Long>> response = ApiResponse.<Map<String, Long>>builder()
                .timestamp(Instant.now())
                .status(HttpStatus.OK.value())
                .message("Lấy tổng số khách hàng thành công")
                .data(data)
                .path(req.getRequestURI())
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Tìm kiếm khách hàng (chỉ ADMIN hoặc MANAGER)
     * GET /api/v1/customers/search?keyword=john
     */
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<List<CustomerDTO>>> searchCustomers(
            @RequestParam(required = false) String keyword,
            HttpServletRequest req) {
        
        List<CustomerDTO> customers = customerService.searchCustomers(keyword);
        
        ApiResponse<List<CustomerDTO>> response = ApiResponse.<List<CustomerDTO>>builder()
                .timestamp(Instant.now())
                .status(HttpStatus.OK.value())
                .message("Tìm kiếm khách hàng thành công")
                .data(customers)
                .path(req.getRequestURI())
                .build();
        
        return ResponseEntity.ok(response);
    }
}
