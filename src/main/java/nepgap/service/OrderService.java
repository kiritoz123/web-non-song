package nepgap.service;

import nepgap.dto.CreateOrderRequest;
import nepgap.dto.OrderDTO;
import nepgap.model.OrderStatus;

import java.util.List;

public interface OrderService {
    /**
     * Tạo đơn hàng từ giỏ hàng của user
     */
    OrderDTO createOrderFromCart(Long userId, CreateOrderRequest request);

    /**
     * Lấy tất cả đơn hàng của user
     */
    List<OrderDTO> getUserOrders(Long userId);

    /**
     * Lấy chi tiết đơn hàng theo ID
     */
    OrderDTO getOrderById(Long userId, Long orderId);

    /**
     * Cập nhật trạng thái đơn hàng (cho admin/manager)
     */
    OrderDTO updateOrderStatus(Long orderId, OrderStatus status);

    /**
     * Hủy đơn hàng (cho user)
     */
    OrderDTO cancelOrder(Long userId, Long orderId);
}
