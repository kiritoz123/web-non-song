package nepgap.service;

import nepgap.dto.CreateOrderRequest;
import nepgap.dto.OrderDTO;
import nepgap.dto.UpdateOrderStatusRequest;
import nepgap.model.OrderStatus;

import java.util.List;

public interface OrderService {
    OrderDTO createOrder(Long userId, CreateOrderRequest request);
    OrderDTO getOrderById(Long userId, Long orderId);
    List<OrderDTO> getUserOrders(Long userId);
    OrderDTO updateOrderStatus(Long userId, Long orderId, UpdateOrderStatusRequest request);
    void updateOrderStatusByPayment(Long orderId, OrderStatus status);
}
