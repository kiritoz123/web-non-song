package nepgap.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 2000)
    private String description;

    private BigDecimal price;

    private String sku;

    private String locale = "vn";

    private Integer stock = 0;

    @Enumerated(EnumType.STRING)
    private ProductStatus status = ProductStatus.PUBLISHED;
}
