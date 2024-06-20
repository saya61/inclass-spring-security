package ac.su.inclassspringsecurity.repository;

import ac.su.inclassspringsecurity.constant.ProductStatusEnum;
import ac.su.inclassspringsecurity.constant.UserRole;
import ac.su.inclassspringsecurity.domain.Cart;
import ac.su.inclassspringsecurity.domain.CartProduct;
import ac.su.inclassspringsecurity.domain.Product;
import ac.su.inclassspringsecurity.domain.User;
import ac.su.inclassspringsecurity.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class CartProductRepositoryTest {
    // 1) Repository 주입
    @Autowired
    private CartProductRepository cartProductRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;

    // 2) Dummy Data 생성
    void CreateDummyData() {
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("testPassword");
        user.setEmail("testEmail");
        user.setRole(UserRole.USER);
        User savedUser = userRepository.save(user); // User 데이터 저장 후 ID 저장(저장된 엔티티 다시 참조)
        System.out.println(user);

        Cart cart = new Cart();
        cart.setUser(savedUser);
        Cart savedCart = cartRepository.save(cart);
        System.out.println(savedCart);

        Product product = new Product();
        product.setName("testProduct");
        product.setPrice(1000);
        product.setStockCount(100);
        product.setStatus(ProductStatusEnum.IN_STOCK);
        product.setCreatedAt(String.valueOf(LocalDateTime.now()));
        product.setUpdatedAt(String.valueOf(LocalDateTime.now()));
        Product savedProduct = productRepository.save(product);
        System.out.println(savedProduct);

        CartProduct cartProduct = new CartProduct();
        cartProduct.setCart(savedCart);
        cartProduct.setProduct(savedProduct);
        cartProduct.setQuantity(10);
        // 나중에 복잡한 서비스를 위해 추가 예정
        cartProduct = cartProductRepository.save(cartProduct);  // 이미 저장된 엔티티(cart,product)를 다시 참조
        // 관심 대상 Entity 출력
        System.out.println(cartProduct);
    }

    void createMultipleDummyData() {
        // 여러 개의 엔티티를 생성하는 메서드
        // 모든 엔티티에 대해 5개씩(총 25개)의 데이터를 생성
        // 후속 쿼리에서 ID 값에 따라 적절한 결과가 나오는지 확인
        List<User> usersList = new ArrayList<>();
        List<Cart> cartList = new ArrayList<>();
        List<Product> productList = new ArrayList<>();
        List<CartProduct> cartProductList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            User user = new User();
            user.setUsername("testUser" + i);
            user.setPassword("testPassword" + i);
            user.setEmail("testEmail" + i + "@tt.cc");
            user.setRole(UserRole.USER);
            usersList.add(user);
        }

        for (int i = 0; i < 5; i++) {
            Product product = new Product();
            product.setName("testProduct" + i);
            product.setPrice(1000 * i);
            product.setStockCount(100 * i);
            product.setStatus(ProductStatusEnum.IN_STOCK);
            product.setCreatedAt(String.valueOf(LocalDateTime.now()));
            product.setUpdatedAt(String.valueOf(LocalDateTime.now()));
            productList.add(product);
        }

        for (int i = 0; i < 5; i++) {
            Cart cart = new Cart();
            cart.setUser(usersList.get(i)); // 위에 생성된 유저를 하나씩 할당
            cartList.add(cart);
        }

        for (int i = 0; i < 5; i++) {  // 카트 5개 수니회
            for (int j= 0; j < 5; j++) {   // 카트 하나당 product 5개
                CartProduct cartProduct = new CartProduct();
                cartProduct.setCart(cartList.get(i));  // 위에 생성된 카트를 하나씩 할당
                cartProduct.setProduct(productList.get(j));    // 위에 생성된 상품을 하나씩 할당
                if (i == 2) {
                    cartProduct.setQuantity(10);
                } else {
                    cartProduct.setQuantity(1);
                }
                cartProductList.add(cartProduct);
            }
        }
        userRepository.saveAll(usersList);
        cartRepository.saveAll(cartList);
        productRepository.saveAll(productList);
        cartProductRepository.saveAll(cartProductList);
    }

    // 3) 데이터 조회 테스트
    // 1. cart_id 로 검색 쿼리(user_id 기준으로 cart 검색 후 접근 가능)
    @Test
    @DisplayName("카트 아이디로 상품 조회")
    void findByCartId() {
        // 1) Dummy Data 호출
        CreateDummyData();
        // 2) 데이터 조회 쿼리 호출
        List<CartProduct> cartProductList = cartProductRepository.findByCartId(1L);
        // 3) console 출력값 확인
        assert !cartProductList.isEmpty();
        System.out.println(cartProductList);     // 카트에서 상품 참조 확인
        // 개행 및 파싱 들어가는 print 라이브러리 사용 추천
    }

    // 2. product_id 로 검색 쿼리
    @Test
    @DisplayName("상품 아이디로 상품 조회")
    void findByProductId() {
        // 1) Dummy Data 생성
        CreateDummyData();
        // 2) 데이터 조회 쿼리 호출
        List<CartProduct> cartProductList = cartProductRepository.findByProductId(1L);
        // 3) console 출력값 확인
        assert !cartProductList.isEmpty();
        System.out.println(cartProductList);     // 카트에서 상품 참조 확인
    }

    // 3. Multiple Data 조회
    @Test
    @DisplayName("여러 개의 상품 조회")
    void findMultipleData() {
        // Given When Then 패턴

        // Given (테스트 환경, 설정 등을 제공 : 테스트 사전 요건들)
        // Multiple Dummy Data 생성
        createMultipleDummyData();

        // When (테스트 핵심 목표가 되는 메서드 호출)
        // cart_id 기준으로 CartProduct 데이터 조회
        List<CartProduct> cartProductList1 = cartProductRepository.findByCartId(1L);
        List<CartProduct> cartProductList3 = cartProductRepository.findByCartId(3L);

        // Then (테스트 결과 검증 및 확인)
        // 테스트 결과가 의도에 부합하는지 검사
        assert !cartProductList1.isEmpty();
        assert !cartProductList3.isEmpty();
        // console 출력값 확인
        System.out.println(cartProductList1);     // 카트에서 상품 참조 확인
        System.out.println(cartProductList3);     // 카트에서 상품 참조 확인
    }
}