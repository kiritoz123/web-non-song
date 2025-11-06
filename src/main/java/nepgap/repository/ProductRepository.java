package nepgap.repository;


import nepgap.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String desc);

    @Query("select p from Product p where (:q is null or lower(p.name) like lower(concat('%',:q,'%')) or lower(p.description) like lower(concat('%',:q,'%')))")
    List<Product> searchByKeyword(String q);
}