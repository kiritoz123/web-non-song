package nepgap.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import nepgap.model.OrderStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateOrderStatusRequest {
    @NotNull(message = "Status is required")
    private OrderStatus status;
}
