package nepgap.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchResultDTO {
    private String type;
    private Long id;
    private String title;
    private String snippet;
}
