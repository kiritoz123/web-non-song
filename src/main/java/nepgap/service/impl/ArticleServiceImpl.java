package nepgap.service.impl;

import lombok.RequiredArgsConstructor;
import nepgap.dto.ArticleDTO;
import nepgap.exception.ApiException;
import nepgap.model.Article;
import nepgap.repository.ArticleRepository;
import nepgap.service.ArticleService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;

    @Override
    public List<ArticleDTO> getAllArticles() {
        return articleRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ArticleDTO getArticleById(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ApiException("Article not found", HttpStatus.NOT_FOUND));
        return toDTO(article);
    }

    private ArticleDTO toDTO(Article article) {
        return ArticleDTO.builder()
                .id(article.getId())
                .title(article.getTitle())
                .content(article.getContent())
                .author(article.getAuthor())
                .publishedAt(article.getPublishedAt())
                .build();
    }
}
