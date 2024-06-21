package ac.su.inclassspringsecurity.service;

import ac.su.inclassspringsecurity.config.Jwt.TokenProvider;
import ac.su.inclassspringsecurity.constant.UserRole;
import ac.su.inclassspringsecurity.domain.*;
import ac.su.inclassspringsecurity.repository.UserRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

// UserDetailsService 패키지에 Spring Security 가 제공하는 인터페이스로, 유저 정보를 가져오는 메서드를 구현해야 함.
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional  // 실패 시 발생하는 예외 처리를 위해 사용
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    // 로그인 전용 메서드 Override
    @Override
    public UserDetails loadUserByUsername(
            String email    // 로그인 ID 를 말함, username 에서 email 로 변경했지만 오류 x = String 으로 타입이 같음.
    ) throws UsernameNotFoundException {
        // UserDetails 객체를 생성 및 반환
        Optional<User> registeredUser = userRepository.findByEmail(email);
        if (registeredUser.isEmpty()) {
            throw new UsernameNotFoundException(email);
        }
        // Optional 객체를 반환하므로 get() 메서드로 User 객체를 반환
        // User 객체를 SpringUser 객체로 변환하여 반환필요

        // 1번. 인증에 사용하기 위해 준비된 UserDetails 구현체
//        User foundUser = registeredUser.get();
//        return new SpringUser(
//                foundUser.getEmail(),
//                foundUser.getPassword(),
//                new ArrayList<>()
//        );
        // 2번. SpringUser 객체로 변환하여 반환
        return SpringUser.getSpringUserDetails(registeredUser.get());
    }

    // CRUD 기능 구현
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

    public AccessTokenDTO getAccessToken(User user) {
        // 1) Spring Security 로그인 전용 메서드 loadUserByUsername 사용해 인증
        UserDetails userDetails;
        try {
            userDetails = loadUserByUsername(user.getEmail());    // UserDetails 리턴
        } catch (Exception e) {
            return null;
        }
        // 2) UserService 에 TokenProvider 주입 -- final 필드로 생성자 주입 완료
        // 3) TokenProvider 에서 Token String 을 생성
        // 비밀번호 체크
        if (passwordEncoder.matches(user.getPassword(), userDetails.getPassword())) {  // user - raw, userDetails - encoded
            // 4) AccessTokenDTO 로 Wrapping 및 리턴
            String accessToken = tokenProvider.generateToken(user, Duration.ofHours(1L));
            String tokenType = "Bearer";
            return new AccessTokenDTO(
                    accessToken,
                    tokenType
            );
        }
        return null;    // 패스워드 불일치 시 null 반환
    }

    public List<User> makeDummyData(int count) {
        List<User> users = userRepository.findAll();
        // 유저 타입별 count 만큼 생성
        // 기존 유저 수 10명 이상 일 경우 스킵 후 기존 리스트 반환
        if (userRepository.count() >= 10) {
            return users;
        }
        for (UserRole role : UserRole.values()) {
            for (int i = 1; i <= count; i++) {
                User newUser = new User();
                newUser.setUsername(role.name() + i);
                newUser.setPassword(passwordEncoder.encode("1234"));
                newUser.setEmail(role.name() + i + "@tt.cc");
                newUser.setRole(role);
                users.add(newUser);
            }
        }
        return userRepository.saveAll(users);
    }

    public List<User> getUserByRole(UserRole role) {
//        // 아래 validation 은 불필요 - role 신뢰
//        if (role == null || role.equals(UserRole.USER)) {
//            return null;
//        }
        List<UserRole> roles = new ArrayList<>();
        // Role이 늘어나도 똑같이 실행됨
        for (int i = role.ordinal(); i < UserRole.values().length; i++) {
            // SUPER 는 i = 0 이므로, 전부 조회됨
            roles.add(UserRole.values()[i]);
        }
        List<User> users = new ArrayList<>();
        for (UserRole targetRole : roles) {
            users.addAll(userRepository.findByRole(targetRole));
        }
        return users;
    }

    public Page<User> getUsersPageByRole(UserRole role, int page, int size) {
        // 위에 있는 List 를 Page 로 변환
        List<UserRole> roles = new ArrayList<>();
        // Role이 늘어나도 똑같이 실행됨
        for (int i = role.ordinal(); i < UserRole.values().length; i++) {
            // SUPER 는 i = 0 이므로, 전부 조회됨
            roles.add(UserRole.values()[i]);
        }
        List<User> users = new ArrayList<>();
        for (UserRole targetRole : roles) {
            users.addAll(userRepository.findByRole(targetRole));
        }

        Pageable pageable = PageRequest.of(page, size);
        // DB가 항상 촘촘한 것은 아니기에 MIN, MAX 이용(size 가 상한임)
        int start = Math.min((int) pageable.getOffset(), users.size());     // 시작 인덱스를 잡기 위한 기준점 생성
        int end = Math.min((start + pageable.getPageSize()), users.size());     // 종료 인덱스를 잡기 위한 기준점 생성
        // DB 상의 인덱스가 아니라, List 상의 인덱스를 기준으로 자르기
        List<User> usersPageContent = users.subList(start, end);

        // Page 객체 생성 및 반환
        // Page 는 인터페이스라 구현체 형태인 PageImpl 사용
        return new PageImpl<>(usersPageContent, pageable, users.size());
    }

    public Optional<User> getUserById(long id) {
        return userRepository.findById(id);
    }

    @Transactional
    public User getUserWithFetchedOrders() {
        // 1) user 객체 조회, Lazy 필드 조회 없음
        Optional<User> userOptional = userRepository.findFirstByOrderByIdDesc();
//        Optional<User> userOptional = userRepository.findById(98L);
        assert userOptional.isPresent();
        User user = userOptional.get();
        // Lazy Loading 방식의 데이터 Fetch 수행
        // 메인 엔티티 로드 후, 추가 쿼리를 수동 실행
        Hibernate.initialize(user.getOrders());  // 부가 쿼리를 객체 관계에 맞춰 초기화 수행

        return user;
    }

    @Transactional
    public List<User> getUserListWithFetchedOrders() {
        // 1) user 리스트 조회, Lazy 필드 후속 조회
        List<User> users = userRepository.findAll();
        assert !users.isEmpty();
        for (User user : users) {
            // Lazy Loading 방식의 데이터 Fetch 수행
            // 메인 엔티티 로드 후, + N 추가 쿼리를 수동 실행
            Hibernate.initialize(user.getOrders());
        }
        return users;
    }

    private final EntityManager entityManager;

    @Transactional
    public List<User> getUserWithJoinFetchedCartsOrders() {
        String jpqlJoinFetchQuery =
                "SELECT DISTINCT u " +
                        "FROM User u " +
                        "LEFT JOIN FETCH u.carts ";    // User 객체와 orders 객체를 JOIN FETCH 수행
        List<User> usersWithCartsOrders =
            entityManager.createQuery(jpqlJoinFetchQuery, User.class).getResultList();
        for (User user : usersWithCartsOrders) {
            Hibernate.initialize(user.getOrders());
        }
        return usersWithCartsOrders;
    }
}
