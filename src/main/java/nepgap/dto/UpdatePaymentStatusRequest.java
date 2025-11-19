package nepgap.dto;


import jakarta.validation.constraints.NotNull;
import lombok.*;
import nepgap.model.PaymentStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdatePaymentStatusRequest {
    @NotNull(message = "Status is required")
    private PaymentStatus status;
}
