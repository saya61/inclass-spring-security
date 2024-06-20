package ac.su.inclassspringsecurity.domain;

import ac.su.inclassspringsecurity.constant.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Entity
@Getter @Setter
@Table(name = "orders") // oder 는 예약어이므로 orders 로 테이블명 설정
public class Order {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    @Enumerated(
//            EnumType.ORDINAL - 기본
            EnumType.STRING
    )
    private OrderStatus status;

    @Column(nullable = false)
    private String createdAt;

    @Column(nullable = false)
    private String updatedAt;

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", user=" + user.getId() +
                ", status=" + status +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}
