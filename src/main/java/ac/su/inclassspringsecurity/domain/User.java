package ac.su.inclassspringsecurity.domain;

import ac.su.inclassspringsecurity.constant.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

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

//    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
//    @JsonIgnore     // JsonMapper 가 있는 Jackson 라이브러리를 사용할 때, 무한 루프를 방지하기 위해 사용
    private List<Cart> carts;

//    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Order> orders;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", Role='" + role + '\'' +
//                ", Cart=" + cart +
//                (carts != null ? ", cart=" + carts : "") +
                (orders != null ? ", orders=" + orders : "") +
                '}';
    }
}