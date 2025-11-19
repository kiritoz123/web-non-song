package nepgap.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileDTO {
    private Long id;
    private String email;
    private String fullName;
    private String phone;
    private String avatar;
}