package nepgap.dto;

import lombok.*;
import nepgap.model.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDTO {
    private Long id;
    private Long userId;
    private BigDecimal total;
    private OrderStatus status;
    private Instant createdAt;
    private List<OrderItemDTO> items;
}
