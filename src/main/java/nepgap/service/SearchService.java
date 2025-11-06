package nepgap.service;


import nepgap.dto.SearchResultDTO;

import java.util.List;

public interface SearchService {
    List<SearchResultDTO> search(String q);
}
