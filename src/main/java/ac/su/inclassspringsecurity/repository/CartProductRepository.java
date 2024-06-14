package ac.su.inclassspringsecurity.repository;

import ac.su.inclassspringsecurity.domain.CartProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartProductRepository extends JpaRepository<CartProduct, Long>, QuerydslPredicateExecutor<CartProduct> {
    List<CartProduct> findByCartId(Long cartId);
    List<CartProduct> findByProductId(Long productId);
}
