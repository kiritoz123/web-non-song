package nepgap.service;

import nepgap.dto.ArticleDTO;

import java.util.List;

public interface ArticleService {
    List<ArticleDTO> getAllArticles();
    ArticleDTO getArticleById(Long id);
}
