package nepgap.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "payment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String provider;

    private String phone;

    private String address;

    private BigDecimal amount;

    private String currency;

    private String receiver;

    private String description;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private Long userId;

}