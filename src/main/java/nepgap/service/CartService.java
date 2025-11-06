package nepgap.service;


import nepgap.dto.CartDTO;

public interface CartService {
    CartDTO getCart(Long userId);
    CartDTO addItem(Long userId, Long productId, Integer qty);
    CartDTO removeItem(Long userId, Long productId);
    void clearCart(Long userId);
}
