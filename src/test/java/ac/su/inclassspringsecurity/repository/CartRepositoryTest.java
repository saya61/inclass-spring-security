package ac.su.inclassspringsecurity.repository;

import ac.su.inclassspringsecurity.constant.UserRole;
import ac.su.inclassspringsecurity.domain.Cart;
import ac.su.inclassspringsecurity.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class CartRepositoryTest {
    // 1) Repository 주입
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    void createDummyData() {
        // 2) Dummy Data 생성 (최소 5개 리스트 기반으로 쿼리 한번에 생성)
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("testPassword");
        user.setEmail("testEmail");
        user.setRole(UserRole.USER);
        User savedUser = userRepository.save(user);
        System.out.println(user);

        Cart cart = new Cart();
        cart.setUser(savedUser);
        Cart savedCart = cartRepository.save(cart);
        System.out.println(savedCart);
    }
    @Test
    @DisplayName("유저 아이디로 검색")
    void findByUserId() {
        // 2) Dummy Data 호출
        createDummyData();
        // 3) 데이터 조회 쿼리 호출
        Optional<Cart> cart = cartRepository.findByUserId(1L);
        assert cart.isPresent();
        // 4) console 출력값 확인
        System.out.println(cart.get());     // 카트에서 유저 참조 확인
        System.out.println(cart.get().getUser());       // 유저에서 카트 참조 확인
    }
}