package ac.su.inclassspringsecurity.service;

import ac.su.inclassspringsecurity.constant.UserRole;
import ac.su.inclassspringsecurity.domain.User;
import ac.su.inclassspringsecurity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional  // 실패 시 발생하는 예외 처리를 위해 사용
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 유저 create
    public void create(
            String username,
            String password1,
            String email
    ) {
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(
                passwordEncoder.encode(password1)
        );
        newUser.setEmail(email);
        newUser.setRole(UserRole.USER);    // Enum default validation 필수 체크됨.

        // 중복 유저 체크
        validateDuplicateUser(newUser);
        User savedUser = userRepository.save(newUser);
    }

    // 중복 유저 검사 메서드 선언
    // 유저 중복 Validation 체크 (username, email) 후 중복 시 throw Exception
    public void validateDuplicateUser(User user) {
        if (isUserExist(user.getUsername())) {
            // throw 검사 발생시 rollback 시켜줌 - @Transactional
            throw new IllegalStateException("이미 존재하는 유저입니다.");
        }
        if (isEmailExist(user.getEmail())) {
            throw new IllegalStateException("이미 존재하는 이메일입니다.");
        }
    }

    // 유저 이름 중복 체크
    public boolean isUserExist(String username) {
        return userRepository.existsByUsername(username);
    }

    // 이메일 중복 체크
    public boolean isEmailExist(String email) {
        return userRepository.existsByEmail(email);
    }
}
