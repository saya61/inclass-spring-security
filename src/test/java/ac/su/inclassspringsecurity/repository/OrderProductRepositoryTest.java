package ac.su.inclassspringsecurity.repository;

import ac.su.inclassspringsecurity.constant.OrderStatus;
import ac.su.inclassspringsecurity.constant.ProductStatusEnum;
import ac.su.inclassspringsecurity.constant.UserRole;
import ac.su.inclassspringsecurity.domain.Order;
import ac.su.inclassspringsecurity.domain.OrderProduct;
import ac.su.inclassspringsecurity.domain.Product;
import ac.su.inclassspringsecurity.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
// TestPropertySource 를 별도로 쓰지 않고 MySQL DB에 직업 테스트 하며 데이터 확인
class OrderProductRepositoryTest {
    // User & Order & OrderProduct & Product 생성 및 테스트
    // 1) 리포지토리 주입
    @Autowired
    private OrderProductRepository orderProductRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;

    // 2) 더미 데이터 생성 : 총액 계산 및 상품 재고 차감 구현
    // 2-1) User & Order 생성 부분 OrderRepositoryTest 에서 참고
    // 2-2) Product 생성 부분 ProductRepositoryTest 에서 참고
    // 2-3) OrderProduct 생성 -> 주문 수량 만큼 Product 재고 차감 & 총액 계산
    //      재고 차감 직전에 에러가 나면 어떻게 될까?
    //      1) 아는 원인 => Data Validation 가능
    //          - 주문 수량이 재고보다 많을 때, 재고 차감 전에 예외 처리 필요(데이터 문제 Validation)
    //      2) 모르는 원인 => Data Validation 불가
    //          - 알 수 없는 에러가 나도 항상 데이터 정합성 보장 필요
    //          - 트랜잭션 적용 (All or Nothing) -> Service Layer 에서 Transaction 적용

    private String createDummyOrder() {
        // 테스트 회차를 반복하며 유저를 여러번 생성할 때,
        // 중복 유저가 쉽게 생산되지 않도록(Unique field) 생성
        // => UUID 사용(중복 string 발생시키는 방향이면 뭐든 좋음)
        String uuid10 = UUID.randomUUID().toString().substring(0, 10);   // 10자리 UUID 생성
        User user = new User();
        user.setUsername("testUser: " + uuid10);
        user.setPassword("1111");
        user.setEmail(uuid10 + "@tt.cc");
        user.setRole(UserRole.USER);
        User savedUser =userRepository.save(user);

        // User 와 관련된 Order 생성
        Order order = new Order();
        order.setUser(savedUser);
        order.setCreatedAt(String.valueOf(LocalDateTime.now()));
        order.setUpdatedAt(String.valueOf(LocalDateTime.now()));
        order.setStatus(OrderStatus.ORDERED);
        orderRepository.save(order);

        return user.getUsername();
    }

    private List<Product> createDummyProduct() {
        List<Product> productList = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Product product = new Product();
            product.setName("테스트 상품 " + i);
            product.setPrice(1000 * (int)(Math.random() * 10));
            product.setStockCount(100 * i);
            product.setStatus(ProductStatusEnum.IN_STOCK);
            product.setCreatedAt(String.valueOf(LocalDateTime.now()));
            product.setUpdatedAt(String.valueOf(LocalDateTime.now()));
            product.setDescription("상세정보를 입력합니다");
            product.setMemo("메모 입력");
            product.setImage("/static/path/to/image");
            productList.add(product);
        }
        List<Product> savedProductList = productRepository.saveAll(productList);
        savedProductList.forEach(System.out::println);

        return savedProductList;
    }

    @Transactional  // transaction 적용, 서비스 레이어 없으면 효력없음
    public void createDummyOrderProduct() {
        createDummyOrder();

        // 2-1) User & Order 생성 부분 OrderRepositoryTest 에서 참고
        List<Product> createdProductList = createDummyProduct();
        // 2-2) Product 생성 부분 ProductRepositoryTest 에서 참고
        List<OrderProduct> createdOrderProductList = new ArrayList<>();

        Map<Product, Integer> OrderAmountPerProduct = new HashMap<>();

        for (Product product : createdProductList) {
            OrderProduct orderProduct = new OrderProduct();
            // 초기화 (상품을 랜덤하게 1~10개 담기)
            Optional<Order> lastOrder = orderRepository.findFirstByOrderByIdDesc();
            assert lastOrder.isPresent();
            Order order = lastOrder.get();

            orderProduct.setOrder(order);
            orderProduct.setProduct(product);

            // 주문 수량 Random 설정
            int orderedAmount = (int)(Math.random() * 10) + 1;
            orderProduct.setQuantity(orderedAmount);    // 주문을 수행 => 상품 Entity 변경이 반드시 동반 되어야 함

            // 가격 계산
            orderProduct.setTotalPrice(product.getPrice() * orderedAmount);

            // 기타 필수 정보 업데이트
            orderProduct.setCreatedAt(String.valueOf(LocalDateTime.now()));
            orderProduct.setUpdatedAt(String.valueOf(LocalDateTime.now()));

            createdOrderProductList.add(orderProduct);
            OrderAmountPerProduct.put(product, orderedAmount);
        }
        orderProductRepository.saveAll(createdOrderProductList);

        // 재고 차감 직전에 에러 발생
        String errorStr = null;
        System.out.println(errorStr.length());  // NullPointerException 발생
        // 주문 수량 만큼 Product 재고 차감
        for (Product product : createdProductList) {
            // 직전 재고 수량에서 주문 수량 차감 후 저장
            product.setStockCount(product.getStockCount() - OrderAmountPerProduct.get(product));
            productRepository.save(product);
        }
    }

    // 3) 테스트 메서드 생성
    @Test
    void findByOrderId() {
        // 여기에 테스트 구현
        // Given - When - Then 패턴으로 구현
        // Given : 테스트 데이터 생성
        createDummyOrderProduct();

        // When : 테스트 메서드 실행

        // Then : 테스트 결과 검증
    }
}