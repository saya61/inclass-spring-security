package ac.su.inclassspringsecurity.repository;

import ac.su.inclassspringsecurity.constant.OrderStatus;
import ac.su.inclassspringsecurity.constant.UserRole;
import ac.su.inclassspringsecurity.domain.Order;
import ac.su.inclassspringsecurity.domain.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class OrderRepositoryTest {
    // User & Order 생성 및 테스트
    // 1) 리포지토리 주입
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderRepository orderRepository;

    // 2) 더미 데이터 생성
    String createDummyOrder() {
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

    // 3) 테스트 메서드 생성
    @Test
    void findByUserId() {
        // 여기에 테스트 구현
        // Given - When - Then 패턴으로 구현
        // Given
        createDummyOrder();
        String username = createDummyOrder();
        Optional<User> user = userRepository.findByUsername(username);
        assert user.isPresent();
        Long userId = user.get().getId();

        // When
        List<Order> orderList = orderRepository.findByUserId(userId);

        // Then
        assert !orderList.isEmpty();
        System.out.println(user.get());
        System.out.println(orderList);
    }

    @Test
    void findFirstByOrderByIdDesc() {
        // 여기에 테스트 구현
        // Given - When - Then 패턴으로 구현
        // Given
        createDummyOrder();

        // When
        Optional<Order> lastOrder = orderRepository.findFirstByOrderByIdDesc();

        // Then
        assert lastOrder.isPresent();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        // json parsing 할 때에는 try-catch 구문을 사용해야 함
        try {
            String lastOrderJson = objectMapper.writeValueAsString(lastOrder.get());
            System.out.println(lastOrderJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

//        System.out.println(lastOrder);
    }
}