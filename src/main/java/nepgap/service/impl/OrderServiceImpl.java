package nepgap.service.impl;

import lombok.RequiredArgsConstructor;
import nepgap.dto.CreateOrderRequest;
import nepgap.dto.OrderDTO;
import nepgap.dto.OrderItemDTO;
import nepgap.dto.UpdateOrderStatusRequest;
import nepgap.exception.ApiException;
import nepgap.model.Order;
import nepgap.model.OrderItem;
import nepgap.model.OrderStatus;
import nepgap.repository.OrderRepository;
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

    @Override
    @Transactional
    public OrderDTO createOrder(Long userId, CreateOrderRequest request) {
        // Calculate total from items
        BigDecimal total = request.getItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Create order items
        List<OrderItem> orderItems = request.getItems().stream()
                .map(dto -> OrderItem.builder()
                        .productId(dto.getProductId())
                        .quantity(dto.getQuantity())
                        .price(dto.getPrice())
                        .build())
                .collect(Collectors.toList());

        // Create order
        Order order = Order.builder()
                .userId(userId)
                .total(total)
                .status(OrderStatus.PENDING)
                .createdAt(Instant.now())
                .items(orderItems)
                .build();

        Order savedOrder = orderRepository.save(order);

        return toDTO(savedOrder);
    }

    @Override
    public OrderDTO getOrderById(Long userId, Long orderId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new ApiException("Order not found", HttpStatus.NOT_FOUND));
        return toDTO(order);
    }

    @Override
    public List<OrderDTO> getUserOrders(Long userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderDTO updateOrderStatus(Long userId, Long orderId, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new ApiException("Order not found", HttpStatus.NOT_FOUND));

        order.setStatus(request.getStatus());
        Order savedOrder = orderRepository.save(order);

        return toDTO(savedOrder);
    }

    @Override
    @Transactional
    public void updateOrderStatusByPayment(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ApiException("Order not found", HttpStatus.NOT_FOUND));

        order.setStatus(status);
        orderRepository.save(order);
    }

    private OrderDTO toDTO(Order order) {
        List<OrderItemDTO> itemDTOs = order.getItems() != null
                ? order.getItems().stream()
                .map(item -> OrderItemDTO.builder()
                        .productId(item.getProductId())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .build())
                .collect(Collectors.toList())
                : List.of();

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
