package ac.su.inclassspringsecurity.repository;

import ac.su.inclassspringsecurity.constant.UserRole;
import ac.su.inclassspringsecurity.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class UserRepositoryTest {
    @Autowired
    private  UserRepository userRepository;

    // PasswordEncoder 는 인터페이스 - 구현체 Bean 알아서 주입
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("User Create Test")
    public void create() {
        User newUser = new User();
        newUser.setUsername("test");
        newUser.setPassword(
                passwordEncoder.encode("test")
        );
        newUser.setEmail("test");
        newUser.setRole(UserRole.ADMIN);    // Enum default validation 필수 체크됨.
        User savedUser = userRepository.save(newUser);
        System.out.println(savedUser);
    }
}