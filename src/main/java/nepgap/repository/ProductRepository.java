package nepgap.repository;


import nepgap.model.Product;
import nepgap.model.ProductProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String desc);

    @Query("select p.id, p.name, p.description, p.price,p.locale,p.stock,p.status,pi.url from Product p LEFT JOIN ProductImage pi where (:q is null or lower(p.name) like lower(concat('%',:q,'%')) or lower(p.description) like lower(concat('%',:q,'%')))")
    List<ProductProjection> searchByKeyword(String q);


    @Query("""
        SELECT p.id, p.name, p.description, p.price,p.locale,p.stock,p.status,pi.url FROM Product p LEFT JOIN ProductImage pi ON p.id = pi.productId
        """)
    List<ProductProjection> findAllProduct();
}