package ac.su.inclassspringsecurity.service;

import ac.su.inclassspringsecurity.domain.AccessTokenDTO;
import ac.su.inclassspringsecurity.domain.User;
import ac.su.inclassspringsecurity.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;


@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class UserServiceTest {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("publish token test")
    public void getAccessTokenTest(){
        userService.create(
                "tester01",
                "1234",
                "tester01@tt.cc"
        );
        Optional<User> optionalUser = userRepository.findByEmail("tester01@tt.cc");
        assert optionalUser.isPresent();
        User createdUser = optionalUser.get();  // get() 메서드로 User 객체를 반환
        createdUser.setPassword("1234");    // form 에서 받은 것처럼
        AccessTokenDTO accessToken = userService.getAccessToken(createdUser);
        assert accessToken != null; // test 코드에서는 바로 assert 써도 무방
    }
}


