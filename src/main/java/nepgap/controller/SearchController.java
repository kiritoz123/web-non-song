package nepgap.controller;

import jakarta.servlet.http.HttpServletRequest;
import nepgap.dto.ApiResponse;
import nepgap.dto.SearchResultDTO;
import nepgap.service.SearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1/search")
public class SearchController {

    private final SearchService searchService;
    public SearchController(SearchService searchService) { this.searchService = searchService; }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SearchResultDTO>>> search(@RequestParam("q") String q, HttpServletRequest req) {
        var list = searchService.search(q);
        ApiResponse<List<SearchResultDTO>> res = ApiResponse.<List<SearchResultDTO>>builder()
                .timestamp(Instant.now())
                .status(200)
                .message("Search results")
                .data(list)
                .path(req.getRequestURI())
                .build();
        return ResponseEntity.ok(res);
    }
}