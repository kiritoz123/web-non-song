package nepgap.repository;


import nepgap.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    List<Article> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(String title, String content);
}
