package nepgap.service.impl;

import nepgap.dto.CartDTO;
import nepgap.model.Cart;
import nepgap.model.CartItem;
import nepgap.repository.CartRepository;
import nepgap.repository.ProductRepository;
import nepgap.service.CartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public CartServiceImpl(CartRepository cartRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public CartDTO getCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId).orElseGet(() -> cartRepository.save(Cart.builder().userId(userId).build()));
        return toDto(cart);
    }

    @Override
    @Transactional
    public CartDTO addItem(Long userId, Long productId, Integer qty) {
        var product = productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException("Product not found"));
        Cart cart = cartRepository.findByUserId(userId).orElseGet(() -> cartRepository.save(Cart.builder().userId(userId).build()));
        var existing = cart.getItems().stream().filter(i -> i.getProductId().equals(productId)).findFirst();
        if (existing.isPresent()) {
            existing.get().setQuantity(existing.get().getQuantity() + qty);
        } else {
            cart.getItems().add(CartItem.builder().productId(productId).quantity(qty).build());
        }
        cartRepository.save(cart);
        return toDto(cart);
    }

    @Override
    @Transactional
    public CartDTO removeItem(Long userId, Long productId) {
        Cart cart = cartRepository.findByUserId(userId).orElseThrow();
        cart.getItems().removeIf(i -> i.getProductId().equals(productId));
        cartRepository.save(cart);
        return toDto(cart);
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        cartRepository.findByUserId(userId).ifPresent(c -> {
            c.getItems().clear();
            cartRepository.save(c);
        });
    }

    private CartDTO toDto(Cart cart) {
        return CartDTO.builder()
                .cartId(cart.getId())
                .userId(cart.getUserId())
                .items(cart.getItems().stream().map(i -> CartDTO.CartItemDTO.builder()
                        .productId(i.getProductId())
                        .quantity(i.getQuantity()).build()).collect(Collectors.toList()))
                .build();
    }
}
