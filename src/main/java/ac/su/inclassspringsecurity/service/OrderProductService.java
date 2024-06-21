package ac.su.inclassspringsecurity.service;

import ac.su.inclassspringsecurity.constant.OrderStatus;
import ac.su.inclassspringsecurity.constant.ProductStatusEnum;
import ac.su.inclassspringsecurity.constant.UserRole;
import ac.su.inclassspringsecurity.domain.Order;
import ac.su.inclassspringsecurity.domain.OrderProduct;
import ac.su.inclassspringsecurity.domain.Product;
import ac.su.inclassspringsecurity.domain.User;
import ac.su.inclassspringsecurity.repository.OrderProductRepository;
import ac.su.inclassspringsecurity.repository.OrderRepository;
import ac.su.inclassspringsecurity.repository.ProductRepository;
import ac.su.inclassspringsecurity.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderProductService {
    // 1) 리포지토리 주입
    private final OrderProductRepository orderProductRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    // TransactionTemplate 을 사용하여 Transaction 을 직접 제어할 수 있음
    private final TransactionTemplate transactionTemplate;

    // EntityManager 를 사용하여 직접 쿼리를 작성할 수 있음
    // EntityManager 를 사용하여 트랜잭션을 직접 제어할 수 있음
    // 현재 Bean(OrderProductService)에서 동일한 EntityManager 객체로 Transaction 을 제어하는 단위 형성됨
    // => DB 접근을 Transaction 의 요건에 맞춰서 제어할 수 있는 단일 Context 로 작용
    // => ACID의 Isolation 요건을 갖추려면 단일 Context 로 작용하는 EntityManager 가 필요
    @PersistenceContext
    private EntityManager entityManager;    // 현재 객체에 주입되는 Repository 와 Binding 되는 효과

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

    private List<Product> createDummyProducts() {
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
    // 효력 없는 이유 : 클래스 단위로 외부 클래스를 호출할 때에, Proxy 패턴을 통해 적용되기 때문
    // 클래스 간의 호출인 경우만 Transaction 의 시작과 종료를 제어 가능
    public void createDummyOrderProduct() {
        createDummyOrder();

        // 2-1) User & Order 생성 부분 OrderRepositoryTest 에서 참고
        List<Product> createdProductList = createDummyProducts();
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

    // 인라인 트랜잭션 적용 코드 예제
    public void createDummyOrderProductWithInlineTransaction() {
        // status 인자를 통해 명시적으로 제어 가능
        transactionTemplate.execute(status -> {
            try {
                // 트랜젝션 시작
                createDummyOrder();
                List<Product> createdProductList = createDummyProducts();
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
                    int orderedAmount = (int) (Math.random() * 10) + 1;
                    orderProduct.setQuantity(orderedAmount);    // 주문을 수행 => 상품 Entity 변경이 반드시 동반 되어야 함
                    OrderAmountPerProduct.put(product, orderedAmount);

                    // 가격 계산
                    orderProduct.setTotalPrice(product.getPrice() * orderedAmount);

                    // 기타 필수 정보 업데이트
                    orderProduct.setCreatedAt(String.valueOf(LocalDateTime.now()));
                    orderProduct.setUpdatedAt(String.valueOf(LocalDateTime.now()));

                    createdOrderProductList.add(orderProduct);
                }

                for (Product product : createdProductList) {
                    // 직전 재고 수량에서 주문 수량 차감 후 저장
                    product.setStockCount(product.getStockCount() - OrderAmountPerProduct.get(product));
                }

                // 연관 관계 뿐 아니라, Atomic 하게 다루고 싶은 작업은 전부 트랜젝션으로 묶어서 처리
                // disk, cache 등등 어떤 대상이건 Atomic 하게 다룰 수 있게 된다.
                // Application 단에서의 Transaction 이 가지는 장점!
                orderProductRepository.saveAll(createdOrderProductList);
//                String errorStr = null;
//                System.out.println(errorStr.length());
                productRepository.saveAll(createdProductList);
                // 모든 연관 관계 Entity 저장 성공 시 트랜젝션 커밋
            } catch (Exception e) {
                status.setRollbackOnly();
                // 함께 롤백 되어야 하는 부가 로직들을 정의할 수가 있음
                // DB 뿐만 아니라, 다른 영역에서의 작업들을 함께 롤백 처리 가능
                throw e;
            }
            return null;
        });
    }
}
