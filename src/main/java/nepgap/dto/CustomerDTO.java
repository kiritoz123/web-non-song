package nepgap.dto;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerDTO {
    private Long id;
    private String email;
    private String fullName;
    private String phone;
    private String avatar;
    private Set<String> roles;
}
