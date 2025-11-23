package nepgap.controller;


import jakarta.servlet.http.HttpServletRequest;
import nepgap.dto.ApiResponse;
import nepgap.dto.CartDTO;
import nepgap.security.UserPrincipal;
import nepgap.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    public CartController(CartService cartService) { this.cartService = cartService; }

    @GetMapping
    public ResponseEntity<ApiResponse<CartDTO>> getCart(@AuthenticationPrincipal UserPrincipal user, HttpServletRequest req) {
        CartDTO dto = cartService.getCart(user.getId());
        ApiResponse<CartDTO> res = ApiResponse.<CartDTO>builder()
                .timestamp(Instant.now())
                .status(200)
                .message("Lấy giỏ hàng thành công")
                .data(dto)
                .path(req.getRequestURI())
                .build();
        return ResponseEntity.ok(res);
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<CartDTO>> addItem(@AuthenticationPrincipal UserPrincipal user,
                                                        @RequestParam Long productId,
                                                        @RequestParam(defaultValue = "1") Integer qty,
                                                        HttpServletRequest req) {
        CartDTO dto = cartService.addItem(user.getId(), productId, qty);
        ApiResponse<CartDTO> res = ApiResponse.<CartDTO>builder()
                .timestamp(Instant.now())
                .status(200)
                .message("Thêm sản phẩm vào giỏ hàng thành công")
                .data(dto)
                .path(req.getRequestURI())
                .build();
        return ResponseEntity.ok(res);
    }

    @PostMapping("/remove")
    public ResponseEntity<ApiResponse<CartDTO>> removeItem(@AuthenticationPrincipal UserPrincipal user,
                                                           @RequestParam Long productId,
                                                           HttpServletRequest req) {
        CartDTO dto = cartService.removeItem(user.getId(), productId);
        ApiResponse<CartDTO> res = ApiResponse.<CartDTO>builder()
                .timestamp(Instant.now())
                .status(200)
                .message("Xóa sản phẩm khỏi giỏ hàng thành công")
                .data(dto)
                .path(req.getRequestURI())
                .build();
        return ResponseEntity.ok(res);
    }
}
