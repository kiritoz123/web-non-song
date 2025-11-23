package nepgap.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nepgap.dto.ApiResponse;
import nepgap.dto.CreateOrderRequest;
import nepgap.dto.OrderDTO;
import nepgap.model.OrderStatus;
import nepgap.security.UserPrincipal;
import nepgap.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * Tạo đơn hàng từ giỏ hàng (user đã đăng nhập)
     * POST /api/v1/orders
     */
    @PostMapping
    public ResponseEntity<ApiResponse<OrderDTO>> createOrder(
            @AuthenticationPrincipal UserPrincipal user,
            @Valid @RequestBody CreateOrderRequest request,
            HttpServletRequest req) {
        
        OrderDTO order = orderService.createOrderFromCart(user.getId(), request);
        
        ApiResponse<OrderDTO> response = ApiResponse.<OrderDTO>builder()
                .timestamp(Instant.now())
                .status(HttpStatus.CREATED.value())
                .message("Tạo đơn hàng thành công")
                .data(order)
                .path(req.getRequestURI())
                .build();
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Lấy danh sách đơn hàng của user hiện tại
     * GET /api/v1/orders
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderDTO>>> getUserOrders(
            @AuthenticationPrincipal UserPrincipal user,
            HttpServletRequest req) {
        
        List<OrderDTO> orders = orderService.getUserOrders(user.getId());
        
        ApiResponse<List<OrderDTO>> response = ApiResponse.<List<OrderDTO>>builder()
                .timestamp(Instant.now())
                .status(HttpStatus.OK.value())
                .message("Lấy danh sách đơn hàng thành công")
                .data(orders)
                .path(req.getRequestURI())
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Lấy chi tiết đơn hàng theo ID
     * GET /api/v1/orders/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderDTO>> getOrderById(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long id,
            HttpServletRequest req) {
        
        OrderDTO order = orderService.getOrderById(user.getId(), id);
        
        ApiResponse<OrderDTO> response = ApiResponse.<OrderDTO>builder()
                .timestamp(Instant.now())
                .status(HttpStatus.OK.value())
                .message("Lấy chi tiết đơn hàng thành công")
                .data(order)
                .path(req.getRequestURI())
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Hủy đơn hàng (chỉ được phép khi trạng thái là PENDING)
     * PUT /api/v1/orders/{id}/cancel
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<OrderDTO>> cancelOrder(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long id,
            HttpServletRequest req) {
        
        OrderDTO order = orderService.cancelOrder(user.getId(), id);
        
        ApiResponse<OrderDTO> response = ApiResponse.<OrderDTO>builder()
                .timestamp(Instant.now())
                .status(HttpStatus.OK.value())
                .message("Hủy đơn hàng thành công")
                .data(order)
                .path(req.getRequestURI())
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Cập nhật trạng thái đơn hàng (chỉ ADMIN/MANAGER)
     * PUT /api/v1/orders/{id}/status
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<OrderDTO>> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status,
            HttpServletRequest req) {
        
        OrderDTO order = orderService.updateOrderStatus(id, status);
        
        ApiResponse<OrderDTO> response = ApiResponse.<OrderDTO>builder()
                .timestamp(Instant.now())
                .status(HttpStatus.OK.value())
                .message("Cập nhật trạng thái đơn hàng thành công")
                .data(order)
                .path(req.getRequestURI())
                .build();
        
        return ResponseEntity.ok(response);
    }
}
