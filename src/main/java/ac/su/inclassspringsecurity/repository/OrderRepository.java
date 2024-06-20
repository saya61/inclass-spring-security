package ac.su.inclassspringsecurity.repository;

import ac.su.inclassspringsecurity.domain.Order;
import ac.su.inclassspringsecurity.domain.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, QuerydslPredicateExecutor<Order> {
    // 주문 조회 기준
    // 1) 주문 번호가 몇 번인가?
//    Optional<Order> findById(Long userId);

    // 2) 누가 주문했는가?
    List<Order> findByUserId(Long userId);

    // 3) 가장 최근에 발생한 주문은 무엇인가?
    Optional<Order> findFirstByOrderByIdDesc();
}
