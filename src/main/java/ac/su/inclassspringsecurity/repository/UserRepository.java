package ac.su.inclassspringsecurity.repository;

import ac.su.inclassspringsecurity.domain.User;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
//    Optional<User> findByUsername(String username);
    List<User> findByUsername(String username);

    // 증복 유저 체크를 위한 기본 단위(username, email) 준비
    boolean existsByUsername(String username);
    Optional<User> findByEmail(String email);
    // 로그인 인증 시 유저 데이터 조회 가능(password, role)

    boolean existsByEmail(String email);    // 로그인 인증시 유저 존재 여부 확인
}
