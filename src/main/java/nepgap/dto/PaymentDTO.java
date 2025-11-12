package nepgap.dto;

import lombok.*;
import nepgap.model.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDTO {
    private Long id;
    private Long orderId;
    private Long userId;
    private BigDecimal amount;
    private String currency;
    private String provider;
    private String externalId;
    private PaymentStatus status;
    private Instant createdAt;
}
