package nepgap.model;

import java.math.BigDecimal;

public interface ProductProjection {
    Long getId();
    String getName();
    String getDescription();
    BigDecimal getPrice();
    String getPlace();
    Integer getStock();
    String getStatus();
    String getUrl();
}
