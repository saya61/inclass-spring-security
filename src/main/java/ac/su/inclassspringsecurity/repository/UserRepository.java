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
    boolean existsByEmail(String email);
}
