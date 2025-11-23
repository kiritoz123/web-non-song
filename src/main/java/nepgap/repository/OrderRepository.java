package nepgap.repository;

import nepgap.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    /**
     * Tìm tất cả đơn hàng của một user
     */
    List<Order> findByUserId(Long userId);

    /**
     * Tìm đơn hàng theo ID và userId (để bảo mật)
     */
    Order findByIdAndUserId(Long id, Long userId);
}
