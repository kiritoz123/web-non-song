package nepgap.service.impl;


import nepgap.dto.SearchResultDTO;
import nepgap.model.Article;
import nepgap.model.Product;
import nepgap.repository.ArticleRepository;
import nepgap.repository.ProductRepository;
import nepgap.service.SearchService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SearchServiceImpl implements SearchService {

    private final ProductRepository productRepository;
    private final ArticleRepository articleRepository;

    public SearchServiceImpl(ProductRepository productRepository, ArticleRepository articleRepository) {
        this.productRepository = productRepository;
        this.articleRepository = articleRepository;
    }

    @Override
    public List<SearchResultDTO> search(String q) {
        List<SearchResultDTO> results = new ArrayList<>();
        if (q == null || q.isBlank()) return results;

        List<Product> products = productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(q, q);
        products.forEach(p -> results.add(SearchResultDTO.builder()
                .type("product")
                .id(p.getId())
                .title(p.getName())
                .snippet(p.getDescription()!=null && p.getDescription().length()>150 ? p.getDescription().substring(0,150) : p.getDescription())
                .build()));

        List<Article> articles = articleRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(q, q);
        articles.forEach(a -> results.add(SearchResultDTO.builder()
                .type("article")
                .id(a.getId())
                .title(a.getTitle())
                .snippet(a.getContent()!=null && a.getContent().length()>150 ? a.getContent().substring(0,150) : a.getContent())
                .build()));

        return results;
    }
}
