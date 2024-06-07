package ac.su.inclassspringsecurity.domain;

import ac.su.inclassspringsecurity.constant.UserRole;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@Table(name = "app_user")  // 테이블명은 예약어를 피하기 위해서 단순 user 로 하지 않기
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Column 은 상세한 제어를 위한 어노테이션 사용.
    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private UserRole role;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", Role=" + role +
                '}';
    }
}