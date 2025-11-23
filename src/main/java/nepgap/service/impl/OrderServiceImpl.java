package nepgap.service.impl;

import lombok.RequiredArgsConstructor;
import nepgap.dto.CreateOrderRequest;
import nepgap.dto.OrderDTO;
import nepgap.exception.ApiException;
import nepgap.model.*;
import nepgap.repository.CartRepository;
import nepgap.repository.OrderRepository;
import nepgap.repository.ProductRepository;
import nepgap.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public OrderDTO createOrderFromCart(Long userId, CreateOrderRequest request) {
        // Lấy giỏ hàng của user
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ApiException("Giỏ hàng trống", HttpStatus.BAD_REQUEST));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new ApiException("Giỏ hàng trống", HttpStatus.BAD_REQUEST);
        }

        // Tạo danh sách order items và tính tổng tiền
        BigDecimal total = BigDecimal.ZERO;
        List<OrderItem> orderItems = cart.getItems().stream()
                .map(cartItem -> {
                    Product product = productRepository.findById(cartItem.getProductId())
                            .orElseThrow(() -> new ApiException("Sản phẩm không tồn tại: " + cartItem.getProductId(), HttpStatus.NOT_FOUND));
                    
                    // Kiểm tra tồn kho
                    if (product.getStock() < cartItem.getQuantity()) {
                        throw new ApiException("Sản phẩm '" + product.getName() + "' không đủ tồn kho", HttpStatus.BAD_REQUEST);
                    }

                    // Giảm số lượng tồn kho
                    product.setStock(product.getStock() - cartItem.getQuantity());
                    productRepository.save(product);

                    return OrderItem.builder()
                            .productId(cartItem.getProductId())
                            .quantity(cartItem.getQuantity())
                            .price(product.getPrice())
                            .build();
                })
                .collect(Collectors.toList());

        // Tính tổng tiền
        for (OrderItem item : orderItems) {
            total = total.add(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        // Tạo đơn hàng
        Order order = Order.builder()
                .userId(userId)
                .total(total)
                .status(OrderStatus.PENDING)
                .createdAt(Instant.now())
                .items(orderItems)
                .build();

        Order savedOrder = orderRepository.save(order);

        // Xóa giỏ hàng sau khi tạo đơn hàng
        cart.getItems().clear();
        cartRepository.save(cart);

        return convertToDTO(savedOrder);
    }

    @Override
    public List<OrderDTO> getUserOrders(Long userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public OrderDTO getOrderById(Long userId, Long orderId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId);
        if (order == null) {
            throw new ApiException("Không tìm thấy đơn hàng", HttpStatus.NOT_FOUND);
        }
        return convertToDTO(order);
    }

    @Override
    @Transactional
    public OrderDTO updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ApiException("Không tìm thấy đơn hàng", HttpStatus.NOT_FOUND));
        
        order.setStatus(status);
        Order savedOrder = orderRepository.save(order);
        
        return convertToDTO(savedOrder);
    }

    @Override
    @Transactional
    public OrderDTO cancelOrder(Long userId, Long orderId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId);
        if (order == null) {
            throw new ApiException("Không tìm thấy đơn hàng", HttpStatus.NOT_FOUND);
        }

        // Chỉ cho phép hủy nếu đơn hàng đang ở trạng thái PENDING
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new ApiException("Chỉ có thể hủy đơn hàng đang chờ xử lý", HttpStatus.BAD_REQUEST);
        }

        // Hoàn lại số lượng tồn kho
        for (OrderItem item : order.getItems()) {
            Product product = productRepository.findById(item.getProductId()).orElse(null);
            if (product != null) {
                product.setStock(product.getStock() + item.getQuantity());
                productRepository.save(product);
            }
        }

        order.setStatus(OrderStatus.CANCELLED);
        Order savedOrder = orderRepository.save(order);

        return convertToDTO(savedOrder);
    }

    /**
     * Chuyển đổi Order entity sang OrderDTO
     */
    private OrderDTO convertToDTO(Order order) {
        List<OrderDTO.OrderItemDTO> itemDTOs = order.getItems().stream()
                .map(item -> {
                    Product product = productRepository.findById(item.getProductId()).orElse(null);
                    return OrderDTO.OrderItemDTO.builder()
                            .id(item.getId())
                            .productId(item.getProductId())
                            .productName(product != null ? product.getName() : "Unknown")
                            .quantity(item.getQuantity())
                            .price(item.getPrice())
                            .build();
                })
                .collect(Collectors.toList());

        return OrderDTO.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .total(order.getTotal())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .items(itemDTOs)
                .build();
    }
}
