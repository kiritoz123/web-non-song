package nepgap.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nepgap.dto.*;
import nepgap.security.UserPrincipal;
import nepgap.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderDTO>> createOrder(
            @AuthenticationPrincipal UserPrincipal user,
            @Valid @RequestBody CreateOrderRequest request,
            HttpServletRequest req) {
        OrderDTO order = orderService.createOrder(user.getId(), request);
        ApiResponse<OrderDTO> response = ApiResponse.<OrderDTO>builder()
                .timestamp(Instant.now())
                .status(HttpStatus.CREATED.value())
                .message("Order created successfully")
                .data(order)
                .path(req.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderDTO>> getOrderById(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long id,
            HttpServletRequest req) {
        OrderDTO order = orderService.getOrderById(user.getId(), id);
        ApiResponse<OrderDTO> response = ApiResponse.<OrderDTO>builder()
                .timestamp(Instant.now())
                .status(200)
                .message("Order retrieved successfully")
                .data(order)
                .path(req.getRequestURI())
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderDTO>>> getUserOrders(
            @AuthenticationPrincipal UserPrincipal user,
            HttpServletRequest req) {
        List<OrderDTO> orders = orderService.getUserOrders(user.getId());
        ApiResponse<List<OrderDTO>> response = ApiResponse.<List<OrderDTO>>builder()
                .timestamp(Instant.now())
                .status(200)
                .message("Orders retrieved successfully")
                .data(orders)
                .path(req.getRequestURI())
                .build();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<OrderDTO>> updateOrderStatus(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequest request,
            HttpServletRequest req) {
        OrderDTO order = orderService.updateOrderStatus(user.getId(), id, request);
        ApiResponse<OrderDTO> response = ApiResponse.<OrderDTO>builder()
                .timestamp(Instant.now())
                .status(200)
                .message("Order status updated successfully")
                .data(order)
                .path(req.getRequestURI())
                .build();
        return ResponseEntity.ok(response);
    }
}
