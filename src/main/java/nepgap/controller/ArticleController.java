package nepgap.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import nepgap.dto.ApiResponse;
import nepgap.dto.ArticleDTO;
import nepgap.service.ArticleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ArticleDTO>>> getAllArticles(HttpServletRequest req) {
        List<ArticleDTO> articles = articleService.getAllArticles();
        ApiResponse<List<ArticleDTO>> response = ApiResponse.<List<ArticleDTO>>builder()
                .timestamp(Instant.now())
                .status(200)
                .message("Articles retrieved successfully")
                .data(articles)
                .path(req.getRequestURI())
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ArticleDTO>> getArticleById(
            @PathVariable Long id,
            HttpServletRequest req) {
        ArticleDTO article = articleService.getArticleById(id);
        ApiResponse<ArticleDTO> response = ApiResponse.<ArticleDTO>builder()
                .timestamp(Instant.now())
                .status(200)
                .message("Article retrieved successfully")
                .data(article)
                .path(req.getRequestURI())
                .build();
        return ResponseEntity.ok(response);
    }
}
