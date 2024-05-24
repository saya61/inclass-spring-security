package ac.su.inclassspringsecurity.repository;

import ac.su.inclassspringsecurity.constant.ProductStatusEnum;
import ac.su.inclassspringsecurity.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, QuerydslPredicateExecutor<Product> {
    // 상품 쿼리 케이스 제어 (1) : 상품 이름에 따른 상품 조회
    List<Product> findByName(String name);
    // 구현부를 전혀 작성하지 않아도 JPA 에서 약속된 이름의 메서드를 선언하기만 하면
    // 구체적인 로직은 자동 생성

    // 상품 쿼리 케이스 제어 (2) : 상품 상태에 따른 상품 리스트 조회
    List<Product> findByStatus(ProductStatusEnum productStatusEnum);

    // 객체를 통으로 반환하기 위해 p, 컬럼으로 하려면 p.name 등으로
    // : 은 문자열 안에 : 들어간거임.
    // 쿼리 해석할 때 아래 인자를 받아옴
    // Product 는 클래스로 오타날시 오류남!!
    @Query("SELECT p FROM Product p WHERE p.status in :statusList")
    List<Product> findByStatusList(List<ProductStatusEnum> statusList);

    // JPQL 못바꿀때
    // product 오타 내도 오류 안뜸ㄴ
    @Query(value = "SELECT  * FROM product WHERE status in :statusList", nativeQuery = true)
    List<Product> findByStatusListNative(List<ProductStatusEnum> statusList);
}
